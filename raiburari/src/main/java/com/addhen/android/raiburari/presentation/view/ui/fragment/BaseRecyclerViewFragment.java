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
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.addhen.android.raiburari.presentation.model.Model;
import com.addhen.android.raiburari.presentation.view.ui.adapter.BaseRecyclerViewAdapter;
import com.addhen.android.raiburari.presentation.view.ui.widget.BloatedRecyclerView;

/**
 * Base {@link android.app.ListFragment} that every fragment list will extend from.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BaseRecyclerViewFragment<M extends Model, L extends BaseRecyclerViewAdapter>
    extends BaseFragment {

  private static final String TAG = BaseRecyclerViewFragment.class.getSimpleName();
  /**
   * RecyclerViewAdapter class
   */
  private final Class<L> mRecyclerViewAdapterClass;
  /**
   * RecyclerViewAdapter
   */
  protected L mRecyclerViewAdapter;
  /**
   * RecyclerView
   */
  protected BloatedRecyclerView mBloatedRecyclerView;

  protected BaseRecyclerViewFragment(Class<L> adapterClass, int layout, int menu) {
    super(layout, menu);
    mRecyclerViewAdapterClass = adapterClass;
  }

  /**
   * Uses reflection to create a new instance of a class
   *
   * @param targetClass The class to create an instance
   * @return The created instance
   */
  private static <T> T createInstance(Class<?> targetClass) {
    try {
      return (T) targetClass.newInstance();
    } catch (IllegalAccessException e) {
      Log.e(TAG, "IllegalAccessException", e);
    } catch (IllegalStateException e) {
      Log.e(TAG, "IllegalStateException", e);
    } catch (SecurityException e) {
      Log.e(TAG, "SecurityException", e);
      for (StackTraceElement exception : e.getStackTrace()) {
        Log.e(TAG, String.format("%s", exception.toString()));
      }
    } catch (InflateException e) {
      Log.e(TAG, "InflateException", e);
    } catch (java.lang.InstantiationException e) {
      Log.e(TAG, "InstantiationException", e);
    }
    return null;
  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mBloatedRecyclerView.setAdapter(null);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    mBloatedRecyclerView = (BloatedRecyclerView) view.findViewById(android.R.id.list);
    if (mBloatedRecyclerView != null) {
      mRecyclerViewAdapter = BaseRecyclerViewFragment.createInstance(mRecyclerViewAdapterClass);
      mBloatedRecyclerView.setFocusable(true);
      mBloatedRecyclerView.setFocusableInTouchMode(true);
      mBloatedRecyclerView.setAdapter(mRecyclerViewAdapter);
      mBloatedRecyclerView.setHasFixedSize(true);
      mBloatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    return view;
  }
}
