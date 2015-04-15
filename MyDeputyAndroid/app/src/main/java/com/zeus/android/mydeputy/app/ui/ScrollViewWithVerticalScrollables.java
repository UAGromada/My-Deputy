package com.zeus.android.mydeputy.app.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Based on this article: http://stackoverflow.com/questions/6920137/android-viewpager-and-horizontalscrollview
 * @author dzhadan
 *
 */
public class ScrollViewWithVerticalScrollables extends ScrollView {

	private SparseIntArray mScrollableIds;
	private SparseArray<View> mScrollables;
	private Rect mHitRect;
	
	public ScrollViewWithVerticalScrollables(Context context) {
		super(context);
		init();
	}

	public ScrollViewWithVerticalScrollables(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollViewWithVerticalScrollables(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mScrollableIds=new SparseIntArray(); mScrollables=new SparseArray<View>(); mHitRect=new Rect();
	}
	
	public void addScrollableId(int id) {
		mScrollableIds.append(mScrollableIds.size(), id);
		
		View scrollable=findViewById(id);
		
		mScrollables.append(mScrollables.size(), scrollable);
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mScrollableIds.size()>0) {
			int i, n=mScrollableIds.size(), cid;
			View csv;
			
			for (i=0; i<n; i++) {
				csv=mScrollables.valueAt(i); cid=mScrollableIds.valueAt(i);
				if (csv==null) {
					csv=findViewById(cid);
					if (csv!=null) {
						mScrollables.setValueAt(i, csv);
					}
				}
				if (csv!=null) {
					csv.getHitRect(mHitRect);
	                if (mHitRect.contains((int) event.getX(), (int) event.getY())) {
	                    return false;
	                }
				}
			}
		}
        return super.onInterceptTouchEvent(event);
    }	
}
