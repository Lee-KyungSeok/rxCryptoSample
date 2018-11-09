package com.seok.rxcryptocurrencyprac.bittrex.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BittrexResult
{
    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("result")
    private List<MarketSummary> result = new ArrayList<>();

    @Expose
    @SerializedName("success")
    private boolean success;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public List<MarketSummary> getResult() {
        return result;
    }

    public void setResult(List<MarketSummary> result) {
        this.result = result;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "BittrexResult{" +
                "message='" + message + '\'' +
                ", result=" + result +
                ", success=" + success +
                '}';
    }
}
