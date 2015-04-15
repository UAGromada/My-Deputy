package com.zeus.android.mydeputy.app.citizen.fragments;

import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.RequestManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

public class InfoProgramFragment extends BaseFragment {

    public static final String TAG = InfoProgramFragment.class.getSimpleName();

    private TextView programView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		programView=(TextView)inflater.inflate(R.layout.simple_text_view,container,false);
        programView.setText(getLocalManager().getCurrentDeputy().getProgram());

		return programView;
	}
}
