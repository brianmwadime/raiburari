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

package com.addhen.android.raiburari.presentation.view.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.addhen.android.raiburari.presentation.BaseApplication;
import com.addhen.android.raiburari.presentation.di.component.ApplicationComponent;
import com.addhen.android.raiburari.presentation.di.module.ActivityModule;
import com.google.android.material.snackbar.Snackbar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Base {@link AppCompatActivity} class that all classes that wants to extend {@link
 * AppCompatActivity } must extend. Handles a lot of the repetitive initializations. For example
 * to load the layout and menu, you provide the super constructor with the layout and menu resIds
 * and it will automatically setup the layout and menu for you.
 *
 * @author Henry Addo
 */
public abstract class BaseActivity extends AppCompatActivity {

  /**
   * Layout resource id
   */
  protected final int mLayout;

  /**
   * Menu resource id
   */
  protected final int mMenu;

  private ActionBar mActionBarToolbar;

  public BaseActivity(int layout, int menu) {
    mLayout = layout;
    mMenu = menu;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getApplicationComponent().inject(this);
    if (mLayout != 0) {
      setContentView(mLayout);
    }

    mActionBarToolbar = getSupportActionBar();
    if (mActionBarToolbar != null) {
      mActionBarToolbar.setDisplayHomeAsUpEnabled(true);
      mActionBarToolbar.setHomeButtonEnabled(true);
    }
  }

  public void onDestroy() {
    super.onDestroy();
  }

  protected void setActionBarTitle(String title) {
    if (mActionBarToolbar != null) {
      mActionBarToolbar.setTitle(title);
    }
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
   * Shows a {@link android.widget.Toast} message with a Long life span of the Toast shown.
   *
   * @param message the message to be shown by the toast.
   */
  protected void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (mMenu != 0) {
      getMenuInflater().inflate(mMenu, menu);
    }
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  protected View fadeIn(final View view, final boolean animate) {
    if (view != null) {
      if (animate) {
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
      } else {
        view.clearAnimation();
      }
    }
    return view;
  }

  protected View fadeOut(final View view, final boolean animate) {
    if (view != null) {
      if (animate) {
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
      } else {
        view.clearAnimation();
      }
    }
    return view;
  }

  protected <V extends View> V setViewGone(final V view) {
    return setViewGone(view, true);
  }

  protected <V extends View> V setViewGone(final V view, final boolean gone) {
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
   * Adds a {@link Fragment} to this activity's layout.
   *
   * @param containerViewId The container view where to add the fragment.
   * @param fragment The fragment to be added.
   * @param tag The tag for the fragment
   */
  protected void addFragment(int containerViewId, Fragment fragment, String tag) {
    FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(containerViewId, fragment, tag);
    fragmentTransaction.commit();
  }

  /**
   * Adds a {@link Fragment} to this activity's layout.
   *
   * @param containerViewId The container view where to add the fragment.
   * @param fragment The fragment to be added.
   */
  protected void addFragment(int containerViewId, Fragment fragment) {
    FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(containerViewId, fragment);
    fragmentTransaction.commit();
  }

  /**
   * Replaces an existing {@link Fragment} in the activity.
   *
   * @param containerViewId The container view where to add the fragment.
   * @param fragment The fragment to be added.
   * @param tag The tag for the fragment
   */
  protected void replaceFragment(int containerViewId, Fragment fragment, String tag) {
    FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(containerViewId, fragment, tag);
    fragmentTransaction.commit();
  }

  /**
   * Replaces an existing {@link Fragment} in the activity.
   *
   * @param containerViewId the container view where to add the fragment.
   * @param fragment the fragment to be added.
   */
  protected void replaceFragment(int containerViewId, Fragment fragment) {
    FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(containerViewId, fragment);
    fragmentTransaction.commit();
  }

  /**
   * Gets the Main Application component for dependency injection.
   *
   * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
   */
  public ApplicationComponent getApplicationComponent() {
    return ((BaseApplication) getApplication()).getApplicationComponent();
  }

  /**
   * Gets an Activity module for dependency injection.
   *
   * @return {@link com.addhen.android.raiburari.presentation.di.module.ActivityModule}
   */
  protected ActivityModule getActivityModule() {
    return new ActivityModule(this);
  }
}
