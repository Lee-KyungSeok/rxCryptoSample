package com.seok.rxcryptocurrencyprac.bittrex.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class MarketSummary {

    @Expose
    @SerializedName("MarketName")
    private String marketName;

    @Expose
    @SerializedName("OpenSellOrders")
    private int openSellOrders;

    @Expose
    @SerializedName("OpenBuyOrders")
    private int openBuyOrders;

    @Expose
    @SerializedName("High")
    private BigDecimal high;

    @Expose
    @SerializedName("Low")
    private BigDecimal low;

    @Expose
    @SerializedName("Volume")
    private BigDecimal volume;

    @Expose
    @SerializedName("BaseVolume")
    private BigDecimal baseVolume;

    @Expose
    @SerializedName("Last")
    private BigDecimal last;

    @Expose
    @SerializedName("PrevDay")
    private BigDecimal prevDay;

    @Expose
    @SerializedName("Bid")
    private BigDecimal bid;

    @Expose
    @SerializedName("Ask")
    private BigDecimal ask;

    @Expose
    @SerializedName("TimeStamp")
    private String timeStamp;

    @Expose
    @SerializedName("Created")
    private String created;

    @Expose
    @SerializedName("DisplayMarketName")
    private String displayMarketName;

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getOpenSellOrders() {
        return openSellOrders;
    }

    public void setOpenSellOrders(int openSellOrders) {
        this.openSellOrders = openSellOrders;
    }

    public int getOpenBuyOrders() {
        return openBuyOrders;
    }

    public void setOpenBuyOrders(int openBuyOrders) {
        this.openBuyOrders = openBuyOrders;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getPrevDay() {
        return prevDay;
    }

    public void setPrevDay(BigDecimal prevDay) {
        this.prevDay = prevDay;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDisplayMarketName() {
        return displayMarketName;
    }

    public void setDisplayMarketName(String displayMarketName) {
        this.displayMarketName = displayMarketName;
    }

    @Override
    public String toString() {
        return "MarketSummaryee{" +
                "marketName=" + marketName +
                ", openSellOrders=" + openSellOrders +
                ", openBuyOrders=" + openBuyOrders +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", baseVolume=" + baseVolume +
                ", last=" + last +
                ", prevDay=" + prevDay +
                ", bid=" + bid +
                ", ask=" + ask +
                ", timeStamp=" + timeStamp +
                ", created=" + created +
//                ", displayMarketName='" + displayMarketName + '\'' +
                '}';
    }
}
