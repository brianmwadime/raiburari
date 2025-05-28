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

package com.addhen.android.raiburari.presentation.view.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.google.android.material.snackbar.Snackbar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

/**
 * Base {@link android.app.Fragment} class that every Fragment in this app will have to implement.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BaseFragment extends Fragment {

  /**
   * Layout resource mId
   */
  protected final int mLayout;

  /**
   * Menu resource mId
   */
  protected final int mMenu;

  /**
   * BaseFragment
   *
   * @param menu mMenu resource mId
   */
  public BaseFragment(int layout, int menu) {
    this.mLayout = layout;
    this.mMenu = menu;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = null;
    if (mLayout != 0) {
      root = inflater.inflate(mLayout, container, false);
    }

    return root;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (this.mMenu != 0) {
      inflater.inflate(this.mMenu, menu);
    }
  }

  protected View fadeIn(final View view, final boolean animate) {
    if (view != null) {
      if (animate) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
      } else {
        view.clearAnimation();
      }
    }
    return view;
  }

  protected View fadeOut(final View view, final boolean animate) {
    if (view != null) {
      if (animate) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
      } else {
        view.clearAnimation();
      }
    }
    return view;
  }

  protected View setViewGone(final View view) {
    return setViewGone(view, true);
  }

  protected View setViewGone(final View view, final boolean gone) {
    if (view != null) {
      if (gone) {
        if (GONE != view.getVisibility()) {

          fadeOut(view, true);

          view.setVisibility(GONE);
        }
      } else {
        if (VISIBLE != view.getVisibility()) {
          view.setVisibility(VISIBLE);

          fadeIn(view, true);
        }
      }
    }
    return view;
  }

  /**
   * Replace every field annotated using @Inject annotation with the provided dependency
   * specified
   * inside a Dagger module value.
   */
  @SuppressWarnings("unchecked") protected <C> C getComponent(Class<C> componentType) {
    return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
  }

  /**
   * Shows a {@link Toast} message.
   *
   * @param resId A message resource
   */
  protected void showToast(@StringRes int resId) {
    showToast(getString(resId));
  }

  /**
   * Shows a {@link Toast} message.
   *
   * @param message A message string
   */
  protected void showToast(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
  }

  /**
   * Shows a simple {@link Snackbar}
   *
   * @param view The view to anchor the Snackbar to
   * @param message The message to be showed
   */
  protected void showSnackbar(View view, String message) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
  }

  /**
   * Shows a simple {@link Snackbar}
   *
   * @param view The view to anchor the Snackbar to
   * @param resId The message to be showed
   */
  protected void showSnackbar(View view, @StringRes int resId) {
    showSnackbar(view, getString(resId));
  }
}
