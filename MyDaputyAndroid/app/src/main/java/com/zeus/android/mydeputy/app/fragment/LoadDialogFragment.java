package com.zeus.android.mydeputy.app.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeus.android.mydeputy.app.R;

/**
 * Created by admin on 2/11/15.
 */
public class LoadDialogFragment extends DialogFragment {

    public static final String DIALOG_TEXT = "dialog_text";

    public static LoadDialogFragment newInstance(String text) {
        LoadDialogFragment loadDialogFragment = new LoadDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_TEXT, text);
        loadDialogFragment.setArguments(bundle);

        return loadDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.simple_element_load_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_white_with_corners));

        String text = getArguments().getString(DIALOG_TEXT);
        TextView title = (TextView) v.findViewById(R.id.load_dialog_title);
        title.setText(text);

        return v;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
