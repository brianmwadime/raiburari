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

package com.addhen.android.raiburari.sample.app.presentation.view.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.addhen.android.raiburari.presentation.view.ui.adapter.BaseRecyclerViewAdapter;
import com.addhen.android.raiburari.sample.app.R;
import com.addhen.android.raiburari.sample.app.presentation.model.UserModel;

/**
 * @author Henry Addo
 */
public class UserAdapter extends BaseRecyclerViewAdapter<UserModel>
    implements View.OnClickListener {

  private OnUserItemClickListener mOnUserItemClickListener;

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
    return new Widgets(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_item, parent, false));
  }

  @Override public int getAdapterItemCount() {
    return getItems().size();
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    if ((position < getItemCount()) && (customHeaderView != null ? position <= getAdapterItemCount()
        : position < getAdapterItemCount()) && (customHeaderView != null ? position > 0 : true)) {
      ((Widgets) viewHolder).title.setText(getItem(position).fullName);
      ((Widgets) viewHolder).email.setText(getItem(position).email);
      ((Widgets) viewHolder).more.setTag(position);
      ((Widgets) viewHolder).more.setOnClickListener(this);
    }
  }

  @Override public void onClick(View view) {
    if (view.getId() == R.id.btn_more) {
      if (mOnUserItemClickListener != null) {
        mOnUserItemClickListener.onMoreClick(view, (Integer) view.getTag());
      }
    }
  }

  public void setOnUserItemClickListener(OnUserItemClickListener onUserItemClickListener) {
    mOnUserItemClickListener = onUserItemClickListener;
  }

  public interface OnUserItemClickListener {

    void onMoreClick(View v, int position);
  }

  public class Widgets extends RecyclerView.ViewHolder {

    TextView title;

    TextView email;

    ImageButton more;

    public Widgets(View convertView) {
      super(convertView);
      title = (TextView) convertView.findViewById(R.id.title);
      email = (TextView) convertView.findViewById(R.id.email);
      more = (ImageButton) convertView.findViewById(R.id.btn_more);
    }
  }
}
