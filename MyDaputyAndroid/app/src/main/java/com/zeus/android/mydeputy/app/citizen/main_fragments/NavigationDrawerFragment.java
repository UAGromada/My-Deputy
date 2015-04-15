package com.zeus.android.mydeputy.app.citizen.main_fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.BaseFragment;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.local.LocalManager;
import com.zeus.android.mydeputy.app.citizen.CitizenMainActivity;
import com.zeus.android.mydeputy.app.local.PreferencesManager;

import java.util.ArrayList;

/**
 * Created by admin on 2/13/15.
 */
public class NavigationDrawerFragment extends BaseFragment {

    public static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView drawerList;
    private TextView deputyName;
    private TextView deputyParty;
    private ImageView deputyPhoto;

    private Activity activity;

    private DrawerItemAdapter drawerItemAdapter;

    private boolean open = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.simple_navigation_drawer, container, false);

        deputyName = (TextView) view.findViewById(R.id.citizen_navifation_name_text);
        deputyParty = (TextView) view.findViewById(R.id.citizen_navifation_party_text);
        deputyPhoto = (ImageView) view.findViewById(R.id.citizen_navifation_photo);
        drawerList = (ListView)view.findViewById(R.id.citizen_navifation_menu_list);

        drawerItemAdapter = new DrawerItemAdapter(getLocalManager().getCurrentOpenFragmentId());

        refreshDeputy();
//        Picasso.with(getActivity())
//                .load(localManager.getCurrentDeputy().getPhoto())
//                .placeholder(R.drawable.ic_action_account_circle)
//                .error(R.drawable.ic_action_account_circle)
//                .transform(new RoundedTransformation(100, 4))
//                .into(deputyPhoto);

        drawerList.setAdapter(drawerItemAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((CitizenMainActivity)activity).openFragment(position);
                ((CitizenMainActivity)activity).closeNavigationDrawer();
            }
        });

        return view;
    }

    public void refreshDeputy(){
        deputyName.setText(getPreferencesManager().getDeputyName());
        deputyParty.setText(getPreferencesManager().getPartyName());
    }

    public void notifyNavigationDataChanges(){
        if (drawerItemAdapter != null) {
            drawerItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public boolean isOpen() {
        return open;
    }

    public void setUp(DrawerLayout dl, Toolbar tb) {
        mDrawerLayout = dl;
        mDrawerToggle = new ActionBarDrawerToggle(activity, dl, tb,
                R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                open = true;
                activity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                open = false;
                activity.invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public class DrawerItemAdapter extends BaseAdapter {

        private ArrayList<DrawerItem> listOfItems = setListContent();

        private ArrayList<DrawerItem> setListContent(){
            listOfItems = new ArrayList<>();
            listOfItems.add(new DrawerItem(R.string.title_deputy_info, R.drawable.ic_action_info_outline, R.drawable.ic_action_info_outline_blue));
            listOfItems.add(new DrawerItem(R.string.title_deputy_news, R.drawable.ic_notification_event_note, R.drawable.ic_notification_event_note_blue));
            listOfItems.add(new DrawerItem(R.string.title_deputy_appeals, R.drawable.ic_content_content_paste, R.drawable.ic_content_content_paste_blue));
            listOfItems.add(new DrawerItem(R.string.title_deputy_quiz, R.drawable.ic_av_equalizer, R.drawable.ic_av_equalizer_blue));
            listOfItems.add(new DrawerItem(R.string.title_deputy_list, R.drawable.ic_social_group, R.drawable.ic_social_group_bleu));

            return listOfItems;
        }


        private LayoutInflater myInflater;
        private int pageID;

        public DrawerItemAdapter(int pageID) {
            this.pageID = pageID;
            myInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return listOfItems.size();
        }

        @Override
        public Object getItem(int position) {
            return listOfItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {;
            Holder holder=null;

            if (convertView == null) {
                convertView = myInflater.inflate(R.layout.simple_item_single_line_icon, parent, false);
                holder=new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.mTitle.setText(activity.getResources().getString(listOfItems.get(position).getTitle()));

            if (position == App.getInstance().getLocalManager().getCurrentOpenFragmentId()){
                holder.mImage.setImageDrawable(activity.getResources().getDrawable(listOfItems.get(position).getSelectedImage()));
                holder.mTitle.setTextColor(getResources().getColor(R.color.main));
            }else{
                holder.mImage.setImageDrawable(activity.getResources().getDrawable(listOfItems.get(position).getImage()));
                holder.mTitle.setTextColor(getResources().getColor(R.color.secondary));
            }

            return convertView;
        }

        private class Holder {
            public TextView mTitle;
            public ImageView mImage;

            public Holder(View container) {
                mTitle = (TextView)container.findViewById(R.id.item_text);
                mImage = (ImageView)container.findViewById(R.id.item_image);

                container.setTag(this);
            }
        }

    }

    public class DrawerItem{
        private int title;
        private int image;
        private int selectedImage;

        public DrawerItem(int title, int image, int selectedImage) {
            this.title = title;
            this.image = image;
            this.selectedImage = selectedImage;
        }

        public int getTitle() {
            return title;
        }

        public int getImage() {
            return image;
        }

        public int getSelectedImage() {
            return selectedImage;
        }
    }
}
