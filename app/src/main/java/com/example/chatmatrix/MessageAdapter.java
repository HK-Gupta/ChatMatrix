package com.example.chatmatrix;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    class SenderViewHolder extends RecyclerView.ViewHolder {
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
