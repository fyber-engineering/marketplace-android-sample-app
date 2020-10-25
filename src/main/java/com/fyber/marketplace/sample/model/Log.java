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

package com.fyber.marketplace.sample.model;

/**
 * Utility. helps display ad lifecycle on screen
 * This is a model for a log being displayed on screen
 */
public class Log {
    
    private final String mTime;
    private final String mSpotId;
    private final String mMessage;
    
    public Log(String time, String spotId, String message) {
        super();
        mTime = time;
        mSpotId = spotId;
        mMessage = message;
    }
    
    public String getTime() {
        return mTime;
    }
    
    public String getSpotId() {
        return mSpotId;
    }
    
    @Override
    public String toString() {
        return String.format("Time - %s,Spot id - %s, msg - %s", mTime, mSpotId, mMessage);
    }
    
    public String getMessage() {
        return mMessage;
    }
    
}
