package com.seok.rxcryptocurrencyprac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.seok.rxcryptocurrencyprac.bittrex.BittrexService;
import com.seok.rxcryptocurrencyprac.bittrex.RetrofitBittrexServiceFactory;
import com.seok.rxcryptocurrencyprac.setting.Settings;
import com.seok.rxcryptocurrencyprac.setting.SettingsActivity;
import com.seok.rxcryptocurrencyprac.storio.StorIOFactory;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.seok.rxcryptocurrencyprac.storio.LocalItemPersistenceHandlingTransformer.addLocalItemPersistenceHandling;
import static com.seok.rxcryptocurrencyprac.transformer.FileCacheFlowableTransformer.cacheToLocalFileNamed;
import static com.seok.rxcryptocurrencyprac.transformer.LoggerTransformer.debugLog;
import static com.seok.rxcryptocurrencyprac.transformer.TimingFlowableTransformer.timeItems;

public class MainActivity extends RxAppCompatActivity { // BaseActivity 와 같이 쓸때는 RxAppCompatActivity 에 있는 내용을 커스텀 해주면 된다.

    @BindView(R.id.hello_world_salute)
    TextView helloText;

    @BindView(R.id.no_data_available)
    TextView noDataAvailableView;

    @BindView(R.id.stock_updates_recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private StockDataAdapter stockDataAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxJavaPlugins.setErrorHandler(ErrorHandler.getInstance());

        ButterKnife.bind(this);

        Observable.just("Please use this app responsibly!")
                .subscribe(s -> helloText.setText(s));

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stockDataAdapter = new StockDataAdapter();
        recyclerView.setAdapter(stockDataAdapter);

        BittrexService service = new RetrofitBittrexServiceFactory().create();

        // twitter 설정값
        final Configuration configuration = new ConfigurationBuilder()
                .setDebugEnabled(BuildConfig.DEBUG)
                .setOAuthConsumerKey("qdZC5BrxBmge4WdFRqqjmA2Nh")
                .setOAuthConsumerSecret("pg0Lw8RkOeIUgCyUP4trL3VMnOZf35NiTjqRah3tUA0grfZol2")
                .setOAuthAccessToken("1057900513688080384-Q5PNX5g355NYNUNEfQjnT4JxT7eVkP")
                .setOAuthAccessTokenSecret("qmEYSQRPwTBXuUDsp8dO7hywhH12Wm2uZW8rNF4OWOgG7")
                .build();

        final Settings settings = Settings.get(this.getApplicationContext());

        Flowable.merge(
                    settings.getMonitoredSymbols().toFlowable(BackpressureStrategy.LATEST)
                        .switchMap(symbols -> createFinancialStockUpdateFlowable(service)), // 사실 심볼에 따라서 다르게 가져오게 할 수 있는데... 예시 api 는 아님 ㅜㅜ
                    settings.getMonitoredKeywords().toFlowable(BackpressureStrategy.LATEST)
                        .switchMap(keywords -> {
                            if(keywords.isEmpty()) {
                                return Flowable.empty();
                            }

                            String[] trackingKeywords = keywords.toArray(new String[0]);

                            // filter query
                            final FilterQuery filterQuery = new FilterQuery()
                                    .track(trackingKeywords) // 트윗에서 찾고 있는 키워드를 지정
                                    .language("en"); // 트윗을 영어로만 제한

                            return createTweetStockUpdateFlowable(configuration, trackingKeywords, filterQuery);
                        })
                )
                .groupBy(StockUpdate::getStockSymbol) // 해당 그룹에 같은 symbol 을 가진 StockUpdate 를 포함할 옵서버블 그룹을 생성한다.
                .flatMap(Flowable::distinctUntilChanged) // 옵서버블을 벗기기 전에 각 그룹이 이전값과 다른 경우에만 아이템을 반환한다.
                .compose(bindToLifecycle()) // dispose 시켜준다. (trello rxLifeCycle)
                .subscribeOn(Schedulers.io())
                .doOnError(ErrorHandler.getInstance())
                .compose(addUiErrorHandling())
                .compose(addLocalItemPersistenceHandling(this))
                .doOnNext(update -> log(update))
                .observeOn(AndroidSchedulers.mainThread())
//                .filter(stockUpdate -> !stockDataAdapter.contains(stockUpdate)) // 존재하지 않는 새 항목만 filtering 한다. => groupby 와 distinct 로 변경함(복잡도 때문)
                .subscribe(stockUpdate -> {
                    Log.d("App", "New update " + stockUpdate.getStockSymbol());
                    noDataAvailableView.setVisibility(View.GONE);
                    stockDataAdapter.add(stockUpdate);
                    recyclerView.scrollToPosition(0); // 리사이클러뷰의 가장 위 상단으로 옮긴다.
                }, error -> {
                    Log.d("Error", error.toString());
                    if (stockDataAdapter.getItemCount() == 0) {
                        noDataAvailableView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private Flowable<StockUpdate> createFinancialStockUpdateFlowable(BittrexService service) {
        return Flowable.interval(0, 5, TimeUnit.SECONDS)
                .flatMapSingle(aLong -> { // single 을 flowable 로 변경
                    Log.e("확인 : ", aLong + "");
                    return service.getSummary();
                })
                .map(bittrexResult -> bittrexResult.getResult())
                .flatMap(marketSummaries -> Flowable.fromIterable(marketSummaries))
                .map(summary -> StockUpdate.create(summary));
    }

    private Flowable<StockUpdate> createTweetStockUpdateFlowable(Configuration configuration, String[] trackingKeywords, FilterQuery filterQuery) {
        return observeTwitterStream(configuration, filterQuery)
                .sample(700, TimeUnit.MILLISECONDS) // 트위터 상태 업데이트가 빠르기 때문에 sample 메서드로 속도의 비율을 줄인다. (하지만 flush 하게 되므로 데이터를 잃게 되므로 주의)
                .map(StockUpdate::create)
                .flatMapMaybe(skipTweetsThatDoNotContainKeywords(trackingKeywords));// 내용이 일치하면 필터링한다.
//              .filter(containsAnyOfKeywords(trackingKeywords)) // 사실 이렇게 단순하게 필터할수도 있다. (flatMapMaybe 부분)
    }

    // twitter status 업데이트를 옵서버블로 방출한다.
    private Flowable<Status> observeTwitterStream(Configuration configuration, FilterQuery filterQuery) {
        return Flowable.create(emitter -> {
            final TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();

            // 옵서버블이 제거될 때 TitterStream 이 종료되도록 설정
            emitter.setCancellable(() -> twitterStream.shutdown());

            StatusListener listener = new StatusListener() {
                @Override
                public void onStatus(Status status) { // 상태 업데이트를 캡쳐
                    emitter.onNext(status); // Observable 로 status 를 emit
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {

                }

                @Override
                public void onStallWarning(StallWarning warning) {

                }

                @Override
                public void onException(Exception ex) { // 예외를 처리
                    // 에러를 observable 로 방출
                    emitter.onError(ex);
                }
            };

            twitterStream.addListener(listener);
            twitterStream.filter(filterQuery);
        }, BackpressureStrategy.LATEST);
    }

    @NotNull
    private Function<StockUpdate, MaybeSource<? extends StockUpdate>> skipTweetsThatDoNotContainKeywords(String[] trackingKeywords) {
        return update -> Flowable.fromArray(trackingKeywords) // 키워드를 스트림으로 변환
                .filter(keyword -> update
                        .getTwitterStatus()
                        .toLowerCase()
                        .contains(keyword.toLowerCase())) // update 와 동일한지 필터링
                .map(keyword -> update) // 다시 update 항목으로 변환
                .firstElement(); // 내용과 일치하는 키워드가 여러개라면 처음 하나만 반환되도록 설정
    }

    @NotNull
    private Predicate<StockUpdate> containsAnyOfKeywords(String[] trackingKeywords) { // predicate 추출
        return stockUpdate -> {
            for (String keywords : trackingKeywords) {
                if (stockUpdate.getTwitterStatus().contains(keywords)) {
                    return true;
                }
            }
            return false;
        };
    }

    @NotNull
    private FlowableTransformer<StockUpdate, StockUpdate> addUiErrorHandling() {
        return upstream -> upstream
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(MainActivity.this::showToastErrorNotification)
                .observeOn(Schedulers.io());
    }

    // 메서드 내에서 추가적인 작업이 가능하다.(유연하다)
    // 하지만 매번 새로운 람다를 생성한다는 단점이 있다.
    @NotNull
    private Consumer<Throwable> showToastErrorNotification() { // consumer 추출
        return error -> {
            log("doOnError", "error");
            Toast.makeText(this, "We couldn't reach internet - falling back to local data", Toast.LENGTH_SHORT).show();
        };
    }

    // 구지 Consumer 로 안하고 메서드 참조해서 사용해도 된다.
    // 매번 새로운 람다를 불필요하게 생성하지는 않지만 유연하지 않다.
    @NotNull
    private void showToastErrorNotification(Throwable error) {
        log("doOnError", "error");
        Toast.makeText(this, "We couldn't reach internet - falling back to local data", Toast.LENGTH_SHORT).show();
    }

    private void log(Throwable throwable) {
        Log.e("APP", "Error on " + Thread.currentThread().getName() + ":", throwable);
    }

    private void log(String stage, Throwable throwable) {
        Log.e("APP", stage + ":" + Thread.currentThread().getName() + ": error", throwable);
    }

    private void log(String stage, String item) {
        Log.d("APP", stage + ":" + Thread.currentThread().getName() + ":" + item);
    }

    private void log(String stage, int item) {
        Log.d("APP", stage + ":" + Thread.currentThread().getName() + ":" + item);
    }

    private void log(String stage, long item) {
        Log.d("APP", stage + ":" + Thread.currentThread().getName() + ":" + item);
    }

    private void log(String stage) {
        Log.d("APP", stage + ":" + Thread.currentThread().getName());
    }

    private void log(StockUpdate update) {
        Log.d("APP", Thread.currentThread().getName() + ":" + update.toString());
    }

    private void log(long value) {
        Log.d("APP", Thread.currentThread().getName() + ":" + value);
    }

    // ======================================= 아래는 그냥 sample

    // StorIO 에서 삭제 기능 사용하려면 아래와 같이 하면 된다.
    private void demo10() {
        StockUpdate stockUpdate = null;
        StorIOFactory.get(this)
                .delete()
                .object(stockUpdate)
                .prepare()
                .asRxCompletable()
                .subscribe();
    }

    // zip 사용 예시
    private void demo7() {
        Observable.zip(
                Observable.just("One", "Two", "Three")
                        .doOnDispose(() -> log("just", "doOnDispose"))
                        .doOnTerminate(() -> log("just", "doOnTerminate")),
                Observable.interval(1, TimeUnit.SECONDS)
                        .doOnDispose(() -> log("interval", "doOnDispose"))
                        .doOnTerminate(() -> log("interval", "doOnTerminate")),
                (number, interval) -> number + "-" + interval
        )
                .doOnDispose(() -> log("zip", "doOnDispose"))
                .doOnTerminate(() -> log("zip", "doOnTerminate"))
                .subscribe(e -> log(e));
    }

    // custom class 를 활용하여 유저 및 토큰 가져오기 예시
    private void demo5() {
        class User {
            String userId;

            public User(String userId) {
                this.userId = userId;
            }
        }

        class UserCredentials {
            public final User user;
            public final String accessToken;

            public UserCredentials(User user, String accessToken) {
                this.user = user;
                this.accessToken = accessToken;
            }
        }

        Observable.just(new User("1"), new User("2"), new User("3"))
                .map(user -> new UserCredentials(user, "accessToken"))
                .subscribe(credentials -> log(credentials.user.userId, credentials.accessToken));

    }

    // 아래는 다른 구독이 날짜를 덮어쓰거나 잘못될 같은 변수를 사용하기 때문에 중단된다.
    private void timingExample2() {
        final Flowable<Long> flowable = Flowable.interval(4, TimeUnit.SECONDS)
                .compose(timeItems((seconds) -> {
                    Log.d("APP", "Seconds passed since the start: " + seconds);
                }));

        flowable.subscribe(this::log);
        flowable.subscribe(this::log);

    }

    // timingTransformer 예시
    private void timingExample() {
        Flowable.interval(4, TimeUnit.SECONDS)
                .compose(timeItems((seconds) -> {
                    Log.d("APP", "Seconds passed since the start: " + seconds);
                }))
                .subscribe(this::log);

    }

    // 캐싱 저장 예시
    private void cachingExample() {
        Flowable.just("1")
                .compose(cacheToLocalFileNamed("test", this))
                .subscribe(this::log);

    }

    // debug logger 사용 예시 (옵서버블이 종료를 시작하는 위치, 어떤 항복이 흐름의 어디에 도착하는지 등 확인 가능)
    private void loggerExample() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .compose(debugLog("afterInterval")) // 로그
                .flatMap(v -> Flowable.just("items"))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(debugLog("afterFlatMap")) // 로그
                .subscribe();
    }
}
