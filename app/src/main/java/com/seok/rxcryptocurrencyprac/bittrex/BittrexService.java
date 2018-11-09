package com.seok.rxcryptocurrencyprac.bittrex;

import com.seok.rxcryptocurrencyprac.bittrex.json.BittrexResult;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BittrexService {

    @GET("public/getmarketsummaries")
    Single<BittrexResult> getSummary();

//    @GET("public/getticker/{market}")
//    Single<BittrexResult> getTicker(
//            @Query("market") String market
//    );
}
