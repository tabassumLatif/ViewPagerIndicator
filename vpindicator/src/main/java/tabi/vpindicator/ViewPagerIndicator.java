package tabi.vpindicator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;


import java.util.ArrayList;

import static android.support.v4.view.ViewPager.OnPageChangeListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ViewPagerIndicator extends LinearLayout {

    private final static int DEFAULT_INDICATOR_WIDTH = 5;
    private ViewPager mViewpager;
    private int mIndicatorMargin = -1;
    private int indicatorPerRow = 8;
    private int mIndicatorWidth = -1;
    private int mIndicatorHeight = -1;
    private int mAnimatorResId = R.animator.scale_with_alpha;
    private int mAnimatorReverseResId = 0;
    private int mIndicatorBackgroundResId = R.drawable.white_radius;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;

    private Animator mAnimatorOut;
    private Animator mAnimatorIn;
    private Animator mImmediateAnimatorOut;
    private Animator mImmediateAnimatorIn;
    private int selectedColor = 0;
    private int unSelectedColor = 0;
    private Drawable selectedDrawable = null;
    private Drawable unSelectedDrawable = null;
    private boolean mIsProgress = false;

    ArrayList<View> views = new ArrayList<>();

    private int mLastPosition = -1;
    TypedArray typedArray;

    public ViewPagerIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mIndicatorWidth =
                typedArray.getDimensionPixelSize(R.styleable.ViewPagerIndicator_vpi_width, -1);
        indicatorPerRow =
                typedArray.getInteger(R.styleable.ViewPagerIndicator_vpi_indicator_per_row, 8);
        if(indicatorPerRow < 1){
            indicatorPerRow = 1;
        }
        mIndicatorHeight =
                typedArray.getDimensionPixelSize(R.styleable.ViewPagerIndicator_vpi_height, -1);
        mIndicatorMargin =
                typedArray.getDimensionPixelSize(R.styleable.ViewPagerIndicator_vpi_margin, -1);

        mAnimatorResId = typedArray.getResourceId(R.styleable.ViewPagerIndicator_vpi_animator,
                R.animator.scale_with_alpha);
        mAnimatorReverseResId =
                typedArray.getResourceId(R.styleable.ViewPagerIndicator_vpi_animator_reverse, 0);
        mIndicatorBackgroundResId =
                typedArray.getResourceId(R.styleable.ViewPagerIndicator_vpi_drawable,
                        R.drawable.white_radius);

        mIndicatorUnselectedBackgroundResId =
                typedArray.getResourceId(R.styleable.ViewPagerIndicator_vpi_drawable_unselected,
                        mIndicatorBackgroundResId);

        int orientation = typedArray.getInt(R.styleable.ViewPagerIndicator_vpi_orientation, -1);
        setOrientation(orientation == VERTICAL ? VERTICAL : HORIZONTAL);

        int gravity = typedArray.getInt(R.styleable.ViewPagerIndicator_vpi_gravity, -1);
        setGravity(gravity >= 0 ? gravity : Gravity.CENTER);

        mIsProgress = typedArray.getBoolean(R.styleable.ViewPagerIndicator_vpi_progress, false);

        typedArray.recycle();
    }

    /**
     * Create and configure Indicator in Java code.
     */
    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
        configureIndicator(indicatorWidth, indicatorHeight, indicatorMargin,
                R.animator.scale_with_alpha, 0, R.drawable.white_radius, R.drawable.white_radius);
    }

    public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin,
            @AnimatorRes int animatorId, @AnimatorRes int animatorReverseId,
            @DrawableRes int indicatorBackgroundId,
            @DrawableRes int indicatorUnselectedBackgroundId) {

        mIndicatorWidth = indicatorWidth;
        mIndicatorHeight = indicatorHeight;
        mIndicatorMargin = indicatorMargin;

        mAnimatorResId = animatorId;
        mAnimatorReverseResId = animatorReverseId;
        mIndicatorBackgroundResId = indicatorBackgroundId;
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

        checkIndicatorConfig(getContext());
    }

    private void checkIndicatorConfig(Context context) {
        mIndicatorWidth = (mIndicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorWidth;
        mIndicatorHeight =
                (mIndicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorHeight;
        mIndicatorMargin =
                (mIndicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : mIndicatorMargin;

        mAnimatorResId = (mAnimatorResId == 0) ? R.animator.scale_with_alpha : mAnimatorResId;

        mAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut = createAnimatorOut(context);
        mImmediateAnimatorOut.setDuration(0);

        mAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn = createAnimatorIn(context);
        mImmediateAnimatorIn.setDuration(0);

        mIndicatorBackgroundResId = (mIndicatorBackgroundResId == 0) ? R.drawable.white_radius
                : mIndicatorBackgroundResId;
        mIndicatorUnselectedBackgroundResId =
                (mIndicatorUnselectedBackgroundResId == 0) ? mIndicatorBackgroundResId
                        : mIndicatorUnselectedBackgroundResId;
        if(mIndicatorUnselectedBackgroundResId != 0){
            unSelectedColor = 0;
            unSelectedDrawable = null;
        }
    }

    private Animator createAnimatorOut(Context context) {
        return AnimatorInflater.loadAnimator(context, mAnimatorResId);
    }

    private Animator createAnimatorIn(Context context) {
        Animator animatorIn;
        if (mAnimatorReverseResId == 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorResId);
            animatorIn.setInterpolator(new ReverseInterpolator());
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, mAnimatorReverseResId);
        }
        return animatorIn;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            mLastPosition = -1;
            createIndicators();
            mViewpager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }

    private final OnPageChangeListener mInternalPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override public void onPageSelected(int position) {

            if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
                return;
            }

            if (mAnimatorIn.isRunning()) {
                mAnimatorIn.end();
                mAnimatorIn.cancel();
            }

            if (mAnimatorOut.isRunning()) {
                mAnimatorOut.end();
                mAnimatorOut.cancel();
            }

            View currentIndicator;
            if (mLastPosition >= 0 && (currentIndicator = views.get(mLastPosition)) != null) {
                if(mIndicatorUnselectedBackgroundResId != 0) {
                    currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
                }else if(unSelectedColor != 0){
                    currentIndicator.setBackgroundColor(unSelectedColor);
                }else if(unSelectedDrawable != null){
                    currentIndicator.setBackgroundDrawable(unSelectedDrawable);
                }
                mAnimatorIn.setTarget(currentIndicator);
                mAnimatorIn.start();
            }

            View selectedIndicator = views.get(position);
            if (selectedIndicator != null) {
                if(mIndicatorBackgroundResId != 0) {
                    selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
                }else if(selectedColor != 0){
                    selectedIndicator.setBackgroundColor(selectedColor);
                }else if(selectedDrawable != null){
                    selectedIndicator.setBackgroundDrawable(selectedDrawable);
                }
                mAnimatorOut.setTarget(selectedIndicator);
                mAnimatorOut.start();
                if(position >= 1) {
                    for (int i = 0; i < position; i++) {
                        View view = views.get(i);
                        if(mIndicatorBackgroundResId != 0) {
                            view.setBackgroundResource(mIndicatorBackgroundResId);
                        }else if(selectedColor != 0){
                            view.setBackgroundColor(selectedColor);
                        }else if(selectedDrawable != null && mIsProgress){
                            view.setBackgroundDrawable(selectedDrawable);
                        }else  if(unSelectedDrawable != null && !mIsProgress){
                            view.setBackgroundDrawable(unSelectedDrawable);
                        }
                    }
                }
            }
            mLastPosition = position;
        }

        @Override public void onPageScrollStateChanged(int state) {
        }
    };

    public DataSetObserver getDataSetObserver() {
        return mInternalDataSetObserver;
    }

    private DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override public void onChanged() {
            super.onChanged();
            if (mViewpager == null) {
                return;
            }

            int newCount = mViewpager.getAdapter().getCount();
            int currentCount = getChildCount();

            if (newCount == currentCount) {  // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = mViewpager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }

            createIndicators();
        }
    };

    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void createIndicators() {
        removeAllViews();
        views.clear();
        int count = mViewpager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        int currentItem = mViewpager.getCurrentItem();
        int orientation = getOrientation();

        for (int i = 0; i < count; i++) {
            if (currentItem == i) {
                addIndicator(orientation, mIndicatorBackgroundResId, mImmediateAnimatorOut, true);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId,
                        mImmediateAnimatorIn, false);
            }
        }
    }

    private void addIndicator(int orientation, @DrawableRes int backgroundDrawableId,
            Animator animator, boolean isSelected) {
        if (animator.isRunning()) {
            animator.end();
            animator.cancel();
        }

        View Indicator = new View(getContext());
        if(isSelected){
            if(backgroundDrawableId != 0){
                    Indicator.setBackgroundResource(backgroundDrawableId);
            }else if(selectedColor != 0){
                    Indicator.setBackgroundColor(selectedColor);
            }else if(selectedDrawable != null){
                Indicator.setBackgroundDrawable(selectedDrawable);
            }
        }else{
            if(backgroundDrawableId != 0){
                    Indicator.setBackgroundResource(backgroundDrawableId);

            }else if(selectedColor != 0){

                    Indicator.setBackgroundColor(unSelectedColor);
            }else if(unSelectedDrawable != null){
                Indicator.setBackgroundDrawable(unSelectedDrawable);
            }
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                mIndicatorWidth,
                mIndicatorHeight);

        if (orientation == VERTICAL) {
            layoutParams.leftMargin = mIndicatorMargin;
            layoutParams.rightMargin = mIndicatorMargin;
            layoutParams.topMargin = mIndicatorMargin;
            layoutParams.bottomMargin = mIndicatorMargin;

        } else {
            layoutParams.leftMargin = mIndicatorMargin;
            layoutParams.rightMargin = mIndicatorMargin;
            layoutParams.topMargin = mIndicatorMargin;
            layoutParams.bottomMargin = mIndicatorMargin;

        }

        if(getChildCount() == 0){
            LinearLayout childLinear = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                    R.layout.layout, null);
//            childLinear.setMinimumHeight(mIndicatorHeight+5);
            childLinear.setGravity(Gravity.CENTER);

            //inflate new Linear and add in parent;
            addView(childLinear);

            childLinear.addView(Indicator, layoutParams);
        }else {
            LinearLayout view = (LinearLayout) getChildAt(getChildCount() - 1);
            if (view.getChildCount() == indicatorPerRow){
                LinearLayout childLinear = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                        R.layout.layout, null);
//                childLinear.setMinimumHeight(mIndicatorHeight+5);

                childLinear.setGravity(Gravity.CENTER);
                //add new linear in parent
                addView(childLinear);
                //inflate new Linear and add in parent;
                childLinear.addView(Indicator, layoutParams);
            }else{
                // add child
                view.addView(Indicator, layoutParams);
            }
        }

        animator.setTarget(Indicator);
        animator.start();
        views.add(Indicator);

    }

    private class ReverseInterpolator implements Interpolator {
        @Override public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
//            return value;
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setSelectedColor(int selectedColor){
        this.selectedColor = selectedColor;
        selectedDrawable = null;
        mIndicatorBackgroundResId = 0;

    }

    public void setUnSelectedColor(int unSelectedColor){
        this.unSelectedColor = unSelectedColor;
        unSelectedDrawable = null;
        mIndicatorUnselectedBackgroundResId = 0;
    }

    public void setSelectedDrawable(Drawable selectedColor){
        this.selectedColor = 0;
        selectedDrawable = selectedColor;
        mIndicatorBackgroundResId = 0;

    }

    public void setUnSelectedDrawable(Drawable unSelectedColor){
        this.unSelectedColor = 0;
        unSelectedDrawable = unSelectedColor;
        mIndicatorUnselectedBackgroundResId = 0;
    }


}
