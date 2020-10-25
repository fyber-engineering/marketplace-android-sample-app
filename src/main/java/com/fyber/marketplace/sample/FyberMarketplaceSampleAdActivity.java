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

package com.fyber.marketplace.sample;

import com.fyber.inneractive.sdk.external.ImpressionData;
import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListenerWithImpressionData;
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.fyber.inneractive.sdk.external.InneractiveFullScreenAdRewardedListener;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenAdEventsListenerWithImpressionData;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenVideoContentController;
import com.fyber.inneractive.sdk.external.InneractiveUnitController;
import com.fyber.inneractive.sdk.external.InneractiveUserConfig;
import com.fyber.inneractive.sdk.external.OnFyberMarketplaceInitializedListener;
import com.fyber.inneractive.sdk.external.OnGlobalImpressionDataListener;
import com.fyber.inneractive.sdk.external.VideoContentListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.fyber.marketplace.sample.controller.LoggerController;

/**
 * Main sample application activity
 */
public class FyberMarketplaceSampleAdActivity
        extends Activity {
    
    private final static String SAMPLE_APP_ID = "102960";
    private final static String SAMPLE_BANNER_SPOT_ID = "150942";
    private final static String SAMPLE_RECTANGLE_SPOT_ID = "150943";
    private final static String SAMPLE_INTERSTITIAL_SPOT_ID = "150946";
    
    /**
     * The banner spot
     */
    private InneractiveAdSpot mBannerSpot;
    /**
     * The interstitial spot
     */
    private InneractiveAdSpot mInterstitialSpot;
    /**
     * The current app id in use
     */
    private String mCurrentAppId;
    /**
     * Flag to determine if the interstitial is not ready or expired
     */
    private boolean mInterstitialAdLoaded = false;
    
    /**
     * Banner's app id input field
     */
    private EditText mEditTextBannerAppId;
    /**
     * app id input field
     */
    private EditText mEditTextInterstitialAppId;
    /**
     * Banner's spot id input field
     */
    private EditText mEditTextBannerSpotId;
    /**
     * Interstitial spot id input field
     */
    private EditText mEditTextInterstitialSpotId;
    
    /**
     * The logger
     */
    protected LoggerController mLoggerController;
    
    
    /**
     * Activity's entry point. Good place to initialize the Fyber Marketplace
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        // Update the window title with the SDK version
        setTitle(getTitleAppName() + " v" + InneractiveAdManager.getVersion());
        initializeUI();
        configureFyberMarketplaceSDK();
        
        
    }
    
    private void configureFyberMarketplaceSDK() {
        initFyberMarketplaceSdk(SAMPLE_APP_ID);
        InneractiveAdManager.setImpressionDataListener(new OnGlobalImpressionDataListener() {
            @Override
            public void onImpression(String spotId, String unitId, ImpressionData impressionData) {
                mLoggerController.addLogMessage(spotId, String.format("OnGlobalImpressionDataListener Impression data - %s", impressionData.toString()));
            }
        });
    }
    
    
    /**
     * populate tab control
     */
    void initializeUI() {
        TabHost tabs = findViewById(R.id.tabhost);
        tabs.setup();
        
        // First tab is used for dynamically created banners
        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Banner");
        tabs.addTab(spec);
        
        // Third tab is used for interstitial ads
        spec = tabs.newTabSpec("Interstitial");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Fullscreen");
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
        
        // Used for caching
        mEditTextBannerAppId = findViewById(R.id.bannerAppId);
        mEditTextBannerSpotId = findViewById(R.id.bannerSpotId);
        mEditTextInterstitialAppId = findViewById(R.id.interstitialAppId);
        mEditTextInterstitialSpotId = findViewById(R.id.interstitialSpotId);
        
        // Update the window title with the SDK version
        updateTitle(getTitleAppName() + " v" + InneractiveAdManager.getVersion());
        mLoggerController =
                new LoggerController(getApplicationContext(), (RecyclerView) findViewById(R.id.log_recycler_view),
                                     (TextView) findViewById(R.id.tv_header_log),
                                     (LinearLayout) findViewById(R.id.recycler_view_container));
    }
    
    /**
     * Helper method for initializing the SDK with the given app id
     *
     * @param appId The app id provided through the Fyber marketplace console
     */
    protected void initFyberMarketplaceSdk(String appId) {
        
        // Get app id from input field
        if (TextUtils.isEmpty(appId)) {
            appId = SAMPLE_APP_ID;
        }
        
        boolean appIdHasChanged = !TextUtils.equals(mCurrentAppId, appId);
        if (!InneractiveAdManager.wasInitialized() || appIdHasChanged) {
            
            //The following is to ease testing purposes. there is no need to destroy the SDK for every request.
            //Please refer to the documentation regarding when to destroy the SDK.
            if (appIdHasChanged && InneractiveAdManager.wasInitialized()) {
                InneractiveAdManager.destroy();
            }
            
            InneractiveAdManager.initialize(this, appId, new OnFyberMarketplaceInitializedListener() {
                @Override
                public void onFyberMarketplaceInitialized(FyberInitStatus status) {
                    mLoggerController.addLogMessage("", "Initialized status: " + status.toString());
                }
            });
            mCurrentAppId = appId;
            
            /*
             * TODO
             * EU's General Data Protection Regulation (GDPR) consent - usage example
             * TODO
             *
             *
             * InneractiveAdManager.setGdprConsent(wasConsentGiven);
             *
             */
            
        }
        
    }
    
    @Override
    protected void onDestroy() {
        // Call InneractiveAdManager::destroy only if this activity is actually finishing, and not during activity restart due to rotation etc.
        if (isFinishing()) {
            destroyDynamicAds();
            // This will destroy the entire library, including any ads in use
            InneractiveAdManager.destroy();
        }
        super.onDestroy();
    }
    
    /**
     * Example how to initialize the Ad with request parameters
     * <br>Improves the ad targeting
     *
     * @param adRequest ad request, which will be used for requestAd
     */
    protected void initializeRequestOptionalParameters(InneractiveAdRequest adRequest) {
        // Add user related data
        adRequest.setUserParams(new InneractiveUserConfig()
                                        .setGender(InneractiveUserConfig.Gender.FEMALE)
                                        .setZipCode("94401")
                                        .setAge(35));
        
        // Add keywords. Separated by a comma
        adRequest.setKeywords("pop,rock,music");
    }
    
    private void destroyDynamicAds() {
        if (mBannerSpot != null) {
            mBannerSpot.destroy();
            mBannerSpot = null;
        }
        
        if (mInterstitialSpot != null) {
            mInterstitialSpot.destroy();
            mInterstitialSpot = null;
        }
    }
    
    /**
     * Called when you choose to show a banner within the dynamic tab
     *
     * @param view the clicked radio button
     */
    public void onLoadBannerClicked(final View view) {
        //in this sample code, we call initialize again so we can pick up app id changes from the UI.
        initFyberMarketplaceSdk(
                mEditTextBannerAppId != null ? mEditTextBannerAppId.getText().toString() : SAMPLE_APP_ID);
        String spotId = SAMPLE_BANNER_SPOT_ID;
        // Get spot id from input field
        if (mEditTextBannerSpotId != null) {
            spotId = mEditTextBannerSpotId.getText().toString();
            if (TextUtils.isEmpty(spotId)) {
                mLoggerController.addLogMessage("",
                                                "You didn't set your specific spot id! The sample spot id will be used. make sure that you're using your specific spot id for tracking your traffic!");
                spotId = SAMPLE_BANNER_SPOT_ID;
            }
            mLoggerController.addLogMessage(spotId, "Requesting banner");
        }
        loadBanner(spotId, view);
    }
    
    /**
     * Called when you choose to show a banner within the dynamic tab
     *
     * @param view the clicked radio button
     */
    public void onLoadRectangleClicked(final View view) {
        //in this sample code, we call initialize again so we can pick up app id changes from the UI.
        initFyberMarketplaceSdk(
                mEditTextBannerAppId != null ? mEditTextBannerAppId.getText().toString() : SAMPLE_APP_ID);
        String spotId = SAMPLE_RECTANGLE_SPOT_ID;
        // Get spot id from input field
        if (mEditTextBannerSpotId != null) {
            spotId = mEditTextBannerSpotId.getText().toString();
            if (TextUtils.isEmpty(spotId)) {
                mLoggerController.addLogMessage("",
                                                "You didn't set your specific spot id! The sample spot id will be used. make sure that you're using your specific spot id for tracking your traffic!");
                spotId = SAMPLE_RECTANGLE_SPOT_ID;
            }
            mLoggerController.addLogMessage(spotId, "Requesting rectangle");
        }
        loadRectangle(spotId, view);
    }
    
    /**
     * Called when the LoadInterstitial button is clicked
     *
     * @param view button
     */
    public void onLoadInterstitialClicked(final View view) {
        //in this sample code, we call initialize again so we can pick up app id changes from the UI.
        initFyberMarketplaceSdk(
                mEditTextInterstitialAppId != null ? mEditTextInterstitialAppId.getText().toString() : SAMPLE_APP_ID);
        String spotId = SAMPLE_INTERSTITIAL_SPOT_ID;
        // Get spot id from input field
        if (mEditTextInterstitialSpotId != null) {
            spotId = mEditTextInterstitialSpotId.getText().toString();
            if (TextUtils.isEmpty(spotId)) {
                mLoggerController.addLogMessage("",
                                                "You didn't set your specific spot id! The sample spot id will be used. make sure that you're using your specific spot id for tracking your traffic!");
                spotId = SAMPLE_INTERSTITIAL_SPOT_ID;
            }
            mLoggerController.addLogMessage(spotId, "Requesting interstitial");
        }
        loadInterstitial(spotId, view);
    }
    
    private void loadBanner(String spotId, View view) {
        InneractiveAdRequest request = initializeBannerSpot(spotId);
    
        mBannerSpot.setRequestListener(new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                if (adSpot != mBannerSpot) {
                    mLoggerController.addLogMessage("", "Wrong Banner Spot: Received - " + adSpot + ", Actual - " + mBannerSpot);
                    return;
                }
                final String spotId = adSpot.getCurrentProcessedRequest().getSpotId();
                ViewGroup layout = findViewById(R.id.ad_layout);
                InneractiveAdViewUnitController controller =
                        (InneractiveAdViewUnitController) mBannerSpot.getSelectedUnitController();
                controller.setEventsListener(new InneractiveAdViewEventsListenerWithImpressionData() {
                    @Override
                    public void onAdImpression(InneractiveAdSpot adSpot, ImpressionData impressionData) {
                        mLoggerController.addLogMessage(spotId, "onAdImpressionWithImpressionData");
                    }
                    
                    @Override
                    public void onAdImpression(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdImpression");
                    }
                    
                    @Override
                    public void onAdClicked(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdClicked");
                    }
                    
                    @Override
                    public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdWillCloseInternalBrowser");
                    }
                    
                    @Override
                    public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdWillOpenExternalApp");
                    }
                    
                    @Override
                    public void onAdEnteredErrorState(InneractiveAdSpot adSpot,
                                                      InneractiveUnitController.AdDisplayError error) {
                        
                        mLoggerController.addLogMessage(spotId, "onAdEnteredErrorState");
                    }
                    
                    @Override
                    public void onAdExpanded(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdExpanded");
                        
                    }
                    
                    @Override
                    public void onAdResized(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdResized");
                    }
                    
                    @Override
                    public void onAdCollapsed(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdCollapsed");
                        
                    }
                });
                
                controller.bindView(layout);
            }
            
            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                mLoggerController.addLogMessage(adSpot.getCurrentProcessedRequest().getSpotId(),
                                                "Failed loading banner! with error: " + errorCode);
            }
        });
        
        mBannerSpot.requestAd(request);
        hideKeyboard(view);
    }
    
    private InneractiveAdRequest initializeBannerSpot(String spotId) {
        if (mBannerSpot != null) {
            mBannerSpot.destroy();
        }
        
        mBannerSpot = InneractiveAdSpotManager.get().createSpot();
        InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
        mBannerSpot.addUnitController(controller);
        
        InneractiveAdRequest request = new InneractiveAdRequest(spotId);
        initializeRequestOptionalParameters(request);
        return request;
    }
    
    private void loadRectangle(final String spotId, View view) {
        // initialize rectangle spot
        InneractiveAdRequest request = initializeBannerSpot(spotId);
        
        mBannerSpot.setRequestListener(new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                if (adSpot != mBannerSpot) {
                    mLoggerController.addLogMessage("", "Wrong Banner Spot: Received - " + adSpot + ", Actual - " + mBannerSpot);
                    return;
                }
                
                ViewGroup layout = findViewById(R.id.ad_layout);
                InneractiveAdViewUnitController controller =
                        (InneractiveAdViewUnitController) mBannerSpot.getSelectedUnitController();
                controller.setEventsListener(new InneractiveAdViewEventsListener() {
                    @Override
                    public void onAdImpression(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdImpression");
                    }
                    
                    @Override
                    public void onAdClicked(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdClicked");
                    }
                    
                    @Override
                    public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdWillCloseInternalBrowser");
                    }
                    
                    @Override
                    public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdWillOpenExternalApp");
                    }
                    
                    @Override
                    public void onAdEnteredErrorState(InneractiveAdSpot adSpot,
                                                      InneractiveUnitController.AdDisplayError error) {
                        mLoggerController.addLogMessage(spotId, "onAdEnteredErrorState - " + error.getMessage());
                    }
                    
                    @Override
                    public void onAdExpanded(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdExpanded");
                    }
                    
                    @Override
                    public void onAdResized(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdResized");
                    }
                    
                    @Override
                    public void onAdCollapsed(InneractiveAdSpot adSpot) {
                        mLoggerController.addLogMessage(spotId, "onAdCollapsed");
                    }
                });
                
                controller.bindView(layout);
            }
            
            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                mLoggerController.addLogMessage(spotId, "Failed loading banner! with error: " + errorCode);
            }
        });
        
        mBannerSpot.requestAd(request);
        hideKeyboard(view);
    }
    
    private void loadInterstitial(final String spotId, View view) {
        if (mInterstitialSpot != null) {
            mInterstitialSpot.destroy();
            mInterstitialSpot = null;
        }
        
        
        // First create a spot
        mInterstitialSpot = InneractiveAdSpotManager.get().createSpot();
        
        // Now create a full screen unit controller
        InneractiveFullscreenUnitController fullscreenUnitController = new InneractiveFullscreenUnitController();
        
        fullscreenUnitController.setRewardedListener(new InneractiveFullScreenAdRewardedListener() {
            @Override
            public void onAdRewarded(InneractiveAdSpot adSpot) {
                mLoggerController.addLogMessage(spotId, "onAdRewarded!");
            }
            
        });
        
        // Configure the request
        InneractiveAdRequest request = new InneractiveAdRequest(spotId);
        initializeRequestOptionalParameters(request);
        
        // Add the unit controller to the spot
        mInterstitialSpot.addUnitController(fullscreenUnitController);
        mInterstitialAdLoaded = false;
        mInterstitialSpot.setRequestListener(new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                mLoggerController.addLogMessage(spotId, "Interstitial loaded successfully!");
                mInterstitialAdLoaded = true;
            }
            
            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                mLoggerController.addLogMessage(spotId, "Failed loading interstitial! with error: " + errorCode);
            }
        });
        
        mInterstitialSpot.requestAd(request);
        hideKeyboard(view);
        
    }
    
    /**
     * Called when the show interstitial button is clicked
     *
     * @param view button
     */
    public void onShowInterstitialAdClicked(View view) {
        if (mInterstitialSpot != null && mInterstitialSpot.isReady()) {
            InneractiveFullscreenUnitController fullscreenUnitController =
                    (InneractiveFullscreenUnitController) mInterstitialSpot.getSelectedUnitController();
            final String spotId = mInterstitialSpot.getRequestedSpotId();
            fullscreenUnitController.setEventsListener(new InneractiveFullscreenAdEventsListenerWithImpressionData() {
                @Override
                public void onAdImpression(InneractiveAdSpot adSpot, ImpressionData impressionData) {
                    mLoggerController.addLogMessage(spotId, "onAdImpressionWithImpressionData - " + impressionData);
                }
                
                @Override
                public void onAdImpression(InneractiveAdSpot adSpot) {
                    mLoggerController.addLogMessage(spotId, "onAdImpression");
                }
                
                @Override
                public void onAdDismissed(InneractiveAdSpot adSpot) {
                    mLoggerController.addLogMessage(spotId, "onAdDismissed");
                }
                
                
                @Override
                public void onAdClicked(InneractiveAdSpot adSpot) {
                    mLoggerController.addLogMessage(spotId, "onAdClicked");
                    
                }
                
                @Override
                public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                    mLoggerController.addLogMessage(spotId, "onAdWillOpenExternalApp");
                    
                }
                
                @Override
                public void onAdEnteredErrorState(InneractiveAdSpot adSpot,
                                                  InneractiveUnitController.AdDisplayError error) {
                    mLoggerController.addLogMessage(spotId, "onAdEnteredErrorState");
                    
                }
                
                @Override
                public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                    mLoggerController.addLogMessage(spotId, "onAdWillCloseInternalBrowser");
                    
                }
            });
            
            // Add video content controller, for controlling video ads
            InneractiveFullscreenVideoContentController videoContentController =
                    new InneractiveFullscreenVideoContentController();
            videoContentController.setEventsListener(new VideoContentListener() {
                @Override
                public void onProgress(int totalDurationInMsec, int positionInMsec) {
                    mLoggerController.addLogMessage(spotId, "Interstitial: Got video content progress: total time = " +
                                                            totalDurationInMsec + " position = " + positionInMsec);
                }
                
                @Override
                public void onCompleted() {
                    mLoggerController.addLogMessage(spotId, "Interstitial: Got video content completed event");
                }
                
                @Override
                public void onPlayerError() {
                    mLoggerController.addLogMessage(spotId, "Interstitial: Got video content player error event");
                }
            });
            
            // If you would like to change the full screen video behaviour, you can create and configure a video content controller, and add it to the unit controller
            // Override the default behaviour - show the controls within the video frame
            videoContentController.setOverlayOutside(false);
            
            // Now add the content controller to the unit controller
            fullscreenUnitController.addContentController(videoContentController);
            
            fullscreenUnitController.show(this);
        } else if (mInterstitialSpot == null || !mInterstitialAdLoaded) {
            mLoggerController.addLogMessage("", "The Interstitial ad is not ready.");
        } else if (!mInterstitialSpot.isReady()) {
            mLoggerController.addLogMessage("", "The Interstitial ad has expired.");
        }
    }
    
    protected String getTitleAppName() {
        return getResources().getString(R.string.title_app_name);
    }
    
    /**
     * Small helper for updating the Sample title
     *
     * @param title the title we want to see in the activity's header
     */
    protected void updateTitle(String title) {
        TextView header = findViewById(R.id.header_text);
        if (header != null) {
            header.setText(title);
        }
    }
    
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
}
