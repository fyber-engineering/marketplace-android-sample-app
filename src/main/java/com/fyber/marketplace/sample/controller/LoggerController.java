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

package com.fyber.marketplace.sample.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fyber.marketplace.sample.adapter.LoggerAdapter;
import com.fyber.marketplace.sample.model.ExpandMode;
import com.fyber.marketplace.sample.model.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility. helps display ad lifecycle on screen
 * Controlling of the display of log messages.
 */
public class LoggerController {
    
    private final RecyclerView mRecyclerView;
    private final LoggerAdapter mAdapter;
    private final TextView mHeaderTextView;
    private ExpandMode mCurrentExpandMode = ExpandMode.COLLAPSE;
    private final LinearLayout mContainer;
    
    public LoggerController(Context context, RecyclerView recyclerView, TextView textView, LinearLayout container) {
        super();
        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mHeaderTextView = textView;
        recyclerView.setHasFixedSize(true);
        mAdapter = new LoggerAdapter(context);
        recyclerView.setAdapter(mAdapter);
        mContainer = container;
        initTextViewClicks(context);
    }
    
    public void addLogMessage(String spotId, String msg) {
        Log log = new Log(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()), spotId, msg);
        android.util.Log.d("Fyber-Debug", log.toString());
        mAdapter.addLog(log);
        
        if (mCurrentExpandMode == ExpandMode.COLLAPSE) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }
    
    private void initTextViewClicks(final Context context) {
        mHeaderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLogViewSize(context);
            }
        });
        mHeaderTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAdapter.clearLog();
                changeLogViewSize(context);
                return true;
            }
        });
    }
    
    private int convertPxToDp(Context context, int px) {
        return (int) (px * context.getResources().getDisplayMetrics().density + 0.5f);
    }
    
    private void changeLogViewSize(Context context) {
        mCurrentExpandMode = mCurrentExpandMode == ExpandMode.COLLAPSE ? ExpandMode.EXPAND : ExpandMode.COLLAPSE;
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                         convertPxToDp(context, mCurrentExpandMode ==
                                                                                                ExpandMode.COLLAPSE ?
                                                                                                80 : 250));
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mContainer.setLayoutParams(rl);
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }
    
}
