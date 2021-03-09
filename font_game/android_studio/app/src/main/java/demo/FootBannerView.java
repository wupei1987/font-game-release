package demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.wupei.fontgame.R;

import java.util.List;

import layaair.game.browser.ExportJavaFunction;

public class FootBannerView extends RelativeLayout {
    private TTNativeExpressAd mTTAd;
    public FrameLayout banner_container;
    private Context mContext;

    public FootBannerView(Context context)
    {
        super(context);
        this.initView(context);
    }

    private void initView(Context context)
    {
        LayoutInflater.from(context).inflate(R.layout.foot_banner_dialog,this,true);

        banner_container = (FrameLayout)findViewById(R.id.container_banner);
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945894707") //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(600,150) //期望模板广告view的size,单位dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                Log.v("FootBannerDialog",message);
                throw new Error(message);
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.v("FootBannerDialog","onNativeExpressAdLoad" + ads.size());
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                mTTAd.render();
                bindAdListener(mTTAd);
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
            }

            @Override
            public void onAdShow(View view, int type) {
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
                Log.v("FootBannerDialog","FootBannerDialog onRenderSuccess");
                banner_container.removeAllViews();
                banner_container.addView(view);
            }
        });
    }

    private TTFullScreenVideoAd mttFullVideoAd;
    private Boolean isFulllAdLoading;
    private Boolean mHasShowDownloadActive;
    public void showVedioAd(){
        JSBridge.debugTipToJs("show full screen video");
        if(isFulllAdLoading)
        {
            JSBridge.showTipToJs("正在加载广告");
            return;
        }
        isFulllAdLoading = true;
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(this.mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945889184")
        //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
        //且仅是模板渲染的代码位ID使用，非模板渲染代码位切勿使用
//                .setExpressViewAcceptedSize(500,500)
//                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            //请求广告失败
            @Override
            public void onError(int code, String message) {

            }

            //广告物料加载完成的回调
            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        JSBridge.debugTipToJs("show full screen video");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        JSBridge.debugTipToJs("full screen video click");
                    }

                    @Override
                    public void onAdClose() {
                        JSBridge.debugTipToJs("full screen video close");
                    }

                    @Override
                    public void onVideoComplete() {
                        JSBridge.debugTipToJs("full screen video complete");
                        ExportJavaFunction.CallBackToJS(JSBridge.class,"showVedioAdCallBk",null);
                    }

                    @Override
                    public void onSkippedVideo() {
                        JSBridge.debugTipToJs("full screen video skip");
                        ExportJavaFunction.CallBackToJS(JSBridge.class,"showVedioAdCallBk",null);
                    }

                });


                ad.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            JSBridge.showTipToJs("下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        JSBridge.showTipToJs("下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        JSBridge.showTipToJs("点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        JSBridge.showTipToJs("下载完成，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        JSBridge.showTipToJs("安装完成，点击下载区域打开");
                    }
                });
            }

            //广告视频本地加载完成的回调，接入方可以在这个回调后直接播放本地视频
            @Override
            public void onFullScreenVideoCached() {
                isFulllAdLoading = false;
                mttFullVideoAd.showFullScreenVideoAd(MainActivity.mActivity, TTAdConstant.RitScenes.GAME_GIFT_BONUS, "");
                mttFullVideoAd = null;
            }
        });
//        JSBridge.debugTipToJs("MainActivity.mActivity:" + MainActivity.mActivity == null ? "true" : "false");
//        mttFullVideoAd.showFullScreenVideoAd(MainActivity.mActivity, TTAdConstant.RitScenes.GAME_GIFT_BONUS, "");
//        mttFullVideoAd = null;
    }
}
