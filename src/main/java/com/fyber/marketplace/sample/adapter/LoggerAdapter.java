/*
 * Copyright 2020. Fyber N.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.fyber.marketplace.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fyber.marketplace.sample.R;
import com.fyber.marketplace.sample.model.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility. helps display ad lifecycle on screen
 * RecyclerView adapter for logs
 */
public class LoggerAdapter
        extends RecyclerView.Adapter<LoggerAdapter.LogViewHolder> {
    
    private final List<Log> mLogList = new ArrayList<>();
    private final LayoutInflater mInflater;
    
    public LoggerAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
    }
    
    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogViewHolder(mInflater.inflate(R.layout.recyler_view_layout, parent, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = mLogList.get(position);
        holder.mRow.setText(String.format("%s - %s - %s", log.getTime(), log.getSpotId(), log.getMessage()));
    }
    
    @Override
    public int getItemCount() {
        return mLogList.size();
    }
    
    public void addLog(Log log) {
        mLogList.add(log);
        notifyDataSetChanged();
    }
    
    public void clearLog() {
        mLogList.clear();
        notifyDataSetChanged();
    }
    
    public static class LogViewHolder
            extends RecyclerView.ViewHolder {
        
        TextView mRow;
        
        LogViewHolder(View itemView) {
            super(itemView);
            mRow = itemView.findViewById(R.id.row);
        }
        
    }
    
}
