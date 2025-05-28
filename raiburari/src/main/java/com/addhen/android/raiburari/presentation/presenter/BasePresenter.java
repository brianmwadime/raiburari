/*
 * Copyright (c) 2016 Henry Addo
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

package com.addhen.android.raiburari.presentation.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.addhen.android.raiburari.presentation.view.UiView;
import java.lang.ref.WeakReference;

/**
 * @author Henry Addo
 */
public class BasePresenter<V extends UiView> implements Presenter<V> {

  private WeakReference<V> mViewRef;

  @UiThread @Override public void attachView(V view) {
    mViewRef = new WeakReference<V>(view);
  }

  /**
   * Checks if a view is attached to this presenter. You should always call this method before
   * calling {@link #getView()} to get the view instance.
   */
  @UiThread public boolean isViewAttached() {
    return mViewRef != null && mViewRef.get() != null;
  }

  /**
   * Get the attached view. You should always call {@link #isViewAttached()} to check if
   * the view is attached to avoid NullPointerExceptions.
   *
   * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
   */
  @UiThread @Nullable
  public V getView() {
    return mViewRef == null ? null : mViewRef.get();
  }

  @UiThread @Override public void detachView() {
    if (mViewRef != null) {
      mViewRef.clear();
      mViewRef = null;
    }
  }
}
