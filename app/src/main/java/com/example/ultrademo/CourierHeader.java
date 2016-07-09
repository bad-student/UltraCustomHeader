package com.example.ultrademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.PtrUIHandlerHook;
import in.srain.cube.views.ptr.header.MaterialProgressDrawable;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by hfwu on 2016/7/1 .
 * Description: 模仿京东下拉刷新
 */
public class CourierHeader extends ImageView implements PtrUIHandler {


    private Context mContext;
    //快件包裹
    private Bitmap express;
    //快递员
    private Bitmap courier;
    private Bitmap courierWithexpress;

    private int measuredWidth;
    private int measuredHeight;

    private float mCurrentProgress;
    private int mCurrentAlpha;
    //缩放
    private Bitmap scaledcourier;
    private Bitmap scaledexpress;
    //快递员跑动效果的帧动画
    private AnimationDrawable frameAnimation;
    //绘制下拉中快递员和包裹渐变效果
    private Paint mPaint;
    //绘制下拉标语
    private Paint sloganPaint;
    //绘制下拉文字提示
    private Paint textTipPaint;
    //下拉状态
    private static boolean isPrepare = false;
    private static boolean isLoading = false;
    private static boolean isComplete = false;
    //下拉标语
    private final static String SLOGAN = "让购物更便捷";
    //下拉文字提示
    private String text = "";
    private final static String PULL_TIP = "下拉刷新";
    private final static String LOOSEN_TIP = "松开刷新";
    private final static String REFRESHING_TIP = "正在刷新";
    //向左整体偏移(原因是帧动画在imageview中间播放，只能通过view.setTranslationX改变位置)
    private final static int COURIERHEADER_TRANSLATIONX=-270;


    public CourierHeader(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CourierHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CourierHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView() {

        //快递员跑动效果的帧动画
        frameAnimation = new AnimationDrawable();
        frameAnimation.addFrame(ContextCompat.getDrawable(mContext, R.mipmap.app_refresh_courier_1), 60);
        frameAnimation.addFrame(ContextCompat.getDrawable(mContext, R.mipmap.app_refresh_courier_2), 60);
        frameAnimation.addFrame(ContextCompat.getDrawable(mContext, R.mipmap.app_refresh_courier_3), 60);
        //frameAnimation.setOneShot(false);

        //包裹bitmap
        express = BitmapFactory.decodeResource(getResources(), R.mipmap.app_refresh_express_0);
        //快递员bitmap
        courier = BitmapFactory.decodeResource(getResources(), R.mipmap.app_refresh_courier_0);
        //这是后面动画中的最后一张图片，拿这张图片的作用是用它的宽高来测量
        //我们这个自定义View的宽高
        courierWithexpress = BitmapFactory.decodeResource(getResources(), R.mipmap.app_refresh_courier_3);

        //来个画笔，我们注意到快递小哥和包裹都有一个渐变效果
        mPaint = new Paint();
        mPaint.setAlpha(0);
        //下拉刷新绘制文字的画笔初始化
        sloganPaint = new Paint();
        sloganPaint.setColor(ContextCompat.getColor(mContext, R.color.grey41));
        sloganPaint.setTextSize(50);
        textTipPaint = new Paint();
        textTipPaint.setColor(ContextCompat.getColor(mContext, R.color.grey51));
        textTipPaint.setTextSize(40);

    }

    @Override
    public void invalidateDrawable(Drawable dr) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    //测量宽度
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = courierWithexpress.getWidth();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    //测量高度
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = courierWithexpress.getHeight();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //需要提示的是：还需要适配，方法中的数值都写死了
        super.onDraw(canvas);
        //向左整体偏移
        CourierHeader.this.setTranslationX(COURIERHEADER_TRANSLATIONX);
        //绘制“让购物更便捷”
        canvas.save();
        canvas.drawText(SLOGAN, getWidth()/2+100, getHeight()/2, sloganPaint);
        canvas.restore();

        if (!isLoading && isPrepare) {//在缩放

            doScale(canvas);
            if(isComplete){//达到触发下拉刷新要求的下拉高度,改变提示文字为：松手刷新
                text = LOOSEN_TIP;
            }else {//提示文字为：下拉刷新
                text = PULL_TIP;
            }

        } else if (isLoading) {//正在刷新
            this.post(new Runnable() {
                @Override
                public void run() {

                    Log.v("CourierHeader", "frameAnimation.start()");
                    CourierHeader.this.setImageDrawable(frameAnimation);
                    frameAnimation.start();
                    invalidate();
                }
            });
            text = REFRESHING_TIP;
        }
        //绘制文字：下拉刷新、正在刷新、松手刷新
        canvas.save();
        canvas.drawText(text, getWidth() / 2+100, getHeight()-15, textTipPaint);
        canvas.restore();

    }

    /**
     * 下拉过程中的缩放效果
     * 需要提示的是：还需要适配，方法中的数值都写死了
     */
    private void doScale(Canvas canvas) {

        canvas.save();
        //canvas.scale(mCurrentProgress, mCurrentProgress, getWidth()/4+scaledexpress.getWidth()*10/4, 100);
        canvas.scale(mCurrentProgress, mCurrentProgress, getWidth()/2+20, 90);
        mPaint.setAlpha(mCurrentAlpha);
        //canvas.drawBitmap(scaledexpress, getWidth()/4+scaledexpress.getWidth()*10/4, 100, mPaint);
        canvas.drawBitmap(scaledexpress, getWidth()/2+20, 90, mPaint);
        canvas.restore();
        canvas.save();
        canvas.scale(mCurrentProgress, mCurrentProgress,getWidth()/2-60,0);
        mPaint.setAlpha(mCurrentAlpha);
        canvas.drawBitmap(scaledcourier,getWidth()/2-80,0, mPaint);
        canvas.restore();

    }

    /**
     * 在这里面拿到测量后的宽和高，w就是测量后的宽，h是测量后的高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredWidth = w;
        measuredHeight = h;
        //根据测量后的宽高来对快递员做一个缩放
        scaledcourier = Bitmap.createScaledBitmap(courier, measuredWidth * 2 /15, measuredHeight / 1, true);
        //根据测量后的宽高来对快递包裹做一个缩放
        scaledexpress = Bitmap.createScaledBitmap(express, scaledcourier.getWidth() * 2 / 5, scaledcourier.getHeight() /5, true);
    }

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {

        Log.v("CourierHeader", "onUIReset");
        //当位置回到初始位置,做了些处理
        this.post(new Runnable() {
            @Override
            public void run() {

                Log.v("CourierHeader", "frameAnimation.stop()");
                frameAnimation.stop();
                CourierHeader.this.setImageDrawable(null);
                CourierHeader.this.clearAnimation();
                isLoading = false;
                isPrepare = false;
                isComplete = false;
            }
        });
    }

    /**
     * prepare for loading
     *
     * @param frame
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

        Log.v("CourierHeader", "onUIRefreshPrepare");
        isPrepare = true;

    }

    /**
     * perform refreshing UI
     *
     * @param frame
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

        Log.v("CourierHeader", "onUIRefreshBegin");


    }

    /**
     * perform UI after refresh
     *
     * @param frame
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

        Log.v("CourierHeader", "onUIRefreshComplete");

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        Log.v("CourierHeader", "onUIPositionChange");

        float percent = Math.min(1f, ptrIndicator.getCurrentPercent());
        this.mCurrentProgress = percent;
        this.mCurrentAlpha = (int) (percent * 255);

        Log.v("CourierHeader", "status ==" + status);
        //正在刷新状态
        if (status == PtrFrameLayout.PTR_STATUS_LOADING) {
            Log.v("CourierHeader", "PTR_STATUS_LOADING");
            isLoading = true;
        }
        //达到触发下拉刷新要求的下拉高度
        if (ptrIndicator.isOverOffsetToRefresh()) {
            Log.v("CourierHeader", "PTR_STATUS_LOADING");
            isComplete = true;
        }
        //viewtree重绘
        invalidate();
    }
}
