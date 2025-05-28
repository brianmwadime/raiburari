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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Provides support for vertical swipe to refresh
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
// Tracking the value in prevX in the switch construct. In this case this rule doesn't hold for
// the prevX variable
@SuppressWarnings("PMD.SingularField") public class VerticalSwipeRefreshLayout
    extends SwipeRefreshLayout {

  private final int mTouchSlop;

  private float prevX;

  public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        prevX = MotionEvent.obtain(event).getX();
        break;
      case MotionEvent.ACTION_MOVE:
        final float eventX = event.getX();
        float xDiff = Math.abs(eventX - prevX);
        if (xDiff > mTouchSlop) {
          return false;
        }
      default:
        break;
    }
    return super.onInterceptTouchEvent(event);
  }
}
