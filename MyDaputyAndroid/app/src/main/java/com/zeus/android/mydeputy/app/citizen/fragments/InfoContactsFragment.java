package com.zeus.android.mydeputy.app.citizen.fragments;

import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.model.Deputy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoContactsFragment extends BaseFragment {

    public static final String TAG = InfoContactsFragment.class.getSimpleName();

    private TextView phone;
    private TextView office;
    private TextView email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.citizen_main_info_contacts, container, false);

        phone = (TextView) view.findViewById(R.id.info_phone);
        office = (TextView) view.findViewById(R.id.info_address);
        email = (TextView) view.findViewById(R.id.info_mail);

        Deputy.Contacts contacts = getLocalManager().getCurrentDeputy().getContacts();
        if (contacts != null) {
            phone.setText(contacts.getPhone());
            office.setText(contacts.getAddress());
            email.setText(contacts.getEmail());
        }

		return view;
	}


}
