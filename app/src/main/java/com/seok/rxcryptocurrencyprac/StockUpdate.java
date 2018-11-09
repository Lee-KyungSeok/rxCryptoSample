package com.seok.rxcryptocurrencyprac;

import com.seok.rxcryptocurrencyprac.bittrex.json.MarketSummary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import twitter4j.Status;

public class StockUpdate implements Serializable {
    private final String stockSymbol;
    private final BigDecimal price;
    private final String date;
    private final String twitterStatus;
    private Integer id;

    public StockUpdate(String stockSymbol, BigDecimal price, String date, String twitterStatus) {

        if (stockSymbol == null) {
            stockSymbol = "";
        }

        if (twitterStatus == null) {
            twitterStatus = "";
        }

        this.stockSymbol = stockSymbol;
        this.price = price;
        this.date = date;
        this.twitterStatus = twitterStatus;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }

    public String getTwitterStatus() {
        return twitterStatus;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static StockUpdate create(MarketSummary summary) {
        return new StockUpdate(summary.getMarketName(), summary.getLast(), summary.getTimeStamp(), "");
    }

    public static StockUpdate create(Status status) {
        return new StockUpdate("", BigDecimal.ZERO, transFormat.format(status.getCreatedAt()), status.getText());
    }

    public boolean isTwitterStatusUpdate() {
        return !twitterStatus.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockUpdate that = (StockUpdate) o;
        return Objects.equals(stockSymbol, that.stockSymbol) &&
                Objects.equals(price, that.price) &&
                Objects.equals(date, that.date) &&
                Objects.equals(twitterStatus, that.twitterStatus) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(stockSymbol, price, date, twitterStatus, id);
    }
}
