package com.zeus.android.mydeputy.app.deputy.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.api.request.BaseListRequest;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Appeal;
import com.zeus.android.mydeputy.app.api.request.AppealEditRequest;
import com.zeus.android.mydeputy.app.api.response.AppealListResponse;
import com.zeus.android.mydeputy.app.deputy.AppealCitizenActivity;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 1/28/15.
 */
public class AppealNewFragment extends BaseFragment implements
        RequestManager.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener{

    public static final String TAG = AppealNewFragment.class.getSimpleName();
    public static final String CUR_APPEAL = "cur_appeal";
    public static final int  REVIEW_DIALOG = 666;

    public static final String LOAD = "load";

    public static final String CUR_APPEAL_TITLE = "cur_appeal_title";
    public static final String CUR_APPEAL_TEXT = "cur_appeal_text";

    private List<Appeal> newAppealList;
    private ListView appealListView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ErrorLayout errorLayout;

    private int currentAppealPosition;

    private Activity activity;
    private AppealAdapter adapter;

    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_appeal_new, container, false);

        // TODO: get response with UserAppeals from RequestManager

        appealListView = (ListView) view.findViewById(R.id.deputy_appeal_list_new);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.secondary, R.color.main_light, R.color.main);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        newAppealList = getLocalManager().getCurrentDeputy().getAppealList();
        adapter = new AppealAdapter(filter(newAppealList));
        appealListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            currentAppealPosition = savedInstanceState.getInt(CUR_APPEAL);
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog) {
                showLoadDialog();
            }
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
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CUR_APPEAL, currentAppealPosition);
        outState.putBoolean(LOAD, loadDialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Appeal appeal = newAppealList.get(currentAppealPosition);

        switch(requestCode) {
            case REVIEW_DIALOG:
                if (resultCode == Activity.RESULT_OK) {
                    sendAppealEditRequest(appeal.getAppealId(), Appeal.STATE_ACCEPTED);
                } else if (resultCode == Activity.RESULT_CANCELED){
                    sendAppealEditRequest(appeal.getAppealId(), Appeal.STATE_ACCEPTED);
                }
                showLoadDialog();

                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.deputy_appeal_new_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Appeal currentAppeal = newAppealList.get(info.position);
            // For each fragment call his own method
            switch (item.getItemId()) {
                case R.id.deputy_appeal_review:
                    displayReview(currentAppeal.getTitle(), currentAppeal.getText());
                    return true;
                case R.id.deputy_appeal_info:
                    Intent i = new Intent(activity, AppealCitizenActivity.class);
                    i.putExtra(AppealCitizenActivity.CITIZEN_ID, currentAppeal.getCitizenId());
                    activity.startActivity(i);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return false;
    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type){
            case  RequestManager.REQUEST_APPEAL_EDIT:
                newAppealList.remove(currentAppealPosition);
                adapter.setAppealList(newAppealList);
                dismissLoadDialog();
                break;
            case RequestManager.REQUEST_APPEAL_LIST:
                AppealListResponse appealListResponse = new Gson().fromJson(response.toString(), AppealListResponse.class);
                newAppealList = appealListResponse.getAppeals();
                getLocalManager().getCurrentDeputy().setAppealList(newAppealList);
                adapter.setAppealList(filter(newAppealList));

                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type){
            case  RequestManager.REQUEST_APPEAL_EDIT:
                dismissLoadDialog();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                break;
            case RequestManager.REQUEST_APPEAL_LIST:
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                sendAppealListRequest();
                break;
        }
    }


    private List<Appeal> filter(List<Appeal> appealList){
        newAppealList = new ArrayList<>();
        for (Appeal appeal: appealList){
            if (appeal.getState() == Appeal.STATE_NEW) newAppealList.add(appeal);
        }
        return newAppealList;
    }

    private void sendAppealEditRequest(int appealId, int state){
        AppealEditRequest request = new AppealEditRequest();
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setEmail(getPreferencesManager().getEmail());
        request.setAppeal_id(appealId);
        request.setParams(new AppealEditRequest.Params(state));

        getRequestManager().sendRequest(request, RequestManager.APPEAL_EDIT_URL, TAG, RequestManager.REQUEST_APPEAL_EDIT);
    }

    public void sendAppealListRequest(){
        BaseListRequest request = new BaseListRequest();
        request.setHash(new HashGenerator().hash(getPreferencesManager().getPassword()));
        request.setEmail(getPreferencesManager().getEmail());

        getRequestManager().sendRequest(request, RequestManager.APPEAL_LIST_URL, TAG, RequestManager.REQUEST_APPEAL_LIST);
    }

    @Override
    public void onRefresh() {
        sendAppealListRequest();
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
                    registerForContextMenu(appealListView);
                    activity.openContextMenu(v);
                    currentAppealPosition = position;
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
                mDate = (TextView)container.findViewById(R.id.item_data);

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
	    DialogFragment newFragment = ReviewAppealFragment.newInstance(title, text);
        newFragment.setRetainInstance(true);
        newFragment.setTargetFragment(this, REVIEW_DIALOG);
	    newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
	    newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
	}

	public static class ReviewAppealFragment extends DialogFragment {

        public static ReviewAppealFragment newInstance(String title, String text){

            ReviewAppealFragment reviewAppealFragment = new ReviewAppealFragment();
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

            btnAccept.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    getDialog().cancel();
				}
	              });

            btnReject.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
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

    private void showLoadDialog() {
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_create_appeal));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void dismissLoadDialog() {
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
            loadDialog = false;
        }
    }
//
//    /**
//     *  Method that open ChooseFragmentDialog
//     *  @author Rosty Vasiukov
//     */
//    public void displayChooser() {
//        DialogFragment newFragment = new ChooserAppealFragment();
//        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
//        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
//    }
//
//    public static class ChooserAppealFragment extends DialogFragment {
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//            View v = inflater.inflate(R.layout.deputy_main_appeal_new_chooser, container);
//
//            Rect displayRectangle = new Rect();
//            Window window = getDialog().getWindow();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//            getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_white_with_corners));
//
//            v.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
//
//            // TODO: get helpers list from server
//
//            //////////////// For debug
//            List<String> persons = new ArrayList<>();
//            persons.add("Депутат");
//            persons.add("Заступник");
//            /////////////////////////////
//
//            final RadioGroup chooserContainer = (RadioGroup) v.findViewById(R.id.deputy_appeal_new_chooser_container);
//
//            // Dynamically add radio buttons to dialog
//            int radioID = 0;
//            for (String person: persons){
//
//                RadioButton radio = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.simple_element_radio_button, null);
//                radio.setText(person);
//                radio.setTextColor(getResources().getColor(R.color.secondary));
//                radio.setId(radioID);
//                chooserContainer.addView(radio);
//                radio.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        chooserContainer.check(view.getId());
//                    }
//                });
//                radioID++;
//            }
//
//            final int radioButtonIdCount = radioID;
//            // Watch for button clicks.
//            Button btnChoose = (Button)v.findViewById(R.id.deputy_appeal_new_chooser_btn_choose);
//            Button btnCancel = (Button)v.findViewById(R.id.deputy_appeal_new_chooser_btn_cancel);
//
//            btnChoose.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
//                    // TODO: send to server request with chosen person
//
//                    if(chooserContainer.getCheckedRadioButtonId() != -1) {
//                        RadioButton mRadio = (RadioButton) chooserContainer.findViewById(chooserContainer.getCheckedRadioButtonId());
//                        //appeal.setPerson(mRadio.getText().toString());
//                        getDialog().cancel();
//                    }
//                }
//            });
//
//            btnCancel.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    getDialog().cancel();
//                }
//            });
//
//            return v;
//        }
//    }
//
//    /**
//     *  Method that set current pressed appeal and open ReviewDialog
//     *  @author Rosty Vasiukov
//     */
//    public void onReviewPressed(int position){
//        currentAppealPosition = position;
//        setCurrentAppeal(newAppealList.get(position));
//        displayReview();
//    }


}
