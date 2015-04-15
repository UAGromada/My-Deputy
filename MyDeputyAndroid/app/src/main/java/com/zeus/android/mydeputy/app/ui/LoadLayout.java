package com.zeus.android.mydeputy.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zeus.android.mydeputy.app.R;

/**
 * Created by admin on 2/11/15.
 */
public class LoadLayout extends LinearLayout {

    private ImageView image;
    private TextView text;
    private Button retry;

    public LoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(getResources().getColor(R.color.background));
        setClickable(true);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.simple_load_layout, this, true);

        image = (ImageView) getChildAt(0);
        text = (TextView)getChildAt(1);
        retry = (Button) getChildAt(2);

        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_cloud_download));
    }

    public void setLoadState(String message){
        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_cloud_download));
        retry.setVisibility(GONE);
        text.setText(message);
    }


    public void setErrorState(String message){
        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_cloud_off));
        retry.setVisibility(VISIBLE);
        text.setText(message);
    }

    public void fade(boolean isFade){
        if (isFade){
            ObjectAnimator fade = ObjectAnimator.ofFloat(this, "alpha", 0f);
            fade.setDuration(1000);
            fade.start();
            setClickable(false);
        }else{
            ObjectAnimator fade = ObjectAnimator.ofFloat(this, "alpha", 100f);
            fade.setDuration(0);
            fade.start();
            setClickable(true);
        }

    }

    @Override
    public void setOnClickListener(OnClickListener listener){
        retry.setOnClickListener(listener);
    }
}
