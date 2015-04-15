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
import com.zeus.android.mydeputy.app.model.Citizen;
import com.zeus.android.mydeputy.app.model.Quiz;
import com.zeus.android.mydeputy.app.deputy.AppealCitizenActivity;

import java.util.List;

/**
 * Created by admin on 1/29/15.
 */
public class AppealCitizenQuiz extends BaseFragment {

    public static final String TAG = AppealCitizenQuiz.class.getSimpleName();

    private ListView quizList;
    private Activity activity;

    private QuizAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deputy_main_appeal_citizen_quiz, container, false);

        quizList = (ListView) view.findViewById(R.id.deputy_appeal_citizen_info_quiz_list);
        Citizen citizen = ((AppealCitizenActivity) activity).getCitizen();

        adapter = new QuizAdapter(citizen.getQuizList());
        quizList.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public class QuizAdapter extends BaseAdapter {

        private List<Quiz> quizList;

        public QuizAdapter(List<Quiz> appeals) {
            quizList = appeals;
        }

        @Override
        public int getCount() {
            return quizList.size();
        }

        @Override
        public Object getItem(int position) {
            return quizList.get(position);
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

            Quiz appeal= (Quiz) getItem(position);

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
