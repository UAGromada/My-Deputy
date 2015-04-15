package com.zeus.android.mydeputy.app.ui;

import com.zeus.android.mydeputy.app.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ExpandableTextView extends TextView implements OnClickListener {
	 private static final String TAG="ExpandableTextView";
	 private boolean isClicked = false;
	 private int trimLength = 150;
	 private String text = "";
	 private int spanLenght = 12;
	 private boolean spannable = true; 
	 
	 public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExpandableTextView(Context context, AttributeSet attrs) {
		 super(context, attrs);
		 TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
		 this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trim_length, 150);
		 this.text = typedArray.getString(R.styleable.ExpandableTextView_android_text);
		 this.spanLenght = typedArray.getInt(R.styleable.ExpandableTextView_span_length, 10);
		 this.spannable = typedArray.getBoolean(R.styleable.ExpandableTextView_spannable, false);
		 initialize();
	}

	public ExpandableTextView(Context context) {
		 super(context);
		 initialize();
	}
	
	private void initialize() {
		setOnClickListener(this);
		notifyChange();
	}

	@Override
	public void onClick(View v) {
		isClicked = !isClicked;
		notifyChange();
	}

	public void setTrimLength(int trimLength) {
		this.trimLength = trimLength;
		notifyChange();
	}

	public void setText(String text) {
		this.text = text;
		notifyChange();
	}

	public void setText(String text, int spanLength, boolean spannable) {
		this.text = text;
		this.spannable = spannable;
		notifyChange();
	}

	private void notifyChange() {
		if (!isClicked) {
			super.setText(getTrimmedText(), BufferType.SPANNABLE);
		} else {
			super.setText(this.text, BufferType.SPANNABLE);
		}
		if (spannable) {
			Spannable s = (Spannable) getText();
			StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
			s.setSpan(boldSpan, 0, spanLenght,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private String getTrimmedText() {
		return this.text.substring(0, trimLength) + ".....";
	} 
}
