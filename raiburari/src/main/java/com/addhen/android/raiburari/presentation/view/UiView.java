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

package com.addhen.android.raiburari.presentation.view;

/**
 * @author Henry Addo
 */

import android.content.Context;
import androidx.annotation.UiThread;

/**
 * Implement this interface to show error messages to the activity at implements it.
 * It almost facilitates presenting users with data from
 * {@link com.addhen.android.raiburari.presentation.presenter.Presenter} to Activities.
 *
 * @author Henry Addo
 */
public interface UiView {

  /**
   * Shows an error message
   *
   * @param message A string resource representing an error.
   */
  @UiThread void showError(String message);

  /**
   * Gets a {@link android.content.Context}.
   */
  @UiThread Context getAppContext();
}
