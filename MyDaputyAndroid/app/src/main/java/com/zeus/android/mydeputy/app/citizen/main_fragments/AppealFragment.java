package com.zeus.android.mydeputy.app.citizen.main_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.api.request.BaseListRequest;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Appeal;
import com.zeus.android.mydeputy.app.api.response.AppealListResponse;
import com.zeus.android.mydeputy.app.citizen.AppealCreateActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 2/13/15.
 */
public class AppealFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = AppealFragment.class.getSimpleName();

    private FloatingActionButton actionButton;
    private ExpandableListView mAppealListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private List<Appeal> mAppealList;
    private MyAppealsAdapter adapter;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.citizen_main_appeal, container, false);

        actionButton = (FloatingActionButton) v.findViewById(R.id.citizen_appeal_action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), AppealCreateActivity.class);
                startActivity(i);
            }
        });

        mAppealListView = (ExpandableListView) v.findViewById(R.id.deputy_appeals_rate_appeals_list);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) v.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        mAppealList = getLocalManager().getCurrentDeputy().getAppealList();
        adapter = new MyAppealsAdapter(activity.getLayoutInflater(), mAppealList);
        mAppealListView.setAdapter(adapter);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mAppealListView.setIndicatorBounds(getDipsFromPixel(16), getDipsFromPixel(46));

        } else {
            mAppealListView.setIndicatorBoundsRelative(getDipsFromPixel(16), getDipsFromPixel(46));
        }

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_appeals));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
        sendAppealListRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onRefresh() {
        sendAppealListRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendAppealListRequest();
                break;
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_APPEAL_LIST:
                AppealListResponse appealListResponse = new Gson().fromJson(response.toString(), AppealListResponse.class);
                mAppealList = appealListResponse.getAppeals();
                adapter.setAppealList(mAppealList);

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);

                getLocalManager().getCurrentDeputy().setAppealList(mAppealList);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_APPEAL_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void sendAppealListRequest(){
        BaseListRequest request = new BaseListRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setDeputyId(getPreferencesManager().getDeputyId());

        getRequestManager().sendRequest(request, RequestManager.APPEAL_LIST_URL, TAG, RequestManager.REQUEST_APPEAL_LIST);
    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }


    private class MyAppealsAdapter extends BaseExpandableListAdapter {

        private class ChildHolder {

            public class RatingClickListener implements View.OnClickListener {
                public void onClick(View v) {
                    Object rawTag=v.getTag();
                    Appeal tag;
                    int rate;
                    int deputyId, appealId;

                    if ((rawTag==null) || !(rawTag instanceof Appeal)) {
                        return;
                    }
                    tag=(Appeal)rawTag;
                    v.setEnabled(false);
                    rate=(int) FloatMath.ceil(mRatingBar.getRating());
                    // TODO Sent rating for this appeal to server,
                    // disable rate button and set rating bar to 'indicator mode' on success
                }
            }

            public TextView mText;
            public RatingBar mRatingBar;
            public ImageButton mRateButton;

            public ChildHolder(View container) {
                mText=(TextView)container.findViewById(R.id.deputy_appeals_rate_appeals_child_item_text);
                mRatingBar=(RatingBar)container.findViewById(R.id.deputy_appeals_rate_appeals_child_item_rating);
                mRateButton=(ImageButton)container.findViewById(R.id.deputy_appeals_rate_appeals_child_item_rate_button);
            }
        }

        private class Holder {
            public TextView mHeader;

            public Holder(View container) {
                mHeader=(TextView)container.findViewById(R.id.deputy_appeals_rate_appeals_item_text);
            }
        }

        private LayoutInflater mLI;
        private List<Appeal> mList;;

        private void setAppealList(List<Appeal> mList){
            this.mList = mList;
            notifyDataSetChanged();
        }

        public MyAppealsAdapter(LayoutInflater inflater, List<Appeal> list) {
            super();
            mLI=inflater;
            mList=list;
        }

        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        public Object getChild(int groupPosition, int childPosition) {
            return mList.get(groupPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return (groupPosition<<1)+1;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Appeal item = (Appeal)getChild(groupPosition,childPosition);
            Object rawHolder=null;
            ChildHolder holder=null;
            boolean isReusable=((convertView!=null) && ((rawHolder=convertView.getTag())!=null) && (rawHolder instanceof ChildHolder));

            if (!isReusable) {
                convertView=mLI.inflate(R.layout.citizen_main_appeals_childe_item, parent, false);
                holder=new ChildHolder(convertView);
            } else {
                holder=(ChildHolder)rawHolder;
            }

            holder.mText.setText(item.getText());
            holder.mRatingBar.setMax(5);

            holder.mRatingBar.setIsIndicator(true);
            holder.mRatingBar.setRating(4);
            holder.mRateButton.setVisibility(View.INVISIBLE);
            holder.mRateButton.setOnClickListener(null);
            holder.mRateButton.setTag(null);

//            if (Appeal.State.RATED.equals(itemState)) {
//                holder.mRatingBar.setIsIndicator(true);
//                holder.mRatingBar.setRating(item.getRate());
//                holder.mRateButton.setVisibility(View.INVISIBLE);
//                holder.mRateButton.setOnClickListener(null);
//                holder.mRateButton.setTag(null);
//            } else if (Appeal.State.ACCEPTED.equals(itemState) || Appeal.State.REJECTED.equals(itemState)) {
//                holder.mRateButton.setVisibility(View.VISIBLE);
//                holder.mRateButton.setOnClickListener(holder.new RatingClickListener());
//                holder.mRateButton.setTag(item);
//                holder.mRatingBar.setIsIndicator(false);
//                holder.mRatingBar.setRating(0);
//            } else {
//                throw new IllegalStateException();
//            }
            return convertView;
        }

        public Object getGroup(int groupPosition) {
            return mList.get(groupPosition).getTitle();
        }

        public int getGroupCount() {
            return mList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition<<1;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String item=(String)getGroup(groupPosition);
            Object rawHolder=null;
            Holder holder=null;
            boolean isReusable=((convertView!=null) && ((rawHolder=convertView.getTag())!=null) && (rawHolder instanceof Holder));

            if (!isReusable) {
                convertView=mLI.inflate(R.layout.citizen_main_appeals_item, parent, false);
                holder=new Holder(convertView);
            } else {
                holder=(Holder)rawHolder;
            }
            holder.mHeader.setText(item);
            return convertView;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
