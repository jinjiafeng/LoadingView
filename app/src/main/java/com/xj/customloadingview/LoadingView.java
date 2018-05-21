package com.xj.customloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class LoadingView extends LinearLayout {
    //动画执行时间
    private static final int DURATION = 350;
    //圆形,矩形,三角形
    private ShapeView mShapeView;
    //阴影
    private View mShadowView;
    //需要平移的距离
    private float mTranslationDistance;
    // 是否停止动画
    private boolean mIsStopAnimator = false;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.ui_loading_view, this);
        mShapeView = findViewById(R.id.shape_view);
        mShadowView = findViewById(R.id.shadow_view);
        mTranslationDistance = dp2px(80);
        //初始化执行下降动画,添加到队列中,源码->View加载完成后会执行
        post(new Runnable() {
            @Override
            public void run() {
                startDropAnim();
            }
        });
    }

    /**
     * 优化性能
     * 采用代码的方式添加
     */
    public static LoadingView attach(ViewGroup parent) {
        LoadingView loadingView = new LoadingView(parent.getContext());
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parent.addView(loadingView);
        return loadingView;
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void startDropAnim() {
        if (!mIsStopAnimator) {
            final AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mTranslationDistance);

            ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowView, "scaleX", 1f, 0f);

            animatorSet.setDuration(DURATION);
            //AnimatorSet中的动画一起执行
            animatorSet.playTogether(shadowAnim, translationAnimator);
            //下落逐渐加速
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //当下落动画结束更换形状
                    mShapeView.exchange();
                    //开启上升动画
                    startUpAnim();
                }
            });
        }
    }

    private void startUpAnim() {
        if (!mIsStopAnimator) {
            final AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", mTranslationDistance, 0);
            //translationAnimator.setDuration(ANIMATOR_DURATION);
            // 配合中间阴影缩小
            ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowView, "scaleX", 0.3f, 1f);
            animatorSet.setDuration(DURATION);
            animatorSet.playTogether(shadowAnim, translationAnimator);
            //上升逐渐减速
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startDropAnim();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    //上升动画开始时开始旋转形状
                    startRotate();
                }
            });
            //需要在animatorSet设置监听后调用,否则不是执行onAnimationStart()方法
            animatorSet.start();
        }
    }

    private void startRotate() {
        ObjectAnimator translationAnimator = null;
        final ShapeView.Shape currentShape = mShapeView.getCurrentShape();
        switch (currentShape) {
            case TRIANGLE:
                translationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 120);
                break;
            case RECTANGLE:
                translationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180);
                break;
            default:
                break;
        }
        if (translationAnimator != null) {
            translationAnimator.setDuration(DURATION);
            translationAnimator.setInterpolator(new DecelerateInterpolator());
            translationAnimator.start();
        }
    }

    /**
     * 动态移除
     * @param visibility 可见性
     */
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(View.INVISIBLE);
        mShapeView.clearAnimation();
        mShadowView.clearAnimation();
        final ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
            removeAllViews();
        }
        mIsStopAnimator = true;
    }
}
