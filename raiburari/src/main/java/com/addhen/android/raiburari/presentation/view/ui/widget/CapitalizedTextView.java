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

import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;

/**
 * Custom view to capitalize the first letter of a sentence. Sadly Android doest support this out
 * of the box.
 */
public class CapitalizedTextView extends AppCompatTextView {

  public CapitalizedTextView(Context context) {
    this(context, null, 0);
  }

  public CapitalizedTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CapitalizedTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override public void setText(CharSequence txt, BufferType type) {
    CharSequence text = txt;
    if (text.length() > 0) {
      text = String.valueOf(text.charAt(0)).toUpperCase(Locale.getDefault()) + text.subSequence(1,
          text.length());
    }
    super.setText(text, type);
  }
}
