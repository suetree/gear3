package per.sue.gear3.widget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ScrollView;

import per.sue.gear3.utils.GearLog;


/**
 * 自定义View继承GearSwipeRefreshView，添加上拉加载更多的布局属性,添加对RecyclerView的支持
 * Created by Pinger on 2016/9/26.
 */

public class GearSwipeRefreshView extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    public static final int LARGE = 0;
    public static final int DEFAULT = 1;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER = 40;
    @VisibleForTesting
    static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final String LOG_TAG = GearSwipeRefreshView.class.getSimpleName();
    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = 76;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = 0.5F;
    private static final float MAX_PROGRESS_ANGLE = 0.8F;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int CIRCLE_BG_LIGHT = -328966;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private View mTarget;
    OnRefreshListener mListener;
    boolean mRefreshing;
    private int mTouchSlop;
    private float mTotalDragDistance;
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed;
    private final int[] mParentOffsetInWindow;
    private boolean mNestedScrollInProgress;
    private int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;
    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId;
    boolean mScale;
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{16842766};
    GearRefreshCircleImageView mCircleView;
    private int mCircleViewIndex;
    protected int mFrom;
    float mStartingScale;
    protected int mOriginalOffsetTop;
    int mSpinnerOffsetEnd;
    GearMaterialProgressDrawable mProgress;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;
    private Animation mScaleDownToStartAnimation;
    boolean mNotify;
    private int mCircleDiameter;
    boolean mUsingCustomStart;
    private OnChildScrollUpCallback mChildScrollUpCallback;
    private Animation.AnimationListener mRefreshAnimationListener;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;

    private Animation.AnimationListener mLoadMoreAnimationListener;
    private boolean isLoadMoreing;
    private boolean canLoadMore;

    GearRefreshCircleImageView bottomCircleView;
    GearMaterialProgressDrawable bottomProgress;
    private int originalBottomHeight = 60;
    private int bottomHeight = 60;
    private float diverDensity ;


    void reset() {
        this.mCircleView.clearAnimation();
        this.mProgress.stop();
        this.mCircleView.setVisibility(View.GONE);
        this.setColorViewAlpha(255);
        if(this.mScale) {
            this.setAnimationProgress(0.0F);
        } else {
            this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCurrentTargetOffsetTop, true);
        }

        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
        resetBottomCircle();
    }

    void resetBottomCircle() {
        this.bottomCircleView.clearAnimation();
        this.mProgress.stop();
        this.bottomCircleView.setVisibility(View.GONE);
        this.setColorViewAlpha(255);
        this.setAnimationProgressForFinsihLoadMore(0.0F);
        this.isLoadMoreing = false;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled) {
            this.reset();
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.reset();
    }

    @SuppressLint({"NewApi"})
    private void setColorViewAlpha(int targetAlpha) {
        this.mCircleView.getBackground().setAlpha(targetAlpha);
        this.mProgress.setAlpha(targetAlpha);
    }

    public void setProgressViewOffset(boolean scale, int start, int end) {
        this.mScale = scale;
        this.mOriginalOffsetTop = start;
        this.mSpinnerOffsetEnd = end;
        this.mUsingCustomStart = true;
        this.reset();
        this.mRefreshing = false;
    }

    public int getProgressViewStartOffset() {
        return this.mOriginalOffsetTop;
    }

    public int getProgressViewEndOffset() {
        return this.mSpinnerOffsetEnd;
    }

    public void setProgressViewEndTarget(boolean scale, int end) {
        this.mSpinnerOffsetEnd = end;
        this.mScale = scale;
        this.mCircleView.invalidate();
    }

    public void setSize(int size) {
        if(size == 0 || size == 1) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            if(size == 0) {
                this.mCircleDiameter = (int)(56.0F * metrics.density);
            } else {
                this.mCircleDiameter = (int)(40.0F * metrics.density);
            }

            this.mCircleView.setImageDrawable((Drawable)null);
            this.mProgress.updateSizes(size);
            this.mCircleView.setImageDrawable(this.mProgress);
        }
    }

    public GearSwipeRefreshView(Context context) {
        this(context, (AttributeSet)null);
    }

    public GearSwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0F;
        this.mParentScrollConsumed = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mActivePointerId = -1;
        this.mCircleViewIndex = -1;
        this.mRefreshAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            @SuppressLint({"NewApi"})
            public void onAnimationEnd(Animation animation) {
                if(GearSwipeRefreshView.this.mRefreshing) {
                    GearSwipeRefreshView.this.mProgress.setAlpha(255);
                    GearSwipeRefreshView.this.mProgress.start();
                    if(GearSwipeRefreshView.this.mNotify && GearSwipeRefreshView.this.mListener != null) {
                        GearSwipeRefreshView.this.mListener.onRefresh(GearSwipeRefreshView.this);
                    }

                    GearSwipeRefreshView.this.mCurrentTargetOffsetTop = GearSwipeRefreshView.this.mCircleView.getTop();
                } else {
                    GearSwipeRefreshView.this.reset();
                }

            }
        };
        this.mAnimateToCorrectPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                boolean targetTop = false;
                boolean endTarget = false;
                int endTarget1;
                if(!GearSwipeRefreshView.this.mUsingCustomStart) {
                    endTarget1 = GearSwipeRefreshView.this.mSpinnerOffsetEnd - Math.abs(GearSwipeRefreshView.this.mOriginalOffsetTop);
                } else {
                    endTarget1 = GearSwipeRefreshView.this.mSpinnerOffsetEnd;
                }

                int targetTop1 = GearSwipeRefreshView.this.mFrom + (int)((float)(endTarget1 - GearSwipeRefreshView.this.mFrom) * interpolatedTime);
                int offset = targetTop1 - GearSwipeRefreshView.this.mCircleView.getTop();
                GearSwipeRefreshView.this.setTargetOffsetTopAndBottom(offset, false);
                GearSwipeRefreshView.this.mProgress.setArrowScale(1.0F - interpolatedTime);
            }
        };
        this.mAnimateToStartPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                GearSwipeRefreshView.this.moveToStart(interpolatedTime);
            }
        };
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = 1000;//1s
        this.setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        this.mCircleDiameter = (int)(40.0F * metrics.density);
        this.originalBottomHeight = bottomHeight = (int)(60.0F * metrics.density);
        diverDensity = metrics.density;
        this.createTopProgressView();
        this.createBottomProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        this.mSpinnerOffsetEnd = (int)(64.0F * metrics.density);
        this.mTotalDragDistance = (float)this.mSpinnerOffsetEnd;
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        this.setNestedScrollingEnabled(true);
        this.mOriginalOffsetTop = this.mCurrentTargetOffsetTop = -this.mCircleDiameter;
        this.moveToStart(1.0F);
        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        this.setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        return this.mCircleViewIndex < 0?i:(i == childCount - 1?this.mCircleViewIndex:(i >= this.mCircleViewIndex?i + 1:i));
    }

    private void createTopProgressView() {
        this.mCircleView = new GearRefreshCircleImageView(this.getContext(), -328966);
        this.mProgress = new GearMaterialProgressDrawable(this.getContext(), this);
        this.mProgress.setBackgroundColor(-328966);
        this.mCircleView.setImageDrawable(this.mProgress);
        this.mCircleView.setVisibility(View.GONE);
        this.addView(this.mCircleView);
    }



    private void createBottomProgressView() {

        this.mLoadMoreAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            @SuppressLint({"NewApi"})
            public void onAnimationEnd(Animation animation) {
                if(GearSwipeRefreshView.this.isLoadMoreing) {
                    GearSwipeRefreshView.this.bottomProgress.setAlpha(255);
                    GearSwipeRefreshView.this.bottomProgress.start();
                    if(GearSwipeRefreshView.this.mListener != null) {
                        GearSwipeRefreshView.this.mListener.onLoadMore(GearSwipeRefreshView.this);
                    }
                }else{
                    resetBottomCircle();
                }
            }
        };

        this.bottomCircleView = new GearRefreshCircleImageView(this.getContext(), -328966);
        bottomProgress = new GearMaterialProgressDrawable(this.getContext(), this);
        bottomProgress.setBackgroundColor(-328966);
        bottomCircleView.setImageDrawable(bottomProgress);
        bottomCircleView.setVisibility(View.GONE);
        this.addView(this.bottomCircleView);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }

    private boolean isAlphaUsedForScale() {
        return Build.VERSION.SDK_INT < 11;
    }

    public void setRefreshing(boolean refreshing) {
        if(refreshing && this.mRefreshing != refreshing) {
            this.mRefreshing = refreshing;
            boolean endTarget = false;
            int endTarget1;
            if(!this.mUsingCustomStart) {
                endTarget1 = this.mSpinnerOffsetEnd + this.mOriginalOffsetTop;
            } else {
                endTarget1 = this.mSpinnerOffsetEnd;
            }

            this.setTargetOffsetTopAndBottom(endTarget1 - this.mCurrentTargetOffsetTop, true);
            this.mNotify = false;
            this.startScaleUpAnimation(this.mRefreshAnimationListener);
        } else {
            this.setRefreshing(refreshing, false);
        }

    }

    @SuppressLint({"NewApi"})
    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        this.mCircleView.setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >= 11) {
            this.mProgress.setAlpha(255);
        }

        this.mScaleAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                GearSwipeRefreshView.this.setAnimationProgress(interpolatedTime);
            }
        };
        this.mScaleAnimation.setDuration((long)this.mMediumAnimationDuration);
        if(listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }

    @SuppressLint({"NewApi"})
    private void startScaleUpAnimationForLoadMore(Animation.AnimationListener listener) {
        this.bottomCircleView.setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >= 11) {
            this.bottomProgress.setAlpha(255);
        }

        this.mScaleAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                GearSwipeRefreshView.this.setAnimationProgressForFinsihLoadMore(interpolatedTime);
            }
        };
        this.mScaleAnimation.setDuration((long)500);
        if(listener != null) {
            this.bottomCircleView.setAnimationListener(listener);
        }

        this.bottomCircleView.clearAnimation();
        this.bottomCircleView.startAnimation(this.mScaleAnimation);
    }

    void setAnimationProgress(float progress) {
        if(this.isAlphaUsedForScale()) {
            this.setColorViewAlpha((int)(progress * 255.0F));
        } else {
            ViewCompat.setScaleX(this.mCircleView, progress);
            ViewCompat.setScaleY(this.mCircleView, progress);
        }
    }

    void setAnimationProgressForFinsihLoadMore(float progress) {
        if(this.isAlphaUsedForScale()) {
            this.setColorViewAlpha((int)(progress * 255.0F));
        } else {
            ViewCompat.setScaleX(this.bottomCircleView, progress);
            ViewCompat.setScaleY(this.bottomCircleView, progress);
        }
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if(this.mRefreshing != refreshing) {
            this.mNotify = notify;
            this.ensureTarget();
            this.mRefreshing = refreshing;
            if(this.mRefreshing) {
                this.animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshAnimationListener);
            } else {
                this.startScaleDownAnimation(this.mRefreshAnimationListener);
            }
        }

    }

    void startScaleDownAnimation(Animation.AnimationListener listener) {
        this.mScaleDownAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                GearSwipeRefreshView.this.setAnimationProgress(1.0F - interpolatedTime);
            }
        };
        this.mScaleDownAnimation.setDuration(150L);
        this.mCircleView.setAnimationListener(listener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }

    void startScaleDownAnimationForFinshLoadMore(Animation.AnimationListener listener) {
        this.mScaleDownAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                GearSwipeRefreshView.this.setAnimationProgressForFinsihLoadMore(1.0F - interpolatedTime);
            }
        };
        this.mScaleDownAnimation.setDuration(150L);
        this.bottomCircleView.setAnimationListener(listener);
        this.bottomCircleView.clearAnimation();
        this.bottomCircleView.startAnimation(this.mScaleDownAnimation);
    }

    @SuppressLint({"NewApi"})
    private void startProgressAlphaStartAnimation() {
        this.mAlphaStartAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 76);
    }

    @SuppressLint({"NewApi"})
    private void startProgressAlphaMaxAnimation() {
        this.mAlphaMaxAnimation = this.startAlphaAnimation(this.mProgress.getAlpha(), 255);
    }

    @SuppressLint({"NewApi"})
    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        if(this.mScale && this.isAlphaUsedForScale()) {
            return null;
        } else {
            Animation alpha = new Animation() {
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    GearSwipeRefreshView.this.mProgress.setAlpha((int)((float)startingAlpha + (float)(endingAlpha - startingAlpha) * interpolatedTime));
                }
            };
            alpha.setDuration(300L);
            this.mCircleView.setAnimationListener((Animation.AnimationListener)null);
            this.mCircleView.clearAnimation();
            this.mCircleView.startAnimation(alpha);
            return alpha;
        }
    }

    /** @deprecated */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        this.setProgressBackgroundColorSchemeResource(colorRes);
    }

    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        this.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.getContext(), colorRes));
    }

    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        this.mCircleView.setBackgroundColor(color);
        this.mProgress.setBackgroundColor(color);
    }

    /** @deprecated */
    @Deprecated
    public void setColorScheme(@ColorInt int... colors) {
        this.setColorSchemeResources(colors);
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        Context context = this.getContext();
        int[] colorRes = new int[colorResIds.length];

        for(int i = 0; i < colorResIds.length; ++i) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i]);
        }

        this.setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(@ColorInt int... colors) {
        this.ensureTarget();
        this.mProgress.setColorSchemeColors(colors);
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    private void ensureTarget() {
        if(this.mTarget == null) {
            for(int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if(!(child instanceof GearRefreshCircleImageView)) {
                    this.mTarget = child;
                    break;
                }
            }
        }

    }

    public void setDistanceToTriggerSync(int distance) {
        this.mTotalDragDistance = (float)distance;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        if(this.getChildCount() != 0) {
            if(this.mTarget == null) {
                this.ensureTarget();
            }

            if(this.mTarget != null) {
                View child = this.mTarget;
                int childLeft = this.getPaddingLeft();
                int childTop = this.getPaddingTop();
                int childWidth = width - this.getPaddingLeft() - this.getPaddingRight();
                int childHeight = height - this.getPaddingTop() - this.getPaddingBottom() ;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                int circleWidth = this.mCircleView.getMeasuredWidth();
                int circleHeight = this.mCircleView.getMeasuredHeight();
                this.mCircleView.layout(width / 2 - circleWidth / 2, this.mCurrentTargetOffsetTop, width / 2 + circleWidth / 2, this.mCurrentTargetOffsetTop + circleHeight);

                int bottomCircleWidth = this.bottomCircleView.getMeasuredWidth();
                int bottomCircleHeight = this.bottomCircleView.getMeasuredHeight();
                int bottomCircleLeft  = width / 2 - bottomCircleWidth / 2;
                int bottomCircleTop  = height - bottomHeight/2  - bottomCircleHeight/2;
                int bottomCircleRight  =  width / 2 + bottomCircleWidth / 2;
                int bottomCircleBottom =  bottomCircleTop + bottomCircleHeight;
                this.bottomCircleView.layout(bottomCircleLeft , bottomCircleTop ,bottomCircleRight, bottomCircleBottom);
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(this.mTarget == null) {
            this.ensureTarget();
        }

        if(this.mTarget != null) {
            this.mTarget.measure(MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), MeasureSpec.EXACTLY));
            this.mCircleView.measure(MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.EXACTLY));
            this.bottomCircleView.measure(MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.mCircleDiameter, MeasureSpec.EXACTLY));

            this.mCircleViewIndex = -1;

            for(int index = 0; index < this.getChildCount(); ++index) {
                if(this.getChildAt(index) == this.mCircleView) {
                    this.mCircleViewIndex = index;
                    break;
                }
            }

        }
    }

    public int getProgressCircleDiameter() {
        return this.mCircleDiameter;
    }



    public boolean canChildScrollUp() {
        if(this.mChildScrollUpCallback != null) {
            return this.mChildScrollUpCallback.canChildScrollUp(this, this.mTarget);
        } else if(Build.VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTarget, -1);//顶部是否可以滚动
        } else if(this.mTarget instanceof AbsListView) {
            AbsListView absListView = (AbsListView)this.mTarget;
            return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
        } else {
            return ViewCompat.canScrollVertically(this.mTarget, -1) || this.mTarget.getScrollY() > 0;
        }
    }

    public boolean canChildScrollDown() {

        GearLog.e(LOG_TAG, "canChildScrollDown mRefreshing = " + mRefreshing + "  isMoreing = " + isLoadMoreing);

        if(this.mChildScrollUpCallback != null) {
            return this.mChildScrollUpCallback.canChildScrollUp(this, this.mTarget);
        } else if(Build.VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTarget, 1);//底部是否可以滚动
        } else if(this.mTarget instanceof AbsListView) {
            AbsListView absListView = (AbsListView)this.mTarget;
            return absListView.getChildCount() > 0 && ((absListView.getLastVisiblePosition() == (absListView.getCount() - 1)) &&  absListView.getChildAt(absListView.getLastVisiblePosition()).getBottom() == absListView.getHeight());
        } else {
            return ViewCompat.canScrollVertically(this.mTarget, 1) ;//底部是否可以滚动
        }
    }

    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        this.mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.ensureTarget();
        int action = MotionEventCompat.getActionMasked(ev);
        if(this.mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            this.mReturningToStart = false;
        }

        if(this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress  && !this.isLoadMoreing) {
            int pointerIndex;
            switch(action) {
                case MotionEvent.ACTION_DOWN:
                    this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop(), true);
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mIsBeingDragged = false;
                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if(pointerIndex < 0) {
                        return false;
                    }
                    this.mInitialDownY = ev.getY(pointerIndex);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(this.mActivePointerId == -1) {
                        Log.e(LOG_TAG, "Got ACTION_MOVE event but don\'t have an active pointer id.");
                        return false;
                    }

                    pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if(pointerIndex < 0) {
                        return false;
                    }

                    float y = ev.getY(pointerIndex);
                    this.startDragging(y);
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }
            return this.mIsBeingDragged;
        }else {
            if(this.isEnabled()&& this.canLoadMore && !this.mReturningToStart && !canChildScrollDown() &&  !this.mRefreshing && !this.isLoadMoreing && !this.mNestedScrollInProgress){
                setLoadMoreing(true);
            }
            return false;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
        if((Build.VERSION.SDK_INT >= 21 || !(this.mTarget instanceof AbsListView)) && (this.mTarget == null || ViewCompat.isNestedScrollingEnabled(this.mTarget))) {
            super.requestDisallowInterceptTouchEvent(b);
        }

    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return this.isEnabled() && !this.mReturningToStart && !this.mRefreshing && (nestedScrollAxes & 2) != 0;
    }

    public void onNestedScrollAccepted(View child, View target, int axes) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        this.startNestedScroll(axes & 2);
        this.mTotalUnconsumed = 0.0F;
        this.mNestedScrollInProgress = true;
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(dy > 0 && this.mTotalUnconsumed > 0.0F) {
            if((float)dy > this.mTotalUnconsumed) {
                consumed[1] = dy - (int)this.mTotalUnconsumed;
                this.mTotalUnconsumed = 0.0F;
            } else {
                this.mTotalUnconsumed -= (float)dy;
                consumed[1] = dy;
            }

            this.moveSpinner(this.mTotalUnconsumed);
        }

        if(this.mUsingCustomStart && dy > 0 && this.mTotalUnconsumed == 0.0F && Math.abs(dy - consumed[1]) > 0) {
            this.mCircleView.setVisibility(View.GONE);
        }

        int[] parentConsumed = this.mParentScrollConsumed;
        if(this.dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, (int[])null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }

    }

    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    public void onStopNestedScroll(View target) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(target);
        this.mNestedScrollInProgress = false;
        if(this.mTotalUnconsumed > 0.0F) {
            this.finishSpinner(this.mTotalUnconsumed);
            this.mTotalUnconsumed = 0.0F;
        }

        this.stopNestedScroll();
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, this.mParentOffsetInWindow);
        int dy = dyUnconsumed + this.mParentOffsetInWindow[1];
        if(dy < 0 && !this.canChildScrollUp()) {
            this.mTotalUnconsumed += (float)Math.abs(dy);
            this.moveSpinner(this.mTotalUnconsumed);
        }
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return this.mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return this.dispatchNestedPreFling(velocityX, velocityY);
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return this.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    @SuppressLint({"NewApi"})
    private void moveSpinner(float overscrollTop) {
        this.mProgress.showArrow(true);
        float originalDragPercent = overscrollTop / this.mTotalDragDistance;
        float dragPercent = Math.min(1.0F, Math.abs(originalDragPercent));
        float adjustedPercent = (float)Math.max((double)dragPercent - 0.4D, 0.0D) * 5.0F / 3.0F;
        float extraOS = Math.abs(overscrollTop) - this.mTotalDragDistance;
        float slingshotDist = this.mUsingCustomStart?(float)(this.mSpinnerOffsetEnd - this.mOriginalOffsetTop):(float)this.mSpinnerOffsetEnd;
        float tensionSlingshotPercent = Math.max(0.0F, Math.min(extraOS, slingshotDist * 2.0F) / slingshotDist);
        float tensionPercent = (float)((double)(tensionSlingshotPercent / 4.0F) - Math.pow((double)(tensionSlingshotPercent / 4.0F), 2.0D)) * 2.0F;
        float extraMove = slingshotDist * tensionPercent * 2.0F;
        int targetY = this.mOriginalOffsetTop + (int)(slingshotDist * dragPercent + extraMove);
        if(this.mCircleView.getVisibility() != View.VISIBLE) {
            this.mCircleView.setVisibility(View.VISIBLE);
        }

        if(!this.mScale) {
            ViewCompat.setScaleX(this.mCircleView, 1.0F);
            ViewCompat.setScaleY(this.mCircleView, 1.0F);
        }

        if(this.mScale) {
            this.setAnimationProgress(Math.min(1.0F, overscrollTop / this.mTotalDragDistance));
        }

        if(overscrollTop < this.mTotalDragDistance) {
            if(this.mProgress.getAlpha() > 76 && !this.isAnimationRunning(this.mAlphaStartAnimation)) {
                this.startProgressAlphaStartAnimation();
            }
        } else if(this.mProgress.getAlpha() < 255 && !this.isAnimationRunning(this.mAlphaMaxAnimation)) {
            this.startProgressAlphaMaxAnimation();
        }

        float strokeStart = adjustedPercent * 0.8F;
        this.mProgress.setStartEndTrim(0.0F, Math.min(0.8F, strokeStart));
        this.mProgress.setArrowScale(Math.min(1.0F, adjustedPercent));
        float rotation = (-0.25F + 0.4F * adjustedPercent + tensionPercent * 2.0F) * 0.5F;
        this.mProgress.setProgressRotation(rotation);
        this.setTargetOffsetTopAndBottom(targetY - this.mCurrentTargetOffsetTop, true);
    }

    private void finishSpinner(float overscrollTop) {
        if(overscrollTop > this.mTotalDragDistance) {
            this.setRefreshing(true, true);
        } else {
            this.mRefreshing = false;
            this.mProgress.setStartEndTrim(0.0F, 0.0F);
            Animation.AnimationListener listener = null;
            if(!this.mScale) {
                listener = new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        if(!GearSwipeRefreshView.this.mScale) {
                            GearSwipeRefreshView.this.startScaleDownAnimation((Animation.AnimationListener)null);
                        }

                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                };
            }

            this.animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, listener);
            this.mProgress.showArrow(false);
        }

    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        boolean pointerIndex = true;
        if(this.mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            this.mReturningToStart = false;
        }

        if(this.isEnabled() && !this.mReturningToStart && !this.canChildScrollUp() && !this.mRefreshing && !this.mNestedScrollInProgress && !isLoadMoreing) {//下拉
            //GearLog.e(LOG_TAG, "onTouchEvent  下拉");
            float y;
            float overscrollTop;
            int pointerIndex1;
            switch(action) {
                case MotionEvent.ACTION_DOWN:
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mIsBeingDragged = false;
                    break;
                case MotionEvent.ACTION_UP:
                    pointerIndex1 = ev.findPointerIndex(this.mActivePointerId);
                    if(pointerIndex1 < 0) {
                        GearLog.e(LOG_TAG, "Got ACTION_UP event but don\'t have an active pointer id.");
                        return false;
                    }

                    if(this.mIsBeingDragged) {
                        y = ev.getY(pointerIndex1);
                        overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                        this.mIsBeingDragged = false;
                        this.finishSpinner(overscrollTop);
                    }

                    this.mActivePointerId = -1;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    pointerIndex1 = ev.findPointerIndex(this.mActivePointerId);
                    if(pointerIndex1 < 0) {
                        GearLog.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                        return false;
                    }

                    y = ev.getY(pointerIndex1);
                    this.startDragging(y);
                    if(this.mIsBeingDragged) {
                        overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                        if(overscrollTop <= 0.0F) {
                            return false;
                        }
                        this.moveSpinner(overscrollTop);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    return false;
                case 4:
                default:
                    break;
                case 5:
                    pointerIndex1 = MotionEventCompat.getActionIndex(ev);
                    if(pointerIndex1 < 0) {
                        GearLog.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                        return false;
                    }
                    this.mActivePointerId = ev.getPointerId(pointerIndex1);
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return true;
        }else {



            return false;
        }
    }

    @SuppressLint({"NewApi"})
    private void startDragging(float y) {
        float yDiff = y - this.mInitialDownY;
        if(yDiff > (float)this.mTouchSlop && !this.mIsBeingDragged) {
            this.mInitialMotionY = this.mInitialDownY + (float)this.mTouchSlop;
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(76);
        }

    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        this.mFrom = from;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200L);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if(listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if(this.mScale) {
            this.startScaleDownReturnToStartAnimation(from, listener);
        } else {
            this.mFrom = from;
            this.mAnimateToStartPosition.reset();
            this.mAnimateToStartPosition.setDuration(200L);
            this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
            if(listener != null) {
                this.mCircleView.setAnimationListener(listener);
            }

            this.mCircleView.clearAnimation();
            this.mCircleView.startAnimation(this.mAnimateToStartPosition);
        }

    }

    void moveToStart(float interpolatedTime) {
        boolean targetTop = false;
        int targetTop1 = this.mFrom + (int)((float)(this.mOriginalOffsetTop - this.mFrom) * interpolatedTime);
        int offset = targetTop1 - this.mCircleView.getTop();
        this.setTargetOffsetTopAndBottom(offset, false);
    }

    @SuppressLint({"NewApi"})
    private void startScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
        this.mFrom = from;
        if(this.isAlphaUsedForScale()) {
            this.mStartingScale = (float)this.mProgress.getAlpha();
        } else {
            this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
        }

        this.mScaleDownToStartAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = GearSwipeRefreshView.this.mStartingScale + -GearSwipeRefreshView.this.mStartingScale * interpolatedTime;
                GearSwipeRefreshView.this.setAnimationProgress(targetScale);
                GearSwipeRefreshView.this.moveToStart(interpolatedTime);
            }
        };
        this.mScaleDownToStartAnimation.setDuration(150L);
        if(listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }

        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }

    void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        this.mCircleView.bringToFront();
        ViewCompat.offsetTopAndBottom(this.mCircleView, offset);
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
        if(requiresUpdate && Build.VERSION.SDK_INT < 11) {
            this.invalidate();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = ev.getPointerId(pointerIndex);
        if(pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0?1:0;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }

    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(GearSwipeRefreshView var1, @Nullable View var2);
    }

    public interface OnRefreshListener {
        public void onRefresh(GearSwipeRefreshView gearSwipeRefreshView);
        public void onLoadMore(GearSwipeRefreshView gearSwipeRefreshView);
    }


    public void setCanLoadMore(boolean b){
        this.canLoadMore = b;
    }

    public void setLoadMoreing(boolean isLoadMoreing){
        if(this.isLoadMoreing != isLoadMoreing ){
            this.isLoadMoreing = isLoadMoreing;
            if(isLoadMoreing ){
                bottomProgress.setStartEndTrim(0f, 0.8f);
                bottomProgress.setArrowScale(1f); //0~1之间
                bottomProgress.setProgressRotation(1);
                bottomProgress.showArrow(true);
                this.startScaleUpAnimationForLoadMore(this.mLoadMoreAnimationListener);
            }else{
                this.startScaleDownAnimationForFinshLoadMore(this.mLoadMoreAnimationListener);
                if(null != mTarget){
                    if(mTarget instanceof ScrollView){
                        ((ScrollView)mTarget).scrollBy(0, 100);
                    }else if(mTarget instanceof AbsListView){
                        AbsListView absListView =  ((AbsListView)mTarget);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(null != absListView)
                                    absListView.smoothScrollBy(100, (int)(100 * diverDensity));
                            }
                        }, 200);

                    }
                }
            }
        }



    }



}