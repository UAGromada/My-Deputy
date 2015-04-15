package com.zeus.android.mydeputy.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.MainActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.api.request.RegisterRequest;

import org.json.JSONObject;

/**
 * Created by admin on 1/26/15.
 */
public class RegisterFragment extends BaseFragment implements RequestManager.OnResponseListener{

    public static final String TAG = RegisterFragment.class.getSimpleName();
    public static final String LOAD = "load";

    private EditText name;
    private EditText password;
    private EditText email;
    private EditText party;

    private ImageView personImg;
    private ImageView passwordImg;
    private ImageView emailImg;
    private ImageView partyImg;

    private RadioGroup radioGroup;
    private LinearLayout partyLayout;

    private Activity activity;
    private LoadDialogFragment loadDialogFragment;
    private boolean loadDialog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register, container, false);

        setHasOptionsMenu(true);

        name = (EditText) view.findViewById(R.id.edit_reg_fullname);
        password = (EditText) view.findViewById(R.id.edit_reg_password);
        email = (EditText) view.findViewById(R.id.edit_reg_email);
        party = (EditText) view.findViewById(R.id.edit_reg_section);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_role);
        partyLayout = (LinearLayout) view.findViewById(R.id.register_party_layout);

        partyLayout.setVisibility(View.GONE);

        personImg = (ImageView) view.findViewById(R.id.register_img_persone);
        passwordImg = (ImageView) view.findViewById(R.id.register_img_password);
        emailImg = (ImageView) view.findViewById(R.id.register_img_mail);
        partyImg = (ImageView) view.findViewById(R.id.register_img_paty);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_reg_citizen:
                        partyLayout.setVisibility(View.GONE);
                        break;
                    case R.id.radio_reg_deputat:
                        partyLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        getActivity().setTitle(getResources().getString(R.string.text_register));
        setOnEditFocus();

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * Change icon color on edittext focus
     */
    private void setOnEditFocus(){
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    personImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_social_person_bleu));
                    name.setTextColor(getResources().getColor(R.color.main));
                }else{
                    personImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_social_person));
                    name.setTextColor(getResources().getColor(R.color.secondary_dark));
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    passwordImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_lock_blue));
                    password.setTextColor(getResources().getColor(R.color.main));
                }else{
                    passwordImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_lock));
                    password.setTextColor(getResources().getColor(R.color.secondary_dark));
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email_blue));
                    email.setTextColor(getResources().getColor(R.color.main));
                }else{
                    emailImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_communication_email));
                    email.setTextColor(getResources().getColor(R.color.secondary_dark));
                }
            }
        });

        party.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    partyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_social_group_bleu));
                    party.setTextColor(getResources().getColor(R.color.main));
                }else{
                    partyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_social_group));
                    party.setTextColor(getResources().getColor(R.color.secondary_dark));
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.done_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.text_register));
        activity = (MainActivity)getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ((MainActivity)activity).openLoginFragment();
                return true;
            case R.id.tool_bar_done:

                int radioGroupId = radioGroup.getCheckedRadioButtonId();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String nameText = name.getText().toString();
                String partyText = party.getText().toString();

                if (emailText.isEmpty() || passwordText.toString().isEmpty() || nameText.isEmpty()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
                }else if(radioGroupId == -1) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.role_error), Toast.LENGTH_SHORT).show();
                }else if (radioGroupId == R.id.radio_reg_deputat && partyText.isEmpty()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.txt_error_all), Toast.LENGTH_SHORT).show();
                }else {

                    int role = 0;
                    switch (radioGroupId){
                        case R.id.radio_reg_citizen:
                            role = MainActivity.CITIZEN;
                            break;
                        case R.id.radio_reg_deputat:
                            role = MainActivity.DEPUTY;
                            break;
                    }

                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setRole(role);
                    registerRequest.setHash(new HashGenerator().hash(passwordText));
                    registerRequest.setEmail(emailText);
                    registerRequest.setFullName(nameText);
                    if(role == MainActivity.DEPUTY) registerRequest.setPartyName(partyText);

                    getRequestManager().sendRequest(registerRequest, RequestManager.REGISTER_URL, TAG, RequestManager.REQUEST_REGISTER);

                    openRegisterLoadDialog();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog){
                openRegisterLoadDialog();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadDialogFragment != null ){
            loadDialogFragment.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOAD, loadDialog);
    }

    private void openRegisterLoadDialog(){
        loadDialog = true;
        if (loadDialogFragment == null) {
            loadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_register));
            loadDialogFragment.setRetainInstance(true);
            loadDialogFragment.setCancelable(false);
            loadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        }
        loadDialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void dismissRegisterLoadDialog(){
        if (loadDialogFragment != null) {
            loadDialogFragment.dismiss();
            loadDialog = false;
        }
    }



    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_REGISTER:
                try{
                    dismissRegisterLoadDialog();
                    ((MainActivity) activity).openLoginFragment();
                }catch (IllegalStateException ex){}
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_REGISTER:
                dismissRegisterLoadDialog();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
