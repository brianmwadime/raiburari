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

package com.addhen.android.raiburari.sample.app.presentation.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.addhen.android.raiburari.presentation.view.ui.fragment.BaseRecyclerViewFragment;
import com.addhen.android.raiburari.presentation.view.ui.listener.RecyclerViewItemTouchListenerAdapter;
import com.addhen.android.raiburari.presentation.view.ui.widget.BloatedRecyclerView;
import com.addhen.android.raiburari.sample.app.R;
import com.addhen.android.raiburari.sample.app.presentation.di.components.UserComponent;
import com.addhen.android.raiburari.sample.app.presentation.model.UserModel;
import com.addhen.android.raiburari.sample.app.presentation.presenter.UserListPresenter;
import com.addhen.android.raiburari.sample.app.presentation.view.UserListView;
import com.addhen.android.raiburari.sample.app.presentation.view.ui.adapter.UserAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Henry Addo
 */
public class MainFragment extends BaseRecyclerViewFragment<UserModel, UserAdapter>
    implements UserListView, RecyclerViewItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

  private static MainFragment mMainFragment;
  @Inject
  UserListPresenter userListPresenter;
//  @BindView(R.id.user_progressbar) ProgressBar mProgressBar;

  /**
   * BaseFragment
   */
  public MainFragment() {
    super(UserAdapter.class, R.layout.list_users, R.menu.menu_main);
    setRetainInstance(true);
  }

  public static MainFragment newInstance() {
    return new MainFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getComponent(UserComponent.class).inject(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    initialize();
    return view;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    userListPresenter.attachView(this);
    if (savedInstanceState == null) {
      userListPresenter.initialize();
    }
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    this.userListPresenter.detachView();
  }

  private void initialize() {
    RecyclerViewItemTouchListenerAdapter itemTouchListenerAdapter =
        new RecyclerViewItemTouchListenerAdapter(mBloatedRecyclerView.recyclerView, this);
    mBloatedRecyclerView.addItemDividerDecoration(getActivity());
    mBloatedRecyclerView.recyclerView.addOnItemTouchListener(itemTouchListenerAdapter);
    mBloatedRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        mBloatedRecyclerView.setRefreshing(true);
        // Hide progress bar when pull refresh is in action
        mBloatedRecyclerView.recyclerView.smoothScrollToPosition(0);
      }
    });
    mBloatedRecyclerView.enableInfiniteScroll();
    mBloatedRecyclerView.setOnLoadMoreListener(new BloatedRecyclerView.OnLoadMoreListener() {
      @Override public void loadMore(int itemsCount, int maxLastVisiblePosition) {

      }
    });
  }

  @Override public void showUserList(Collection<UserModel> userModelCollection) {
    if (userModelCollection != null) {
      List userList = new ArrayList(userModelCollection);
      mRecyclerViewAdapter.setItems(userList);
      mBloatedRecyclerView.setAdapter(mRecyclerViewAdapter);
    }
  }

  @Override public void showLoading() {
    // No-op
  }

  @Override public void hideLoading() {
    // No-op
  }

  @Override public void showRetry() {
    // No-op
  }

  @Override public void hideRetry() {
    // No-op
  }

  @Override public void showError(String message) {
    // No-op
  }

  @Override public Context getAppContext() {
    return getActivity().getApplicationContext();
  }

  @Override public void onItemClick(RecyclerView parent, View clickedView, int position) {

  }

  @Override public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
    // No-op
  }
}
