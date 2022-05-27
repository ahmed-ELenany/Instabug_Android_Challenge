package com.instabug.androidChallenge.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.instabug.androidChallenge.R;
import com.instabug.androidChallenge.model.Words;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.viewHolder> {

    private final Context context;
    List<Words> dataList;


    public WordsAdapter(Context c, List<Words> dataList) {
        this.context = c;
        this.dataList = dataList;
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_row, parent, false);

        return new viewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        holder.tvWord.setText(dataList.get(holder.getAdapterPosition()).getWord());
        holder.tvCount.setText(dataList.get(holder.getAdapterPosition()).getCount()+"");

    }

    //**********************************************************************************************************
    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
 TextView tvWord,tvCount;
        public viewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvCount = itemView.findViewById(R.id.tvCount);

        }
    }

}
