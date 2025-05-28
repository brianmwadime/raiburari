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

package com.addhen.android.raiburari.sample.app.presentation.view;

import androidx.annotation.UiThread;
import com.addhen.android.raiburari.presentation.view.UiView;

/**
 * @author Henry Addo
 */
public interface LoadDataView extends UiView {

  /**
   * Shows a view with a progress bar indicating a loading process.
   */
  @UiThread void showLoading();

  /**
   * Hides a loading view.
   */
  @UiThread void hideLoading();

  /**
   * Shows a retry view in case of an error when retrieving data.
   */
  @UiThread void showRetry();

  /**
   * Hide a retry view shown if there was an error when retrieving data.
   */
  @UiThread void hideRetry();
}
