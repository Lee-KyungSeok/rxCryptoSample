package com.seok.rxcryptocurrencyprac;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockDataAdapter extends RecyclerView.Adapter<StockUpdateViewHolder> {

    private final List<StockUpdate> data = new ArrayList<>();

    @NonNull
    @Override
    public StockUpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_update, parent, false);
        StockUpdateViewHolder holder = new StockUpdateViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StockUpdateViewHolder holder, int position) {
        StockUpdate stockUpdate = data.get(position);
        holder.setStockSymbol(stockUpdate.getStockSymbol());
        holder.setPrice(stockUpdate.getPrice());
        holder.setDate(stockUpdate.getDate());
        holder.setTwitterStatus(stockUpdate.getTwitterStatus());
        holder.setIsStatusUpdate(stockUpdate.isTwitterStatusUpdate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(StockUpdate newStockUpdate) {
        this.data.add(0, newStockUpdate);
        notifyItemInserted(0);
    }

    public boolean contains(StockUpdate newStockUpdate) {

        for(StockUpdate stockUpdate: data) {
            if(stockUpdate.getStockSymbol().equals(newStockUpdate.getStockSymbol())) {
                if(stockUpdate.getPrice().equals(newStockUpdate.getPrice()) // 가격이 동일한지 확인
                        && stockUpdate.getTwitterStatus().equals(newStockUpdate.getTwitterStatus())) { // 트윗인 경우 트윗 상태를 확인
                    return true;
                }
                break;
            }
        }

        return false;
    }
}
