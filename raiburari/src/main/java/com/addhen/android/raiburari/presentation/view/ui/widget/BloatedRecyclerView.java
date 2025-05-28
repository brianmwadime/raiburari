/*
 * Copyright (c) 2015 Henry Addo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.addhen.android.raiburari.presentation.view.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.addhen.android.raiburari.R;
import com.addhen.android.raiburari.presentation.view.ui.adapter.BaseRecyclerViewAdapter;
import com.addhen.android.raiburari.presentation.view.ui.listener.ObservableScrollState;
import com.addhen.android.raiburari.presentation.view.ui.listener.ObservableScrollViewListener;

/**
 * This extends the base {@link RecyclerView} to encapsulate endless
 * scroll, pluggable animations, item decorators, FAB and Parallax effect
 *
 * @author Henry Addo
 */
@SuppressWarnings("PMD.CollapsibleIfStatements") public class BloatedRecyclerView
    extends FrameLayout {

  private static final int SCROLLBARS_NONE = 0;

  private static final int SCROLLBARS_VERTICAL = 1;

  private static final int SCROLLBARS_HORIZONTAL = 2;

  private static final float SCROLL_MULTIPLIER = 0.5f;

  private static final float EPSILON = .0000001f;

  public RecyclerView recyclerView;

  public int showLoadMoreItemNum = 3;

  protected RecyclerView.OnScrollListener mOnScrollListener;

  protected LayoutManagerType layoutManagerType;

  protected int mPadding;

  protected int mPaddingTop;

  protected int mPaddingBottom;

  protected int mPaddingLeft;

  protected int mPaddingRight;

  protected boolean mClipToPadding;

  protected ViewStub mEmpty;

  protected View mEmptyView;

  protected int mEmptyId;

  protected ViewStub mFloatingButtonViewStub;

  protected View mFloatingButtonView;

  protected int mFloatingButtonId;

  private VerticalSwipeRefreshLayout mSwipeRefreshLayout;

  private OnLoadMoreListener onLoadMoreListener;

  private int lastVisibleItemPosition;

  private boolean isLoadingMore;

  private BaseRecyclerViewAdapter mAdapter;

  private ObservableScrollState mObservableScrollState;

  private ObservableScrollViewListener mCallbacks;

  private SparseIntArray mChildrenHeights = new SparseIntArray();

  private int mPrevFirstVisiblePosition;

  private int mPrevFirstVisibleChildHeight = -1;

  private int mPrevScrolledChildrenHeight;

  private int mPrevScrollY;

  private int mScrollY;

  private boolean mFirstScroll;

  private boolean mDragging;

  private int mScrollbarsStyle;

  private CustomRelativeWrapper mHeader;

  private int mTotalYScrolled;

  private OnParallaxScroll mParallaxScroll;

  public BloatedRecyclerView(Context context) {
    super(context);
    initViews();
  }

  public BloatedRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initAttrs(attrs);
    initViews();
  }

  public BloatedRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttrs(attrs);
    initViews();
  }

  protected void initViews() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.bloated_recyclerview, this);
    recyclerView = (RecyclerView) view.findViewById(R.id.bloated_recycleview_list);
    mSwipeRefreshLayout = (VerticalSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    setScrollbars();
    mSwipeRefreshLayout.setEnabled(false);
    if (recyclerView != null) {
      recyclerView.setClipToPadding(mClipToPadding);
      if (Math.abs(mPadding - (-1.1f)) < EPSILON) {
        recyclerView.setPadding(mPadding, mPadding, mPadding, mPadding);
      } else {
        recyclerView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
      }
    }
    mEmpty = (ViewStub) view.findViewById(R.id.bloated_recycleview_empty_view);
    mFloatingButtonViewStub = (ViewStub) view.findViewById(R.id.bloated_recycleview_view_stub);
    mEmpty.setLayoutResource(mEmptyId);
    mFloatingButtonViewStub.setLayoutResource(mFloatingButtonId);
    if (mEmptyId != 0) {
      mEmptyView = mEmpty.inflate();
    }
    mEmpty.setVisibility(View.GONE);
    if (mFloatingButtonId != 0) {
      mFloatingButtonView = mFloatingButtonViewStub.inflate();
      mFloatingButtonView.setVisibility(View.VISIBLE);
    }
  }

  private void setScrollbars() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    switch (mScrollbarsStyle) {
      case SCROLLBARS_VERTICAL:
        mSwipeRefreshLayout.removeView(recyclerView);
        View verticalView =
            inflater.inflate(R.layout.bloated_recyclerview_vertical_scroll, mSwipeRefreshLayout,
                true);
        recyclerView = (RecyclerView) verticalView.findViewById(R.id.bloated_recycleview_list);
        break;
      case SCROLLBARS_HORIZONTAL:
        mSwipeRefreshLayout.removeView(recyclerView);
        View horizontalView =
            inflater.inflate(R.layout.bloated_recyclerview_horizontal_scroll, mSwipeRefreshLayout,
                true);
        recyclerView = (RecyclerView) horizontalView.findViewById(R.id.bloated_recycleview_list);
        break;
      default:
        break;
    }
  }

  protected void initAttrs(AttributeSet attrs) {
    TypedArray typedArray =
        getContext().obtainStyledAttributes(attrs, R.styleable.RaiBloatedRecyclerView);
    try {
      mPadding =
          (int) typedArray.getDimension(R.styleable.RaiBloatedRecyclerView_recyclerviewPadding,
              -1.1f);
      mPaddingTop =
          (int) typedArray.getDimension(R.styleable.RaiBloatedRecyclerView_recyclerviewPaddingTop,
              0.0f);
      mPaddingBottom = (int) typedArray.getDimension(
          R.styleable.RaiBloatedRecyclerView_recyclerviewPaddingBottom, 0.0f);
      mPaddingLeft =
          (int) typedArray.getDimension(R.styleable.RaiBloatedRecyclerView_recyclerviewPaddingLeft,
              0.0f);
      mPaddingRight =
          (int) typedArray.getDimension(R.styleable.RaiBloatedRecyclerView_recyclerviewPaddingRight,
              0.0f);
      mClipToPadding =
          typedArray.getBoolean(R.styleable.RaiBloatedRecyclerView_recyclerviewClipToPadding,
              false);
      mEmptyId =
          typedArray.getResourceId(R.styleable.RaiBloatedRecyclerView_recyclerviewEmptyView, 0);
      mFloatingButtonId = typedArray.getResourceId(
          R.styleable.RaiBloatedRecyclerView_recyclerviewFloatingActionView, 0);
      mScrollbarsStyle =
          typedArray.getInt(R.styleable.RaiBloatedRecyclerView_recyclerviewScrollbars,
              SCROLLBARS_NONE);
    } finally {
      typedArray.recycle();
    }
  }

  void setDefaultScrollListener() {
    mOnScrollListener = new RecyclerView.OnScrollListener() {

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mHeader != null) {
          mTotalYScrolled += dy;
          translateHeader(mTotalYScrolled);
        }
        enableShoworHideToolbarAndFloatingButton(recyclerView);
      }
    };
    recyclerView.addOnScrollListener(mOnScrollListener);
  }

  /**
   * Enable loading more of the recyclerview
   */
  @SuppressWarnings("InflateParams") public void enableInfiniteScroll() {
    mOnScrollListener = new RecyclerView.OnScrollListener() {
      int y;

      private int[] lastPositions;

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mHeader != null) {
          mTotalYScrolled += dy;
          translateHeader(mTotalYScrolled);
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManagerType == null) {
          if (layoutManager instanceof LinearLayoutManager) {
            layoutManagerType = LayoutManagerType.LINEAR;
          } else if (layoutManager instanceof GridLayoutManager) {
            layoutManagerType = LayoutManagerType.GRID;
          } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            layoutManagerType = LayoutManagerType.STAGGERED_GRID;
          } else {
            throw new RuntimeException(
                "Unsupported LayoutManager used. Valid ones are LinearLayoutManager"
                    + ", GridLayoutManager and StaggeredGridLayoutManager");
          }
        }

        switch (layoutManagerType) {
          case LINEAR:
            lastVisibleItemPosition =
                ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            break;
          case GRID:
            lastVisibleItemPosition =
                ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            break;
          case STAGGERED_GRID:
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                (StaggeredGridLayoutManager) layoutManager;
            if (lastPositions == null) {
              lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            }

            staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
            lastVisibleItemPosition = findMax(lastPositions);
            break;
          default:
            break;
        }
        enableShoworHideToolbarAndFloatingButton(recyclerView);
        y = dy;
      }

      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int currentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE) {

          if (y > 0
              && (visibleItemCount > 0 && (lastVisibleItemPosition) >= totalItemCount - 1)
              && !isLoadingMore) {
            isLoadingMore = true;
            if (onLoadMoreListener != null) {
              isLoadingMore = false;
              onLoadMoreListener.loadMore(
                  BloatedRecyclerView.this.recyclerView.getAdapter().getItemCount(),
                  lastVisibleItemPosition);
            }
          }
        }
      }
    };
    recyclerView.addOnScrollListener(mOnScrollListener);
    if (mAdapter.getInfiniteScrollView() == null) {
      mAdapter.setInfiniteScrollView(LayoutInflater.from(getContext())
          .inflate(R.layout.bloated_recyclerview_bottom_progressbar, null));
    }
  }

  @SuppressWarnings("InflateParams") public void disableInfiniteScroll() {
    setDefaultScrollListener();
    mAdapter.setInfiniteScrollView(LayoutInflater.from(getContext())
        .inflate(R.layout.bloated_recyclerview_empty_progressbar, null));
  }

  public View getmEmptyView() {
    return mEmptyView;
  }

  public ViewStub getEmptyViewSub() {
    return mEmpty;
  }

  protected void enableShoworHideToolbarAndFloatingButton(RecyclerView recyclerView) {
    if (mCallbacks != null && getChildCount() > 0) {

      int firstVisiblePosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0));
      int lastVisiblePosition =
          recyclerView.getChildAdapterPosition(recyclerView.getChildAt(getChildCount() - 1));
      int k = firstVisiblePosition;
      for (int j = 0; k <= lastVisiblePosition; k++, j++) {
        if (mChildrenHeights.indexOfKey(k) < 0
            || recyclerView.getChildAt(j).getHeight() != mChildrenHeights.get(k)) {
          mChildrenHeights.put(k, recyclerView.getChildAt(j).getHeight());
        }
      }

      View firstVisibleChild = recyclerView.getChildAt(0);
      if (firstVisibleChild != null) {
        if (mPrevFirstVisiblePosition < firstVisiblePosition) {
          // scroll down
          int skippedChildrenHeight = 0;
          if (firstVisiblePosition - mPrevFirstVisiblePosition != 1) {
            for (int i = firstVisiblePosition - 1; i > mPrevFirstVisiblePosition; i--) {
              if (0 < mChildrenHeights.indexOfKey(i)) {
                skippedChildrenHeight += mChildrenHeights.get(i);
              } else {
                // Approximate each item's height to the first visible child.
                // It may be incorrect, but without this, scrollY will be broken
                // when scrolling from the bottom.
                skippedChildrenHeight += firstVisibleChild.getHeight();
              }
            }
          }
          mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight + skippedChildrenHeight;
          mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
        } else if (firstVisiblePosition < mPrevFirstVisiblePosition) {
          // scroll up
          int skippedChildrenHeight = 0;
          if (mPrevFirstVisiblePosition - firstVisiblePosition != 1) {
            for (int i = mPrevFirstVisiblePosition - 1; i > firstVisiblePosition; i--) {
              if (0 < mChildrenHeights.indexOfKey(i)) {
                skippedChildrenHeight += mChildrenHeights.get(i);
              } else {
                // Approximate each item's height to the first visible child.
                // It may be incorrect, but without this, scrollY will be broken
                // when scrolling from the bottom.
                skippedChildrenHeight += firstVisibleChild.getHeight();
              }
            }
          }
          mPrevScrolledChildrenHeight -= firstVisibleChild.getHeight() + skippedChildrenHeight;
          mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
        } else if (firstVisiblePosition == 0) {
          mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
        }
        if (mPrevFirstVisibleChildHeight < 0) {
          mPrevFirstVisibleChildHeight = 0;
        }
        mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.getTop();
        mPrevFirstVisiblePosition = firstVisiblePosition;

        mCallbacks.onScrollChanged(mScrollY, mFirstScroll, mDragging);
        if (mFirstScroll) {
          mFirstScroll = false;
        }

        if (mPrevScrollY < mScrollY) {
          //down
          mObservableScrollState = ObservableScrollState.UP;
        } else if (mScrollY < mPrevScrollY) {
          //up
          mObservableScrollState = ObservableScrollState.DOWN;
        } else {
          mObservableScrollState = ObservableScrollState.STOP;
        }
        mPrevScrollY = mScrollY;
      }
    }
  }

  public void addOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
    recyclerView.addOnScrollListener(customOnScrollListener);
  }

  public void addItemDividerDecoration(Context context) {
    RecyclerView.ItemDecoration itemDecoration =
        new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(itemDecoration);
  }

  /**
   * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect
   * both measurement and drawing of individual item views.
   * <p>Item decorations are ordered. Decorations placed earlier in the list will be
   * run/queried/drawn first for their effects on item views. Padding added to views will be
   * nested; a padding added by an earlier decoration will mean further item decorations in the
   * list will be asked to draw/pad within the previous decoration's given area.</p>
   *
   * @param itemDecoration Decoration to add
   */
  public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
    recyclerView.addItemDecoration(itemDecoration);
  }

  /**
   * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect
   * both measurement and drawing of individual item views.
   * <p>Item decorations are ordered. Decorations placed earlier in the list will be
   * run/queried/drawn first for their effects on item views. Padding added to views will be
   * nested; a padding added by an earlier decoration will mean further item decorations in the
   * list will be asked to draw/pad within the previous decoration's given area.</p>
   *
   * @param itemDecoration Decoration to add
   * @param index Position in the decoration chain to insert this decoration at. If this
   * value is negative the decoration will be added at the end.
   */
  public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
    recyclerView.addItemDecoration(itemDecoration, index);
  }

  /**
   * Gets the current ItemAnimator for this RecyclerView. A null return value
   * indicates that there is no animator and that item changes will happen without
   * any animations. By default, RecyclerView instantiates and
   * uses an instance of {@link DefaultItemAnimator}.
   *
   * @return ItemAnimator The current ItemAnimator. If null, no animations will occur
   * when changes occur to the items in this RecyclerView.
   */
  public RecyclerView.ItemAnimator getItemAnimator() {
    return recyclerView.getItemAnimator();
  }

  /**
   * Sets the {@link RecyclerView.ItemAnimator} that will handle animations involving changes
   * to the items in this RecyclerView. By default, RecyclerView instantiates and
   * uses an instance of {@link DefaultItemAnimator}. Whether item
   * animations are
   * enabled for the RecyclerView depends on the ItemAnimator and whether
   * the LayoutManager
   * {@link RecyclerView.LayoutManager#supportsPredictiveItemAnimations()
   * supports item animations}.
   *
   * @param animator The ItemAnimator being set. If null, no animations will occur
   * when changes occur to the items in this RecyclerView.
   */
  public void setItemAnimator(RecyclerView.ItemAnimator animator) {
    recyclerView.setItemAnimator(animator);
  }

  /**
   * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
   */
  public void setDefaultOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {

    mSwipeRefreshLayout.setEnabled(true);
    mSwipeRefreshLayout.setColorSchemeResources(R.color.progress_background,
        R.color.progress_background_dark, R.color.progress_done);

    mSwipeRefreshLayout.setOnRefreshListener(listener);
  }

  /**
   * Set the load more listener of recyclerview
   */
  public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    this.onLoadMoreListener = onLoadMoreListener;
  }

  /**
   * Set the layout manager to the recycler
   */
  public void setLayoutManager(RecyclerView.LayoutManager manager) {
    recyclerView.setLayoutManager(manager);
  }

  /**
   * Get the adapter of BloatedRecyclerView
   */
  public RecyclerView.Adapter getAdapter() {
    return recyclerView.getAdapter();
  }

  /**
   * Set a BaseRecyclerViewAdapter or the subclass of BaseRecyclerViewAdapter to the recyclerview
   */
  public void setAdapter(BaseRecyclerViewAdapter adapter) {
    mAdapter = adapter;
    recyclerView.setAdapter(mAdapter);
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
    if (mAdapter != null) {
      mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
          super.onItemRangeChanged(positionStart, itemCount);
          update();
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
          super.onItemRangeInserted(positionStart, itemCount);
          update();
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
          super.onItemRangeRemoved(positionStart, itemCount);
          update();
        }

        @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
          super.onItemRangeMoved(fromPosition, toPosition, itemCount);
          update();
        }

        @Override public void onChanged() {
          super.onChanged();
          update();
        }

        private void update() {
          isLoadingMore = false;
          if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
          }

          if (recyclerView.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
            mEmpty.setVisibility(View.VISIBLE);
          } else if (mEmptyId != 0) {
            mEmpty.setVisibility(View.GONE);
          }
          if (mAdapter.getItemCount() >= showLoadMoreItemNum
              && mAdapter.getInfiniteScrollView() != null
              && mAdapter.getInfiniteScrollView().getVisibility() == View.GONE) {
            mAdapter.getInfiniteScrollView().setVisibility(View.VISIBLE);
          }
          if (mAdapter.getItemCount() < showLoadMoreItemNum
              && mAdapter.getInfiniteScrollView() != null) {
            mAdapter.getInfiniteScrollView().setVisibility(View.GONE);
          }
        }
      });
      if ((mAdapter == null || mAdapter.getAdapterItemCount() == 0) && mEmptyId != 0) {
        mEmpty.setVisibility(View.VISIBLE);
      }
    }
  }

  public void setHasFixedSize(boolean hasFixedSize) {
    recyclerView.setHasFixedSize(hasFixedSize);
  }

  /**
   * Notify the widget that refresh state has changed. Do not call this when refresh is triggered
   * by a swipe gesture.
   */
  public void setRefreshing(boolean refreshing) {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(refreshing);
    }
  }

  /**
   * Enable or disable the SwipeRefreshLayout.
   * Default is false
   */
  public void enableDefaultSwipeRefresh(boolean isSwipeRefresh) {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setEnabled(isSwipeRefresh);
    }
  }

  private int findMax(int... lastPositions) {
    int max = Integer.MIN_VALUE;
    for (int value : lastPositions) {
      if (value > max) {
        max = value;
      }
    }
    return max;
  }

  /**
   * Set the parallax header of the recyclerview
   */
  public void setParallaxHeader(View header) {
    mHeader = new CustomRelativeWrapper(header.getContext());
    mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    mAdapter.setCustomHeaderView(mHeader);
  }

  /**
   * Set the on scroll method of parallax header
   */
  public void setOnParallaxScroll(OnParallaxScroll parallaxScroll) {
    mParallaxScroll = parallaxScroll;
    mParallaxScroll.onParallaxScroll(0, 0, mHeader);
  }

  private void translateHeader(float of) {
    float ofCalculated = of * SCROLL_MULTIPLIER;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mHeader.setTranslationY(ofCalculated);
    } else {
      TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
      anim.setFillAfter(true);
      anim.setDuration(0);
      mHeader.startAnimation(anim);
    }
    mHeader.setClipY(Math.round(ofCalculated));
    if (mParallaxScroll != null) {
      float left = Math.min(1, ((ofCalculated) / (mHeader.getHeight() * SCROLL_MULTIPLIER)));
      mParallaxScroll.onParallaxScroll(left, of, mHeader);
    }
  }

  public void setScrollViewCallbacks(ObservableScrollViewListener listener) {
    mCallbacks = listener;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    mPrevFirstVisiblePosition = ss.prevFirstVisiblePosition;
    mPrevFirstVisibleChildHeight = ss.prevFirstVisibleChildHeight;
    mPrevScrolledChildrenHeight = ss.prevScrolledChildrenHeight;
    mPrevScrollY = ss.prevScrollY;
    mScrollY = ss.scrollY;
    mChildrenHeights = ss.childrenHeights;
    super.onRestoreInstanceState(ss.getSuperState());
  }

  @Override public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.prevFirstVisiblePosition = mPrevFirstVisiblePosition;
    ss.prevFirstVisibleChildHeight = mPrevFirstVisibleChildHeight;
    ss.prevScrolledChildrenHeight = mPrevScrolledChildrenHeight;
    ss.prevScrollY = mPrevScrollY;
    ss.scrollY = mScrollY;
    ss.childrenHeights = mChildrenHeights;
    return ss;
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {

    if (mCallbacks != null) {
      switch (ev.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
          mFirstScroll = true;
          mDragging = true;
          mCallbacks.onDownMotionEvent();
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          //mIntercepted = false;
          mDragging = false;
          mCallbacks.onUpOrCancelMotionEvent(mObservableScrollState);
          break;
        default:
          break;
      }
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    Log.d("BloatedRecyclerView", "mCallbacks   " + (mCallbacks == null));
    if (mCallbacks != null) {
      switch (ev.getActionMasked()) {
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          //mIntercepted = false;
          mDragging = false;
          mCallbacks.onUpOrCancelMotionEvent(mObservableScrollState);
          break;
        default:
          break;
      }
    }
    return super.onTouchEvent(ev);
  }

  public enum LayoutManagerType {
    LINEAR, GRID, STAGGERED_GRID
  }

  public interface OnLoadMoreListener {

    void loadMore(int itemsCount, int maxLastVisiblePosition);
  }

  public interface OnParallaxScroll {

    void onParallaxScroll(float percentage, float offset, View parallax);
  }

  /**
   * Custom layout for the Parallax Header.
   */
  public static class CustomRelativeWrapper extends RelativeLayout {

    private int mOffset;

    public CustomRelativeWrapper(Context context) {
      super(context);
    }

    @Override protected void dispatchDraw(Canvas canvas) {
      canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
      super.dispatchDraw(canvas);
    }

    public void setClipY(int offset) {
      mOffset = offset;
      invalidate();
    }
  }

  /**
   * This saved state class is a Parcelable and should not extend
   * {@link BaseSavedState} nor {@link android.view.AbsSavedState}
   * because its super class AbsSavedState's constructor
   * {@link android.view.AbsSavedState#AbsSavedState(Parcel)} currently passes null
   * as a class loader to read its superstate from Parcelable.
   * This causes {@link android.os.BadParcelableException} when restoring saved states.
   * <p/>
   * The super class "RecyclerView" is a part of the support library,
   * and restoring its saved state requires the class loader that loaded the RecyclerView.
   * It seems that the class loader is not required when restoring from RecyclerView itself,
   * but it is required when restoring from RecyclerView's subclasses.
   */
  static class SavedState implements Parcelable {

    public static final SavedState EMPTY_STATE = new SavedState() {
    };

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      @Override public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      @Override public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };

    int prevFirstVisiblePosition;

    int prevFirstVisibleChildHeight = -1;

    int prevScrolledChildrenHeight;

    int prevScrollY;

    int scrollY;

    SparseIntArray childrenHeights;

    // This keeps the parent(RecyclerView)'s state
    Parcelable superState;

    /**
     * Called by EMPTY_STATE instantiation.
     */
    private SavedState() {
      superState = null;
    }

    /**
     * Called by onSaveInstanceState.
     */
    protected SavedState(Parcelable superState) {
      this.superState = !superState.equals(EMPTY_STATE) ? superState : null;
    }

    protected SavedState(Parcel in) {
      // Parcel 'in' has its parent(RecyclerView)'s saved state.
      // To restore it, class loader that loaded RecyclerView is required.
      Parcelable superState = in.readParcelable(RecyclerView.class.getClassLoader());
      this.superState = superState != null ? superState : EMPTY_STATE;

      prevFirstVisiblePosition = in.readInt();
      prevFirstVisibleChildHeight = in.readInt();
      prevScrolledChildrenHeight = in.readInt();
      prevScrollY = in.readInt();
      scrollY = in.readInt();
      childrenHeights = new SparseIntArray();
      final int numOfChildren = in.readInt();
      if (0 < numOfChildren) {
        for (int i = 0; i < numOfChildren; i++) {
          final int key = in.readInt();
          final int value = in.readInt();
          childrenHeights.put(key, value);
        }
      }
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      out.writeParcelable(superState, flags);

      out.writeInt(prevFirstVisiblePosition);
      out.writeInt(prevFirstVisibleChildHeight);
      out.writeInt(prevScrolledChildrenHeight);
      out.writeInt(prevScrollY);
      out.writeInt(scrollY);
      final int numOfChildren = childrenHeights == null ? 0 : childrenHeights.size();
      out.writeInt(numOfChildren);
      if (0 < numOfChildren) {
        for (int i = 0; i < numOfChildren; i++) {
          out.writeInt(childrenHeights.keyAt(i));
          out.writeInt(childrenHeights.valueAt(i));
        }
      }
    }

    public Parcelable getSuperState() {
      return superState;
    }
  }
}
