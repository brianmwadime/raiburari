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

package com.addhen.android.raiburari.presentation.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ChecksSdkIntAtLeast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * @author Henry Addo
 */
public final class Utils {

  private static final int DEFAULT_BUFFER_SIZE = 8192;

  private static int screenWidth;

  private static int screenHeight;

  private Utils() {
    // No instance allowed
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.KITKAT)
  public static boolean isKitKatOrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
  public static boolean isJellyBeanMR1OrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.JELLY_BEAN)
  public static boolean isJellyBeanOrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static boolean isICSOrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.HONEYCOMB)
  public static boolean isHoneycombOrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.GINGERBREAD)
  public static boolean isGingerbreadOrHigher() {
    return true;
  }

  public static boolean isGoogleTV(Context context) {
    return context.getPackageManager().hasSystemFeature("com.google.android.tv");
  }

  @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP)
  public static boolean isLollipopOrHigher() {
    // Hardcode lollipop version number because devices lower that lollipop don't have
    // the lollipop's version number
    return Build.VERSION.SDK_INT >= 21;
  }

  /**
   * Checks if {@link Environment}.MEDIA_MOUNTED is returned by {@code getExternalStorageState()}
   * and therefore external storage is read- and writeable.
   */
  public static boolean isExtStorageAvailable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * Whether there is any network connected.
   */
  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager == null) {
      return false;
    }
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public static boolean isRtlLayout() {
    if (Utils.isJellyBeanMR1OrHigher()) {
      int direction = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
      return direction == View.LAYOUT_DIRECTION_RTL;
    }
    return false;
  }

  /**
   * Whether there is an active WiFi connection.
   */
  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
  }

  /**
   * Copies the contents of one file to the other using {@link FileChannel}s.
   *
   * @param src source {@link File}
   * @param dst destination {@link File}
   */
  public static void copyFile(File src, File dst) throws IOException {
    FileInputStream in = new FileInputStream(src);
    FileOutputStream out = new FileOutputStream(dst);
    FileChannel inChannel = in.getChannel();
    FileChannel outChannel = out.getChannel();

    try {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      if (inChannel != null) {
        inChannel.close();
      }
      if (outChannel != null) {
        outChannel.close();
      }
    }

    in.close();
    out.close();
  }

  /**
   * Copies data from one input stream to the other using a buffer of 8 kilobyte in size.
   *
   * @param input {@link InputStream}
   * @param output {@link OutputStream}
   */
  public static int copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int count = 0;
    int n;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /**
   * Execute an {@link AsyncTask} on a thread pool.
   *
   * @param task Task to execute.
   * @param args Optional arguments to pass to {@link AsyncTask#execute(Object[])}.
   * @param <T> Task argument type.
   */
  @TargetApi(11) @SuppressWarnings("deprecation") public static <T> void executeAsyncTask(
      AsyncTask<T, ?, ?> task, T... args) {
    // TODO figure out how to subclass abstract and generalized AsyncTask,
    // then put this there
    if (Utils.isHoneycombOrHigher()) {
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
    } else {
      task.execute(args);
    }
  }

  public static boolean isTablet(Resources res) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      return res.getConfiguration().smallestScreenWidthDp >= 600;
    } else {
      return (res.getDisplayMetrics().widthPixels / res.getDisplayMetrics().density) >= 600;
    }
  }

  public static int dpToPx(int dp) {
    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
  }

  @TargetApi(13) public static int getScreenHeight(Context c) {
    if (screenHeight == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }

    return screenHeight;
  }

  @TargetApi(13) public static int getScreenWidth(Context c) {
    if (screenWidth == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenWidth = size.x;
    }
    return screenWidth;
  }
}
