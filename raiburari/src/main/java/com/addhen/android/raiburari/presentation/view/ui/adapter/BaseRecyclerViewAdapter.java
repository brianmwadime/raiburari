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

package com.addhen.android.raiburari.presentation.view.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.addhen.android.raiburari.presentation.model.Model;
import com.addhen.android.raiburari.presentation.view.ui.widget.BloatedRecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Base RecyclerView Adapter class for all RecyclerView adapters for a specific Type {@link Model}
 *
 * @param <M> Model class
 * @author Henry Addo
 */
public abstract class BaseRecyclerViewAdapter<M extends Model>
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final List<M> mItems;
  public boolean isLoadMoreChanged;
  protected BloatedRecyclerView.CustomRelativeWrapper customHeaderView;
  private View mInfiniteScrollView;

  public BaseRecyclerViewAdapter() {
    mItems = new ArrayList<>();
  }

  public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

  /**
   * Returns the number of items in the adapter bound to the parent RecyclerView.
   *
   * @return The number of items in the bound adapter
   */
  public abstract int getAdapterItemCount();

  public BloatedRecyclerView.CustomRelativeWrapper getCustomHeaderView() {
    return customHeaderView;
  }

  /**
   * Set the header view of the adapter.
   */
  public void setCustomHeaderView(BloatedRecyclerView.CustomRelativeWrapper customHeaderView) {
    this.customHeaderView = customHeaderView;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

    if (position == ViewTypes.FOOTER) {
      RecyclerView.ViewHolder viewHolder = new CustomViewViewHolder(mInfiniteScrollView);
      if (getAdapterItemCount() == 0) {
        viewHolder.itemView.setVisibility(View.GONE);
      }
      return viewHolder;
    } else if (position == ViewTypes.HEADER) {
      if (customHeaderView != null) {
        return new CustomViewViewHolder(customHeaderView);
      }
    } else if (position == ViewTypes.CHANGED_FOOTER) {
      RecyclerView.ViewHolder viewHolder = new CustomViewViewHolder(mInfiniteScrollView);
      if (getAdapterItemCount() == 0) {
        viewHolder.itemView.setVisibility(View.GONE);
      }
      return viewHolder;
    }

    return onCreateViewHolder(viewGroup);
  }

  public List<M> getItems() {
    return mItems;
  }

  public void setItems(List<M> items) {
    mItems.clear();
    mItems.addAll(items);
    notifyDataSetChanged();
  }

  public M getItem(int position) {
    return mItems.get(position);
  }

  public void addItem(M item, int position) {
    int pos = position;
    mItems.add(pos, item);
    if (customHeaderView != null) {
      pos++;
    }
    notifyItemInserted(pos);
  }

  public void removeItem(M item) {
    int position = mItems.indexOf(item);
    if (position < 0) {
      return;
    }
    mItems.remove(customHeaderView != null ? position - 1 : position);
    notifyItemRemoved(position);
  }

  public void swipeInfiniteScrollView(View customview) {
    mInfiniteScrollView = customview;
    isLoadMoreChanged = true;
  }

  public View getInfiniteScrollView() {
    return mInfiniteScrollView;
  }

  public void setInfiniteScrollView(View customview) {
    mInfiniteScrollView = customview;
  }

  @Override public int getItemCount() {

    int headerOrFooter = 0;

    if (customHeaderView != null) {
      headerOrFooter++;
    }

    if (mInfiniteScrollView != null) {
      headerOrFooter++;
    }

    return getAdapterItemCount() + headerOrFooter;
  }

  @Override public int getItemViewType(int position) {
    if (position == getItemCount() - 1 && mInfiniteScrollView != null) {
      if (isLoadMoreChanged) {
        return ViewTypes.CHANGED_FOOTER;
      } else {
        return ViewTypes.FOOTER;
      }
    } else if (position == 0 && customHeaderView != null) {
      return ViewTypes.HEADER;
    } else {
      return ViewTypes.NORMAL;
    }
  }

  private static class ViewTypes {

    public static final int NORMAL = 0;

    public static final int HEADER = 1;

    public static final int FOOTER = 2;

    public static final int CHANGED_FOOTER = 3;
  }

  class CustomViewViewHolder extends RecyclerView.ViewHolder {

    CustomViewViewHolder(View itemView) {
      super(itemView);
    }
  }
}

