package com.zeus.android.mydeputy.app.citizen.fragments;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.News;
import com.zeus.android.mydeputy.app.model.News.*;
import com.zeus.android.mydeputy.app.api.request.NewsAddCommentRequest;
import com.zeus.android.mydeputy.app.api.response.NewsSingleResponse;
import com.zeus.android.mydeputy.app.citizen.NewsSingleActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsSingleCommentFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        View.OnClickListener{

    public static final String TAG = NewsSingleCommentFragment.class.getSimpleName();
	public static final String COMMENT_DATA_KEY="SelectedNewsCommentFragmentArgsKey";

	private List<NewsComment> mComments;

	private ImageButton mAddCommentButton;
	private EditText mCommentTextView;
	private ListView mCommentsListView;
    private ErrorLayout errorLayout;

    private News news;

    private Activity activity;
    private CommentsFeedAdapter adapter;

    private List<OnCommentAdderListener> listeners;


    public interface OnCommentAdderListener{

        public void notifyCommentAddedListener(int id, int state);
    }

    public static NewsSingleCommentFragment newInstance(int newsId) {
        NewsSingleCommentFragment newsSingleCommentFragment = new NewsSingleCommentFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(NewsSingleActivity.NEWS_KEY, newsId);
        newsSingleCommentFragment.setArguments(bundle);

        return newsSingleCommentFragment;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.citizen_main_news_single_comments_feed, container, false);

		mAddCommentButton=(ImageButton)view.findViewById(R.id.deputy_single_news_comments_addcomment_button);
		mAddCommentButton.setOnClickListener(new AddCommentButtonListener());
        errorLayout= (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        ButtonStateChangeListener listener = new ButtonStateChangeListener();

		mCommentTextView=(EditText)view.findViewById(R.id.deputy_single_news_comments_comment_text);
		mCommentTextView.setOnEditorActionListener(listener);
        mCommentTextView.setPadding(getDipsFromPixel(16), getDipsFromPixel(8), getDipsFromPixel(16), getDipsFromPixel(8));

		mCommentsListView=(ListView)view.findViewById(R.id.deputy_single_news_comments_list);

		return view;
	}

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getResources().getString(R.string.txt_comments));
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }


    @Override
    public void onClick(View v) {
        ((NewsSingleActivity)activity).sendNewsSingleRequest();
    }


    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_SINGLE:

                NewsSingleResponse newsSingleResponse = new Gson().fromJson(response.toString(), NewsSingleResponse.class);
                news = newsSingleResponse.getNews();

                mComments = news.getComments();
                adapter = new CommentsFeedAdapter(getActivity().getLayoutInflater(),mComments);
                mCommentsListView.setAdapter(adapter);

                errorLayout.setVisibility(View.GONE);
                break;
            case RequestManager.REQUEST_NEWS_ADD_COMMENT:

                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_NEWS_SINGLE:
                errorLayout.setVisibility(View.VISIBLE);
                break;

            case RequestManager.REQUEST_NEWS_ADD_COMMENT:
                adapter.removeComment();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private class ButtonStateChangeListener implements OnEditorActionListener {
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			String text;

			text=mCommentTextView.getText().toString();
			if (TextUtils.isEmpty(text)) {
				mAddCommentButton.setEnabled(false);
			} else {
				mAddCommentButton.setEnabled(true);
			}
			return false;
		}
	}
	
	private class AddCommentButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			addCommentRequest();
		}
	}

    private void addCommentRequest(){

        String commentText = mCommentTextView.getText().toString();

        if (commentText.isEmpty()){
            Toast.makeText(activity, getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
        }else {
            NewsAddCommentRequest request = new NewsAddCommentRequest();
            request.setEmail(getPreferencesManager().getEmail());
            request.setHash(getPreferencesManager().getPasswordHash());
            request.setNewsId(news.getNewsId());
            request.setText(commentText);

            getRequestManager().sendRequest(request, RequestManager.NEWS_ADD_COMMENT_URL, TAG, RequestManager.REQUEST_NEWS_ADD_COMMENT);

            Calendar calendar = Calendar.getInstance();
            adapter.addNewComment(new NewsComment(calendar.getTimeInMillis()/1000, getPreferencesManager().getUserName(), commentText));
            mCommentTextView.getText().clear();
        }
    }
	
	private class CommentsFeedAdapter extends BaseAdapter {

        private List<Integer> positions;
		
		private LayoutInflater mLI;
		private List<NewsComment> mComments;
		
		public CommentsFeedAdapter(LayoutInflater inflater, List<NewsComment> comments) {
			super();
			mLI=inflater; mComments=comments;
            positions = new ArrayList<>();
		}

        public void addNewComment(NewsComment comment){
            mComments.add(comment);
            notifyDataSetChanged();
        }

        public void removeComment(){
            mComments.remove(mComments.size()-1);
            notifyDataSetChanged();
        }

		public int getCount() {
			return mComments.size();
		}

		public Object getItem(int pos) {
			return mComments.get(pos);
		}

		public long getItemId(int position) {
			return 0;
		}

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=null;

            if(convertView==null){

                convertView = mLI.inflate(R.layout.simple_item_two_lines_date, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // jus


                //
                // t use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            NewsComment item = (NewsComment)getItem(position);

            if(item != null) {
                holder.mHeader.setText(item.getAuthorName());
                holder.mText.setText(item.getCommentText());
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yy");
                String date = df.format(new Date(item.getCreated()*1000));
                holder.mDate.setText(date);
            }

            return convertView;
        }


        private class Holder {
            public TextView mHeader;
            public TextView mText;
            public TextView mDate;

            public Holder(View container) {
                mHeader =(TextView)container.findViewById(R.id.item_title);
                mText =(TextView)container.findViewById(R.id.item_text);
                mDate =(TextView)container.findViewById(R.id.item_data);
                mText.setSingleLine(false);
                // store the holder with the view.
                container.setTag(this);
            }
        }
	}

    private void addOnCommentAdderListener(OnCommentAdderListener listener){
        if (listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    private void removeOnCommentAdderListener(OnCommentAdderListener listener){
        if (listeners == null) listeners = new ArrayList<>();
        listeners.remove(listener);
    }

    private void notifyOnCommentAdderListener(int id, int state){
        if (listeners == null){
            for (OnCommentAdderListener listener: listeners){
                listener.notifyCommentAddedListener(id, state);
            }
        }
    }
}
