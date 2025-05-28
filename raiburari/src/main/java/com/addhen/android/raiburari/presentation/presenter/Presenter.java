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

package com.addhen.android.raiburari.presentation.presenter;

import androidx.annotation.UiThread;
import com.addhen.android.raiburari.presentation.view.UiView;

/**
 * Presenter interface that all App Presenters must implemented
 *
 * @author Henry Addo
 */
public interface Presenter<V extends UiView> {

  /**
   * Set or attach the view to this presenter
   */
  @UiThread void attachView(V view);

  /**
   * Will be called if the view has been destroyed. Typically this method will be invoked from
   * <code>Activity.detachView()</code> or <code>Fragment.onDestroyView()</code>
   */
  @UiThread void detachView();
}
