package com.zeus.android.mydeputy.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.citizen.fragments.NewsSingleCommentFragment;

/**
 * Created by admin on 2/20/15.
 */
public class CommentView extends LinearLayout {

    public static final int STATE_FAIL = 1;
    public static final int STATE_SUCCESS = 2;

    private Button button;

    public CommentView(Context context) {
        super(context);

        button = (Button)this.findViewById(R.id.comment_load_button);
        button.setText(getResources().getString(R.string.comment_sending));
        button.setEnabled(false);
        button.setVisibility(GONE);
    }

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setButtonVisible(){
        button.setVisibility(VISIBLE);
    }


    public void notifyCommentAddedListener(int state) {
        switch (state){
            case STATE_FAIL:
                button.setVisibility(GONE);
                break;
            case STATE_SUCCESS:
                button.setText(getResources().getString(R.string.load_error));
                button.setEnabled(true);
                break;
        }
    }
}
