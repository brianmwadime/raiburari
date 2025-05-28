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
import androidx.annotation.NonNull;
import javax.inject.Inject;

/**
 * Preference for saving Boolean values
 *
 * @author Henry Addo
 */
public class BooleanPreference extends BasePreference<Boolean> {

  /**
   * Constructs a new {@link BooleanPreference}
   *
   * @param sharedPreferences SharedPreferences to be used for storing the value.
   * @param key The key for the preference
   */
  @Inject public BooleanPreference(SharedPreferences sharedPreferences, String key) {
    this(sharedPreferences, key, false);
  }

  /**
   * Constructs a new {@link BooleanPreference}
   *
   * @param sharedPreferences SharedPreferences to be used for storing the value.
   * @param key The key for the preference
   * @param defaultValue The default value
   */
  public BooleanPreference(SharedPreferences sharedPreferences, String key, Boolean defaultValue) {
    super(sharedPreferences, key, defaultValue);
  }

  /**
   * Gets the saved Boolean
   *
   * @return The saved Boolean
   */
  @Override public Boolean get() {

    return getSharedPreferences().getBoolean(getKey(), getDefaultValue());
  }

  /**
   * Sets the Boolean to be saved
   *
   * @param value The Boolean value to be saved
   */
  @Override public void set(@NonNull Boolean value) {
    this.set((boolean) value);
  }

  /** Set the value for the preference */
  @SuppressLint("CommitPrefEdits") public void set(boolean value) {
    SharedPreferences.Editor editor = getSharedPreferences().edit().putBoolean(getKey(), value);
    PREF_SAVER.save(editor);
  }
}
