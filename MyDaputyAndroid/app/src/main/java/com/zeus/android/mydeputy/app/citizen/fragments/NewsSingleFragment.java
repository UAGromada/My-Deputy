package com.zeus.android.mydeputy.app.citizen.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.model.News;
import com.zeus.android.mydeputy.app.citizen.NewsSingleActivity;

/**
 * Created by admin on 2/3/15.
 */
public class NewsSingleFragment extends BaseFragment {

    public static final String TAG = NewsSingleFragment.class.getSimpleName();

    private TextView mNewsTextView;
    private TextView mNewsTitleView;
    private FloatingActionButton mAddCommentButton;

    private Activity activity;

    private News news;

    public static NewsSingleFragment newInstance(int newsId) {
        NewsSingleFragment newsSingleFragment = new NewsSingleFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(NewsSingleActivity.NEWS_KEY, newsId);
        newsSingleFragment.setArguments(bundle);

        return newsSingleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.citizen_main_news_single, container, false);

        mNewsTextView = (TextView) view.findViewById(R.id.citizen_single_news_text);
        mNewsTitleView = (TextView) view.findViewById(R.id.citizen_single_news_title);

        mAddCommentButton = (FloatingActionButton) view.findViewById(R.id.deputy_single_news_addcomment);
        mAddCommentButton.setOnClickListener(new AddCommentButtonListener());

        news = getLocalManager().getCurrentDeputy().getNewsById(getArguments().getInt(NewsSingleActivity.NEWS_KEY));

        mNewsTitleView.setText(news.getTitle());
        mNewsTextView.setText(news.getText());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(news.getTitle());
    }

    private class AddCommentButtonListener implements View.OnClickListener {
        public void onClick(View v) {

            ((NewsSingleActivity)activity).openCommentsFragment();
        }
    }
}
