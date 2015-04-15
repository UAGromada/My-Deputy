package com.zeus.android.mydeputy.app.citizen.main_fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.api.request.BaseRequest;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Deputy;
import com.zeus.android.mydeputy.app.api.response.DeputyListResponse;
import com.zeus.android.mydeputy.app.citizen.CitizenMainActivity;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;
import com.zeus.android.mydeputy.app.ui.transformation.RoundedTransformation;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 2/13/15.
 */
public class ListFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = ListFragment.class.getSimpleName();

    private List<Deputy> mDeputies;
    private ExpandableListView mDeputyList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private DeputiesAdapter deputiesAdapter;

    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.citizen_main_list, container, false);

        mDeputies = App.getInstance().getLocalManager().getDeputyList();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) v.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        deputiesAdapter = new DeputiesAdapter(mDeputies);
        mDeputyList=(ExpandableListView) v.findViewById(R.id.deputy_list);
        mDeputyList.setAdapter(deputiesAdapter);

        activity.setTitle(activity.getResources().getString(R.string.title_deputy_list));
        getRequestManager().addListener(this);

        sendDeputyListRequest();
        setArrowPosition();

        return  v;
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
        sendDeputyListRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    @Override
    public void onRefresh() {
        sendDeputyListRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendDeputyListRequest();
                break;
        }
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_DEPUTY_LIST:
                DeputyListResponse deputyListResponse = new Gson().fromJson(response.toString(), DeputyListResponse.class);

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);

                mDeputyList.smoothScrollToPosition(0);
                mDeputies = deputyListResponse.getDeputies();
                deputiesAdapter.setDeputyList(mDeputies);

                getLocalManager().setDeputyList(mDeputies);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case RequestManager.REQUEST_DEPUTY_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendDeputyListRequest(){

        BaseRequest request = new BaseRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());

        getRequestManager().sendRequest(request, RequestManager.DEPUTIES_LIST_URL, TAG, RequestManager.REQUEST_DEPUTY_LIST);
        swipeRefreshLayout.setRefreshing(true);
    }

    public class DeputiesAdapter extends BaseExpandableListAdapter {

        private List<Deputy> deputyList;

        public DeputiesAdapter(List<Deputy> list) {
            this.deputyList = list;
        }

        private void addItemToList(Deputy d){
            deputyList.add(d);
            notifyDataSetChanged();
        }

        private void setDeputyList(List<Deputy> list){
            this.deputyList = list;
            notifyDataSetChanged();
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return deputyList.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Deputy deputy =(Deputy)getChild(groupPosition,childPosition);
            ChildHolder holder=null;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=infalInflater.inflate(R.layout.citizen_main_list_chiled_item, parent, false);
                holder=new ChildHolder(convertView);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.mText.setText("Розвернута, більш-меньш детальна інформація про депутата. Громадьска та політична діяльність");
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getLocalManager().setCurrentDeputy(deputy);
                    getLocalManager().setCurrentOpenFragmentId(CitizenMainActivity.FRAGMENT_INFO);
                    getPreferencesManager().setDeputyId(deputy.getId());
                    getPreferencesManager().setPartyName(deputy.getPartyName());
                    getPreferencesManager().setDeputyName(deputy.getName());
                    ((CitizenMainActivity)activity).openFragment(CitizenMainActivity.FRAGMENT_INFO);

                    ((CitizenMainActivity)activity).enableDrawerNavigation();
                    ((CitizenMainActivity)activity).refreshDrawerNavigation();
                }
            });

            return convertView;
        }

        private class ChildHolder {
            public TextView mText;
            public Button mButton;

            public ChildHolder(View container) {
                mText=(TextView)container.findViewById(R.id.list_deputies_chiled_item_text);
                mButton=(Button)container.findViewById(R.id.deputies_list_chiled_item_btn_choose);

                container.setTag(this);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return deputyList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return deputyList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            Deputy deputy = (Deputy) getGroup(groupPosition);
            Holder holder=null;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=infalInflater.inflate(R.layout.citizen_main_list_item, parent, false);
                holder=new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.mHeader.setText(deputy.getName());
            holder.mText.setText(deputy.getPartyName());

            Picasso.with(activity)
                    .load(deputy.getPhoto())
                    .placeholder(R.drawable.ic_action_account_circle)
                    .error(R.drawable.ic_action_account_circle)
                    .transform(new RoundedTransformation(100, 4))
                    .into(holder.mImage);

            return convertView;
        }

        private class Holder {
            public TextView mHeader;
            public ImageView mImage;
            public TextView mText;

            public Holder(View container) {
                mHeader = (TextView)container.findViewById(R.id.deputy_list_item_text);
                mImage = (ImageView)container.findViewById(R.id.deputies_list_item_image);
                mText = (TextView)container.findViewById(R.id.deputy_list_item_text_info);

                container.setTag(this);
            }
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }


    private void setArrowPosition(){
        DisplayMetrics metrics = new DisplayMetrics();
        super.onResume();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mDeputyList.setIndicatorBounds(width- getDipsFromPixel(35), width- getDipsFromPixel(5));

        } else {
            mDeputyList.setIndicatorBoundsRelative(width- getDipsFromPixel(35), width- getDipsFromPixel(5));
        }
    }
}
