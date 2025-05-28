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

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import javax.inject.Inject;

/**
 * RxJava based application {@link SharedPreferences}
 *
 * @author Henry Addo
 */
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation") public class RxSharedPreferences {

  private final SharedPreferences mSharedPreferences;

  private final Observable<String> mChangedKeys;

  @Inject public RxSharedPreferences(@NonNull final SharedPreferences sharedPreferences) {
    mSharedPreferences = sharedPreferences;
    this.mChangedKeys = Observable.create(new ObservableOnSubscribe<String>() {
      @Override public void subscribe(final ObservableEmitter<String> e) {
        if (!e.isDisposed()) {
          final SharedPreferences.OnSharedPreferenceChangeListener listener =
              new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                    String key) {
                  e.onNext(key);
                  e.onComplete();
                }
              };

          Disposable d = Disposables.fromAction(new Action() {
            @Override public void run() {
              sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
            }
          });
          e.setDisposable(d);
          sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        }
      }
    }).share();
  }

  private static Predicate<String> matchesKey(final String key) {
    return new Predicate<String>() {
      @Override public boolean test(String value) {
        return key.equals(value);
      }
    };
  }

  public Observable<String> getString(String key) {
    return getString(key, null);
  }

  public Observable<String> getString(String key, final String defaultValue) {
    return mChangedKeys.filter(matchesKey(key)).startWith(key).map(new Function<String, String>() {
      @Override public String apply(String changedKey) {
        return mSharedPreferences.getString(changedKey, defaultValue);
      }
    });
  }

  public Consumer<String> setString(final String key) {
    return new Consumer<String>() {
      @Override public void accept(String value) {
        mSharedPreferences.edit().putString(key, value).apply();
      }
    };
  }

  public Observable<Boolean> getBoolean(String key) {
    return getBoolean(key, null);
  }

  public Observable<Boolean> getBoolean(String key, final Boolean defaultValue) {
    return mChangedKeys.filter(matchesKey(key)).startWith(key).map(new Function<String, Boolean>() {
      @Override public Boolean apply(String changedKey) {
        return mSharedPreferences.getBoolean(changedKey, defaultValue);
      }
    });
  }

  public Consumer<Boolean> setBoolean(final String key) {
    return new Consumer<Boolean>() {
      @Override public void accept(Boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
      }
    };
  }

  public Observable<Integer> getInt(String key) {
    return getInt(key, null);
  }

  public Observable<Integer> getInt(String key, final Integer defaultValue) {
    return mChangedKeys.filter(matchesKey(key)).startWith(key).map(new Function<String, Integer>() {
      @Override public Integer apply(String changedKey) {
        return mSharedPreferences.getInt(changedKey, defaultValue);
      }
    });
  }
}
