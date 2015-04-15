package com.zeus.android.mydeputy.app.deputy.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.api.request.BaseListRequest;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Appeal;
import com.zeus.android.mydeputy.app.api.response.AppealListResponse;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 1/28/15.
 */
public class AppealAcceptedFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = AppealAcceptedFragment.class.getSimpleName();

    public static final String CUR_APPEAL = "cur_appeal";

    public static final String CUR_APPEAL_TITLE = "cur_appeal_title";
    public static final String CUR_APPEAL_TEXT = "cur_appeal_text";

    private List<Appeal> acceptedAppealList;
    private ListView acceptedAppealListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private int currentAppealPosition;

    private Activity activity;
    private AppealAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_appeal_new, container, false);

        // TODO: get response with UserAppeals from RequestManager

        acceptedAppealListView = (ListView) view.findViewById(R.id.deputy_appeal_list_new);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        acceptedAppealList = filter(getLocalManager().getCurrentDeputy().getAppealList());

        adapter = new AppealAdapter(filter(acceptedAppealList));
        acceptedAppealListView.setAdapter(adapter);
        registerForContextMenu(acceptedAppealListView);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            currentAppealPosition = savedInstanceState.getInt(CUR_APPEAL);
        }
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CUR_APPEAL, currentAppealPosition);
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case RequestManager.REQUEST_APPEAL_LIST:
                AppealListResponse appealListResponse = new Gson().fromJson(response.toString(), AppealListResponse.class);
                List<Appeal> appealList = appealListResponse.getAppeals();
                getLocalManager().getCurrentDeputy().setAppealList(appealList);
                updateList(appealList);

                getLocalManager().getCurrentDeputy().setAppealList(appealList);

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
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

    public void updateList(List<Appeal> appealList){
        if (adapter == null){
            adapter = new AppealAdapter(filter(appealList));
        }else {
            adapter.setAppealList(filter(appealList));
        }
    }

    private List<Appeal> filter(List<Appeal> appealList){
        acceptedAppealList = new ArrayList<>();
        for (Appeal appeal: appealList){
            if (appeal.getState() == Appeal.STATE_ACCEPTED) acceptedAppealList.add(appeal);
        }
        return acceptedAppealList;
    }

    public class AppealAdapter extends BaseAdapter {

        private List<Appeal> appealList;

        public AppealAdapter(List<Appeal> list) {
            this.appealList = list;
        }

        public void setAppealList(List<Appeal> list){
            this.appealList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return appealList.size();
        }

        @Override
        public Object getItem(int position) {
            return appealList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=null;

            if(convertView==null){

                // inflate the layout
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.simple_item_two_lines_date, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            Appeal appeal= (Appeal) getItem(position);

            if(appeal != null){
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yy");
                String date = df.format(new Date(appeal.getCreated()*1000));

                holder.mHeader.setText(appeal.getTitle());
                holder.mText.setText(appeal.getText());
                holder.mDate.setText(date);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentAppealPosition = position;
                    Appeal currentAppeal = acceptedAppealList.get(position);
                    displayReview(currentAppeal.getTitle(), currentAppeal.getText());
                }
            });

            return convertView;
        }


        private class Holder {
            public TextView mHeader;
            public TextView mText;
            public TextView mDate;

            public Holder(View container) {
                mHeader =(TextView)container.findViewById(R.id.item_title);
                mText =(TextView)container.findViewById(R.id.item_text);
                mDate = (TextView) container.findViewById(R.id.item_data);

                // store the holder with the view.
                container.setTag(this);
            }
        }
    }

    /**
     *  Method that open ReviewFragmentDialog
     *  @author Rosty Vasiukov
     */
    private void displayReview(String title, String text) {
        DialogFragment newFragment = ReviewDialogFragment.newInstance(title, text);
        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    public void sendAppealListRequest(){
        BaseListRequest request = new BaseListRequest();
        request.setHash(new HashGenerator().hash(getPreferencesManager().getPassword()));
        request.setEmail(getPreferencesManager().getEmail());

        getRequestManager().sendRequest(request, RequestManager.APPEAL_LIST_URL, TAG, RequestManager.REQUEST_APPEAL_LIST);
    }


    public static class ReviewDialogFragment extends DialogFragment {

        public static ReviewDialogFragment newInstance(String title, String text){

            ReviewDialogFragment reviewAppealFragment = new ReviewDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(CUR_APPEAL_TITLE, title);
            bundle.putString(CUR_APPEAL_TEXT, text);
            reviewAppealFragment.setArguments(bundle);

            return reviewAppealFragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.deputy_main_appeal_review, container, false);

            Rect displayRectangle = new Rect();
            Window window = getDialog().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            v.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

            getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_white_with_corners));

            TextView title = (TextView)v.findViewById(R.id.deputy_appeal_new_review_title);
            TextView text = (TextView)v.findViewById(R.id.deputy_appeal_new_review_text);

            title.setText(getArguments().getString(CUR_APPEAL_TITLE));
            text.setText(getArguments().getString(CUR_APPEAL_TEXT));

            // Watch for button clicks.
            Button btnAccept = (Button)v.findViewById(R.id.deputy_appeal_new_review_btn_accept);
            Button btnReject = (Button)v.findViewById(R.id.deputy_appeal_new_review_btn_reject);

            btnAccept.setVisibility(View.GONE);
            btnReject.setText(getResources().getString(R.string.txt_close));
            btnReject.setTextColor(getResources().getColor(R.color.main));
            btnReject.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getDialog().cancel();
                }
            });

            return v;
        }

        @Override
        public void onDestroyView() {
            if (getDialog() != null && getRetainInstance())
                getDialog().setDismissMessage(null);
            super.onDestroyView();
        }
    }


    /**
     *  Method that generate sorted list with headers
     *  @author Rosty Vasiukov
     */
//    private List<Appeal> sortListByPerson(List<Appeal> list){
//
//        persons = new ArrayList<>();
//        position = new ArrayList<>();
//        List<Appeal> sortedList = new ArrayList<>();
//
//        for (int i = 0; i<list.size(); i++){
//
//            // Add person if person list is empty
//            if(persons.size()>0){
//
//                // If persons list dosnt contain person add person to list
//                if (!persons.contains(list.get(i).getPerson())){
//                    persons.add(list.get(i).getPerson());
//                }
//            }else{
//                persons.add(list.get(i).getPerson());
//            }
//        }
//
//        for (String person: persons){
//
//            // Add header position to position list
//            position.add(sortedList.size());
//            boolean header = false;
//
//            for (Appeal appeal: list){
//                if(appeal.getPerson().equals(person)){
//                    if (!header) {
//                        // Add header to sorted list
//                        sortedList.add(appeal);
//                    }
//                    sortedList.add(appeal);
//                }
//            }
//        }
//
//        return sortedList;
//    }

//    public class AppealHeaderAdapter extends BaseAdapter {
//
//        private List<Appeal> appealList;
//        private List<Integer> positions;
//
//        public AppealHeaderAdapter(List<Appeal> appeals, List<Integer> positions) {
//            appealList = appeals;
//            this.positions = position;
//        }
//
//        @Override
//        public int getCount() {
//            return appealList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return appealList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        private boolean checkIfHeader(int position) {
//            if (positions.contains(position)) {
//                return true;
//            }
//            return false;
//        }
//
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            Holder holder = null;
//
//            if (convertView == null) {
//
//                // inflate the layout
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                convertView = inflater.inflate(R.layout.deputy_main_appeal_accepted_item, parent, false);
//
//                // well set up the ViewHolder
//                holder = new Holder(convertView);
//
//            } else {
//                // we've just avoided calling findViewById() on resource everytime
//                // just use the viewHolder
//                holder = (Holder) convertView.getTag();
//            }
//
//            Appeal appeal = (Appeal) getItem(position);
//
//            // If Appeal is header add it to different view
//            if (appeal != null) {
//                if (checkIfHeader(position)) {
//                    holder.mHeader.setText(appeal.getPerson());
//                    holder.mHeader.setTextColor(getResources().getColor(R.color.main));
//                    holder.mText.setVisibility(View.GONE);
//                    holder.layout.setBackgroundColor(getResources().getColor(R.color.background));
//                    convertView.setClickable(false);
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
//                    convertView.setLongClickable(false);
//                    convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View v) {
//                            return true;
//                        }
//                    });
//                } else {
//                    holder.mHeader.setText(appeal.getTitle());
//                    holder.mText.setText(appeal.getText());
//
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            getActivity().openContextMenu(v);
//                        }
//                    });
//                }
//            }
//
//            return convertView;
//        }
//
//
//        private class Holder {
//            public TextView mHeader;
//            public TextView mText;
//            public LinearLayout layout;
//
//            public Holder(View container) {
//                layout = (LinearLayout) container.findViewById(R.id.deputy_appeal_all_item_layout);
//                mHeader = (TextView) container.findViewById(R.id.deputy_appeal_all_item_title);
//                mText = (TextView) container.findViewById(R.id.deputy_appeal_all_item_subtext);
//
//                // store the holder with the view.
//                container.setTag(this);
//            }
//        }
//    }
}
