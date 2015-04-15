package com.zeus.android.mydeputy.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zeus.android.mydeputy.app.R;

/**
 * Created by admin on 2/24/15.
 */
public class ErrorLayout extends LinearLayout {

    private Button retry;

    public ErrorLayout (Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.simple_error_layout, this, true);

        retry = (Button) findViewById(R.id.error_retry);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener){
        retry.setOnClickListener(listener);
    }
}