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

package com.addhen.android.raiburari.sample.app.presentation.view.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.addhen.android.raiburari.presentation.view.ui.activity.BaseActivity;
import com.addhen.android.raiburari.sample.app.R;
import com.addhen.android.raiburari.sample.app.presentation.di.components.DaggerUserComponent;
import com.addhen.android.raiburari.sample.app.presentation.di.components.UserComponent;
import com.addhen.android.raiburari.sample.app.presentation.view.ui.fragment.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements HasComponent<UserComponent> {

  Toolbar mToolbar;
  DrawerLayout mDrawerLayout;
  NavigationView mNavigationView;
  ViewPager viewPager;
  FloatingActionButton fab;
  TabLayout mTabLayout;
  private UserComponent userComponent;

  public MainActivity() {
    super(R.layout.activity_main, 0);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injector();
    initViews();
  }

  private void initViews() {
    setSupportActionBar(mToolbar);
    final ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setHomeAsUpIndicator(R.drawable.ic_menu);
      ab.setDisplayHomeAsUpEnabled(true);
    }

    mDrawerLayout = findViewById(R.id.drawer_layout);
    mNavigationView = findViewById(R.id.nav_view);
    viewPager = findViewById(R.id.viewpager);
    fab = findViewById(R.id.fab);
    mTabLayout = findViewById(R.id.tabs);
    mToolbar = findViewById(R.id.toolbar);

    if (mNavigationView != null) {
      setupDrawerContent(mNavigationView);
    }

    if (viewPager != null) {
      setupViewPager(viewPager);
    }

    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });

    mTabLayout.setupWithViewPager(viewPager);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupViewPager(ViewPager viewPager) {
    Adapter adapter = new Adapter(getSupportFragmentManager());
    adapter.addFragment(new MainFragment(), "Category 1");
    adapter.addFragment(new MainFragment(), "Category 2");
    adapter.addFragment(new MainFragment(), "Category 3");
    viewPager.setAdapter(adapter);
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
          }
        });
  }

  private void injector() {
    this.userComponent = DaggerUserComponent.builder()
        .applicationComponent(getApplicationComponent())
        .activityModule(getActivityModule())
        .build();
  }

  @Override public UserComponent getComponent() {
    return userComponent;
  }

  static class Adapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();

    private final List<String> mFragmentTitles = new ArrayList<>();

    public Adapter(FragmentManager fm) {
      super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
      mFragments.add(fragment);
      mFragmentTitles.add(title);
    }

    @Override public Fragment getItem(int position) {
      return mFragments.get(position);
    }

    @Override public int getCount() {
      return mFragments.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return mFragmentTitles.get(position);
    }
  }
}
