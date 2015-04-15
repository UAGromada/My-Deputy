package com.zeus.android.mydeputy.app.deputy.main_fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Deputy;
import com.zeus.android.mydeputy.app.api.request.DeputyInfoEditRequest;
import com.zeus.android.mydeputy.app.api.request.DeputyInfoRequest;
import com.zeus.android.mydeputy.app.api.response.DeputyInfoResponse;
import com.zeus.android.mydeputy.app.fragment.LoadDialogFragment;
import com.zeus.android.mydeputy.app.ui.ErrorLayout;

import org.json.JSONObject;

/**
 * Created by admin on 2/16/15.
 */
public class InfoFragment extends BaseFragment implements RequestManager.OnResponseListener, View.OnClickListener{

    public static final String TAG = InfoFragment.class.getSimpleName();
    public static final String LOAD = "load";
    public static final String EDIT_MODE = "edit_mode";

    private EditText phone;
    private EditText office;
    private EditText email;
    private EditText programme;
    private EditText promises;

    private ImageView phoneImg;
    private ImageView officeImg;
    private ImageView emailImg;

    private FloatingActionButton actionButton;
    private ErrorLayout errorLayout;

    private Activity activity;
    private FragmentManager fragmentManager;
    private LoadDialogFragment loadDialogFragment;

    private boolean loadDialog = false;
    private boolean editMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_info, container, false);

        setHasOptionsMenu(true);

        phone = (EditText) view.findViewById(R.id.info_phone);
        office = (EditText) view.findViewById(R.id.info_address);
        email = (EditText) view.findViewById(R.id.info_mail);
        programme = (EditText) view.findViewById(R.id.info_programme);
        promises = (EditText) view.findViewById(R.id.info_promises);

        phoneImg = (ImageView) view.findViewById(R.id.info_phone_img);
        officeImg = (ImageView) view.findViewById(R.id.info_address_img);
        emailImg = (ImageView) view.findViewById(R.id.info_mail_img);

        actionButton = (FloatingActionButton) view.findViewById(R.id.info_action_btn);
        actionButton.setOnClickListener(this);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(this);

        fragmentManager = getActivity().getSupportFragmentManager();

        getActivity().setTitle(getResources().getString(R.string.title_deputy_info));
        setOnEditFocus();

        if (getLocalManager().getCurrentDeputy().getContacts() == null){
            sendDeputyInfoRequest();
        }else{
            setDeputyInfo(getLocalManager().getCurrentDeputy());
        }

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            editMode = savedInstanceState.getBoolean(EDIT_MODE);
            setEditMode(editMode);
            if (loadDialog){
                showLoadDialog();
            }
        }else {
            setEditMode(false);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequestManager().addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getRequestManager().removeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOAD, loadDialog);
        outState.putBoolean(EDIT_MODE, editMode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info_action_btn:
                if (!editMode){
                    setEditMode(true);
                }else {
                    sendDeputyInfoEditRequest();
                    showLoadDialog();
                }
                break;
            case R.id.load_retry:
                sendDeputyInfoRequest();
                break;
        }

    }

    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_DEPUTY_INFO_EDIT:
                dismissLoadDialog();
                setEditMode(false);
                sendDeputyInfoRequest();
                break;
            case RequestManager.REQUEST_DEPUTY_INFO:
                DeputyInfoResponse deputyInfoResponse = new Gson().fromJson(response.toString(), DeputyInfoResponse.class);
                setDeputyInfo(deputyInfoResponse.getDeputy());

                getLocalManager().getCurrentDeputy().setPromises(deputyInfoResponse.getDeputy().getPromises());
                getLocalManager().getCurrentDeputy().setProgram(deputyInfoResponse.getDeputy().getProgram());
                getLocalManager().getCurrentDeputy().setContacts(deputyInfoResponse.getDeputy().getContacts());

                errorLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_DEPUTY_INFO_EDIT:
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                dismissLoadDialog();
                break;
            case RequestManager.REQUEST_DEPUTY_INFO:
                errorLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setDeputyInfo(Deputy deputy){
        phone.setText(deputy.getContacts().getPhone());
        office.setText(deputy.getContacts().getAddress());
        email.setText(deputy.getContacts().getEmail());
        programme.setText(deputy.getProgram());
        promises.setText(deputy.getPromises());
    }

    private void sendDeputyInfoRequest(){
        DeputyInfoRequest request = new DeputyInfoRequest();
        request.setEmail(getPreferencesManager().getEmail());
        request.setHash(getPreferencesManager().getPasswordHash());
        request.setDeputyId(getPreferencesManager().getDeputyId());

        getRequestManager().sendRequest(request, RequestManager.DEPUTY_INFO_URL, TAG, RequestManager.REQUEST_DEPUTY_INFO);
    }

    private void sendDeputyInfoEditRequest(){

        String prog = programme.getText().toString();
        String prom = promises.getText().toString();

        DeputyInfoEditRequest request = new DeputyInfoEditRequest();
        request.setEmail(getPreferencesManager().getEmail());
        request.setHash(getPreferencesManager().getPasswordHash());

        DeputyInfoEditRequest.Params params = new DeputyInfoEditRequest.Params();
        params.setProgramme(prog.trim());
        params.setPromises(prom.trim());
        Deputy.Contacts contacts = new Deputy.Contacts();
        contacts.setEmail(email.getText().toString().trim());
        contacts.setPhone(phone.getText().toString().trim());
        contacts.setAddress(office.getText().toString().trim());

        params.setContacts(contacts);

        request.setParams(params);

        getRequestManager().sendRequest(request, RequestManager.USER_INFO_EDIT_URL, TAG, RequestManager.REQUEST_DEPUTY_INFO_EDIT);

    }
    /**
     * Change icon color on edittext focus
     */
    private void setOnEditFocus(){
//        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    phoneImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_call_blue));
//                    phone.setTextColor(getResources().getColor(R.color.main));
//                } else {
//                    phoneImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_call));
//                    phone.setTextColor(getResources().getColor(R.color.secondary_dark));
//                }
//            }
//        });
//
//        office.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    officeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_business_blue));
//                    office.setTextColor(getResources().getColor(R.color.main));
//                } else {
//                    officeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_business));
//                    office.setTextColor(getResources().getColor(R.color.secondary_dark));
//                }
//            }
//        });
//
//        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email_blue));
//                    email.setTextColor(getResources().getColor(R.color.main));
//                }else{
//                    emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email));
//                    email.setTextColor(getResources().getColor(R.color.secondary_dark));
//                }
//            }
//        });
    }

    private void showLoadDialog(){
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_deputy_info));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(fragmentManager, "dialog");
    }

    private void dismissLoadDialog() {
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
            loadDialog = false;
        }
    }

    private void setEditMode(boolean isEnable){
        phone.setEnabled(isEnable);
        office.setEnabled(isEnable);
        email.setEnabled(isEnable);
        programme.setEnabled(isEnable);
        promises.setEnabled(isEnable);

        editMode = isEnable;

        if (isEnable){
            actionButton.setImageResource(R.drawable.ic_content_save);
            phoneImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_call));
            officeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_business));
            emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email));

        }else{
            actionButton.setImageResource(R.drawable.ic_action_content_create);
            phoneImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_call_blue));
            officeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_business_blue));
            emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email_blue));
        }
    }
}
