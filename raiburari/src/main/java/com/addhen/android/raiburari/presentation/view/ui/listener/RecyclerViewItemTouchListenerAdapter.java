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

package com.addhen.android.raiburari.presentation.view.ui.listener;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecyclerView does not have standard way to add click listeners to the items,
 * this RecyclerView.OnItemTouchListener intercepts touch events and translates them to simple
 * onItemClick and onItemLongClick callbacks.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class RecyclerViewItemTouchListenerAdapter extends GestureDetector.SimpleOnGestureListener
    implements RecyclerView.OnItemTouchListener {

  private final RecyclerViewOnItemClickListener mListener;

  private final RecyclerView mRecyclerView;

  private final GestureDetector mGestureDetector;

  public RecyclerViewItemTouchListenerAdapter(RecyclerView recyclerView,
      RecyclerViewOnItemClickListener listener) {
    if (recyclerView == null || listener == null) {
      throw new IllegalArgumentException("RecyclerView and Listener arguments can not be null");
    }
    mRecyclerView = recyclerView;
    mListener = listener;
    mGestureDetector = new GestureDetector(recyclerView.getContext(), this);
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    mGestureDetector.onTouchEvent(motionEvent);
    return false;
  }

  @Override public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    // No-op
  }

  @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // No-op
  }

  @Override public void onShowPress(MotionEvent e) {
    View view = getChildViewUnder(e);
    if (view != null) {
      view.setPressed(true);
    }
  }

  @Override public boolean onSingleTapUp(MotionEvent e) {
    View view = getChildViewUnder(e);
    if (view == null) {
      return false;
    }

    view.setPressed(false);
    int position = mRecyclerView.getChildAdapterPosition(view);
    mListener.onItemClick(mRecyclerView, view, position);
    return true;
  }

  public void onLongPress(MotionEvent e) {
    View view = getChildViewUnder(e);
    if (view == null) {
      return;
    }
    int position = mRecyclerView.getChildAdapterPosition(view);
    mListener.onItemLongClick(mRecyclerView, view, position);
    view.setPressed(false);
  }

  @Nullable private View getChildViewUnder(MotionEvent e) {
    return mRecyclerView.findChildViewUnder(e.getX(), e.getY());
  }

  public interface RecyclerViewOnItemClickListener {

    void onItemClick(RecyclerView parent, View clickedView, int position);

    void onItemLongClick(RecyclerView parent, View clickedView, int position);
  }
}
