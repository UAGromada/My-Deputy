package com.zeus.android.mydeputy.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.api.request.BaseRequest;
import com.zeus.android.mydeputy.app.citizen.CitizenMainActivity;
import com.zeus.android.mydeputy.app.deputy.DeputyMainActivity;
import com.zeus.android.mydeputy.app.util.HashGenerator;
import com.zeus.android.mydeputy.app.local.PreferencesManager;
import com.zeus.android.mydeputy.app.MainActivity;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.api.response.LoginResponse;

import org.json.JSONObject;

/**
 * Created by admin on 1/26/15.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener, RequestManager.OnResponseListener{

    public static final String TAG = LoginFragment.class.getSimpleName();
    public static final String LOAD = "load";

    private ImageView passwordImg;
    private ImageView emailImg;

    private EditText email;
    private EditText password;
    private CheckBox saveLogin;

    private LoadDialogFragment loginLoadDialogFragment;
    private boolean loadDialog = false;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        email = (EditText) view.findViewById(R.id.edit_log_email);
        password = (EditText) view.findViewById(R.id.edit_log_password);
        saveLogin = (CheckBox) view.findViewById(R.id.register_save);
        passwordImg = (ImageView) view.findViewById(R.id.login_img_password);
        emailImg = (ImageView) view.findViewById(R.id.login_img_email);

        view.findViewById(R.id.button_log_login).setOnClickListener(this);
        view.findViewById(R.id.link_to_register).setOnClickListener(this);

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.text_login));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_log_login:

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (emailText.isEmpty()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.email_error), Toast.LENGTH_SHORT).show();
                }else if(passwordText.isEmpty()){
                    Toast.makeText(getActivity(), getResources().getString(R.string.password_error), Toast.LENGTH_SHORT).show();
                }else {

                    BaseRequest request = new BaseRequest();
                    request.setEmail(emailText);
                    request.setHash(new HashGenerator().hash(passwordText));

                    getRequestManager().sendRequest(request, RequestManager.LOGIN_URL, TAG, RequestManager.REQUEST_LOGIN);

                    openLoginLoadDialog();
                }
                break;
            case R.id.link_to_register:
                ((MainActivity)activity).openRegisterFragment();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            loadDialog = savedInstanceState.getBoolean(LOAD);
            if (loadDialog){
                openLoginLoadDialog();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loginLoadDialogFragment != null ){
            loginLoadDialogFragment.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOAD, loadDialog);
    }


    private void openLoginLoadDialog(){
        loadDialog = true;
        loginLoadDialogFragment = LoadDialogFragment.newInstance(getResources().getString(R.string.txt_load_login));
        loginLoadDialogFragment.setRetainInstance(true);
        loginLoadDialogFragment.setCancelable(false);
        loginLoadDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Light_Dialog_FixedSize);
        loginLoadDialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void dismissDialog(){
        if (loginLoadDialogFragment != null) {
            loginLoadDialogFragment.dismiss();
            loadDialog = false;
        }
    }


    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_LOGIN:
                LoginResponse loginResponse = new Gson().fromJson(response.toString(), LoginResponse.class);

                int role = loginResponse.getUser().getRole();
                login(role);
                PreferencesManager preferenceManager = App.getInstance().getPreferencesManager();

                if (role == MainActivity.DEPUTY) {
                    preferenceManager.setDeputyName(loginResponse.getUser().getFull_name());
                    preferenceManager.setPartyName(loginResponse.getUser().getParty_name());
                }else{
                    preferenceManager.setUserName(loginResponse.getUser().getFull_name());
                }

                preferenceManager.setEmail(email.getText().toString());
                preferenceManager.setPassword(password.getText().toString());

                if (saveLogin.isChecked()){

                    // Save email and pass to login automatically
                    preferenceManager.setSaveLogin(true);
                    preferenceManager.setRole(role);
                }

                dismissDialog();
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {
        switch (type) {
            case RequestManager.REQUEST_LOGIN:
                dismissDialog();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void login(int role){
        Class c;
        if (role == MainActivity.CITIZEN) {
            c = CitizenMainActivity.class;
        } else {
            c = DeputyMainActivity.class;
        }

        Intent i = new Intent(activity, c);
        activity.startActivity(i);
        activity.finish();
    }
}
