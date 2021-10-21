package com.rn.recycleview;

import android.content.Context;
import androidx.core.view.NestedScrollingParent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import androidx.annotation.Nullable;

public class NestedRecyclerView extends RecyclerView implements NestedScrollingParent {
   private View nestedScrollTarget;
   private boolean nestedScrollTargetIsBeingDragged;
   private boolean nestedScrollTargetWasUnableToScroll;
   private boolean skipsTouchInterception;

   @Override
   public boolean dispatchTouchEvent(MotionEvent ev) {
      boolean temporarilySkipsInterception = this.nestedScrollTarget != null;
      if (temporarilySkipsInterception) {
         this.skipsTouchInterception = true;
      }

      boolean handled = super.dispatchTouchEvent(ev);
      if (temporarilySkipsInterception) {
         this.skipsTouchInterception = false;
         if (!handled || this.nestedScrollTargetWasUnableToScroll) {
            handled = super.dispatchTouchEvent(ev);
         }
      }

      return handled;
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent e) {
      return !this.skipsTouchInterception && super.onInterceptTouchEvent(e);
   }

   @Override
   public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
      if (target == this.nestedScrollTarget && !this.nestedScrollTargetIsBeingDragged) {
         if (dyConsumed != 0) {
            this.nestedScrollTargetIsBeingDragged = true;
            this.nestedScrollTargetWasUnableToScroll = false;
         } else if (dyConsumed == 0 && dyUnconsumed != 0) {
            this.nestedScrollTargetWasUnableToScroll = true;
            ViewParent var10000 = target.getParent();
            if (var10000 != null) {
               var10000.requestDisallowInterceptTouchEvent(false);
            }
         }
      }

   }

   @Override
   public void onNestedScrollAccepted(View child, View target, int axes) {
      if ((axes & 2) != 0) {
         this.nestedScrollTarget = target;
         this.nestedScrollTargetIsBeingDragged = false;
         this.nestedScrollTargetWasUnableToScroll = false;
      }

      super.onNestedScrollAccepted(child, target, axes);
   }

   @Override
   public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
      return (nestedScrollAxes & 2) != 0;
   }

   @Override
   public void onStopNestedScroll(View child) {
      this.nestedScrollTarget = (View)null;
      this.nestedScrollTargetIsBeingDragged = false;
      this.nestedScrollTargetWasUnableToScroll = false;
   }

   public NestedRecyclerView(Context context) {
      super(context);
   }

   public NestedRecyclerView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public NestedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }
}
