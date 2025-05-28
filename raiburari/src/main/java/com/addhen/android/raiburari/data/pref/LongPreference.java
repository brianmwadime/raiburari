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

package com.addhen.android.raiburari.data.pref;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Preference for saving Long values
 *
 * @author Henry Addo
 */
public class LongPreference extends BasePreference<Long> {

  /**
   * Constructs a new {@link LongPreference}
   *
   * @param sharedPreferences SharedPreferences to be used for storing the value.
   * @param key The key for the preference
   */
  @Inject public LongPreference(SharedPreferences sharedPreferences, String key) {
    this(sharedPreferences, key, 0);
  }

  /**
   * Constructs a new {@link LongPreference}
   *
   * @param sharedPreferences SharedPreferences to be used for storing the value.
   * @param key The key for the preference
   * @param defaultValue The default value
   */
  public LongPreference(SharedPreferences sharedPreferences, String key, long defaultValue) {
    super(sharedPreferences, key, defaultValue);
  }

  @Override public Long get() {
    return getSharedPreferences().getLong(getKey(), getDefaultValue());
  }

  /**
   * Sets the long to be saved
   *
   * @param value The Integer value to be saved
   */
  @Override public void set(@NonNull Long value) {
    this.set((long) value);
  }

  @SuppressLint("CommitPrefEdits") public void set(long value) {
    SharedPreferences.Editor editor = getSharedPreferences().edit().putLong(getKey(), value);
    PREF_SAVER.save(editor);
  }
}
