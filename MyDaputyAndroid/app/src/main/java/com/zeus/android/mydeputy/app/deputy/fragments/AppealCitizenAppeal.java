package com.zeus.android.mydeputy.app.deputy.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.model.Appeal;
import com.zeus.android.mydeputy.app.model.Citizen;
import com.zeus.android.mydeputy.app.deputy.AppealCitizenActivity;

import java.util.List;

/**
 * Created by admin on 1/29/15.
 */
public class AppealCitizenAppeal extends BaseFragment {

    public static final String TAG = AppealCitizenAppeal.class.getSimpleName();

    private ListView appealList;
    private Activity activity;

    private AppealAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_appeal_citizen_appeal, container, false);

        appealList = (ListView) view.findViewById(R.id.deputy_appeal_citizen_info_appeal_list);
        Citizen citizen = ((AppealCitizenActivity) activity).getCitizen();

        adapter = new AppealAdapter(citizen.getAppealList());
        appealList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    public class AppealAdapter extends BaseAdapter {

        private List<Appeal> appealList;

        public AppealAdapter(List<Appeal> appeals) {
            appealList = appeals;
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
                convertView = inflater.inflate(R.layout.simple_element_single_item, parent, false);

                // well set up the ViewHolder
                holder = new Holder(convertView);

            }else{
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                holder = (Holder) convertView.getTag();
            }

            Appeal appeal= (Appeal) getItem(position);

            if(appeal != null){
                holder.mHeader.setText(appeal.getTitle());
            }

            return convertView;
        }


        private class Holder {
            public TextView mHeader;

            public Holder(View container) {
                mHeader =(TextView)container.findViewById(R.id.deputy_single_item_txt);

                container.setTag(this);
            }
        }
    }
}
