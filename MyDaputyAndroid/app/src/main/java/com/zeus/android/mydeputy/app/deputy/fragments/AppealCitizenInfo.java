package com.zeus.android.mydeputy.app.deputy.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;
import com.zeus.android.mydeputy.app.model.Citizen;
import com.zeus.android.mydeputy.app.api.response.CitizenInfoResponse;
import com.zeus.android.mydeputy.app.deputy.AppealCitizenActivity;

import org.json.JSONObject;

/**
 * Created by admin on 1/29/15.
 */
public class AppealCitizenInfo extends BaseFragment implements RequestManager.OnResponseListener {

    public static final String TAG = AppealCitizenInfo.class.getSimpleName();

    private TextView title;
    private TextView txt;
    private ImageView photo;

    private Activity activity;
    private Citizen citizen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_appeal_citizen_info, container, false);

        title = (TextView) view.findViewById(R.id.deputy_appeal_citizen_info_info_title);
        txt = (TextView) view.findViewById(R.id.deputy_appeal_citizen_info_info_txt);
        photo = (ImageView) view.findViewById(R.id.deputy_appeal_citizen_info_info_img);

        citizen = ((AppealCitizenActivity) activity).getCitizen();

        title.setText(citizen.getFullName());
        txt.setText("Рудина року! золотий призер! просто супермен, а іноді і чоловік паук!");

        Picasso.with(getActivity())
                .load(citizen.getPhoto())
                .placeholder(R.drawable.ic_action_account_circle)
                .error(R.drawable.ic_action_account_circle)
                .into(photo);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public void onRequestSuccess(JSONObject response, int type) {
        switch (type) {
            case RequestManager.REQUEST_CITIZEN_INFO:
                CitizenInfoResponse citizenInfoResponse = new Gson().fromJson(response.toString(), CitizenInfoResponse.class);
                citizen = citizenInfoResponse.getCitizen();
                title.setText(citizen.getFullName());
                txt.setText("Рудина року! золотий призер! просто супермен, а іноді і чоловік паук!");

                Picasso.with(getActivity())
                        .load(citizen.getPhoto())
                        .placeholder(R.drawable.ic_action_account_circle)
                        .error(R.drawable.ic_action_account_circle)
                        .into(photo);
                break;
        }
    }

    @Override
    public void onRequestFail(String message, int type) {

    }
}
