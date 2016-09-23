package com.percolate.mentions.sample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * An onClick listener for items in a RecyclerView. This was taken from the sample provided in the StickyHeadersRecyclerView library.
 * https://github.com/timehop/sticky-headers-recyclerview/blob/master/sample/src/main/java/com/timehop/stickyheadersrecyclerview/sample/RecyclerItemClickListener.java
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
  private final OnItemClickListener mListener;

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  private final GestureDetector mGestureDetector;

  public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
    mListener = listener;
    mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
      @Override public boolean onSingleTapUp(MotionEvent e) {
        return true;
      }
    });
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
    View childView = view.findChildViewUnder(e.getX(), e.getY());
    if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
      mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
    }
    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // do nothing
  }
}