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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.collection.LruCache;

import com.addhen.android.raiburari.R;

/**
 * @author Henry Addo
 */
public class FontSupportedTextView extends AppCompatTextView {

  public FontSupportedTextView(Context context) {
    this(context, null);
  }

  public FontSupportedTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FontSupportedTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    if (!isInEditMode()) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RaiFontSupportedTextView);
      if (a.hasValue(R.styleable.RaiFontSupportedTextView_fontFile)) {
        setFont(context, a.getString(R.styleable.RaiFontSupportedTextView_fontFile));
      }
      a.recycle();
    }
  }

  public void setFont(Context context, final String customFont) {
    final TypefaceManager typefaceManager = new TypefaceManager();
    final Typeface typeface = typefaceManager.getTypeface(context, customFont);
    if (typeface != null) {
      setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
      setTypeface(typeface);
    }
  }

  private static class TypefaceManager {

    private final LruCache<String, Typeface> mCache;

    TypefaceManager() {
      mCache = new LruCache<>(3);
    }

    public Typeface getTypeface(final Context context, final String filename) {
      Typeface typeface = mCache.get(filename);
      if (typeface == null) {
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + filename);
        mCache.put(filename, typeface);
      }
      return typeface;
    }
  }
}
