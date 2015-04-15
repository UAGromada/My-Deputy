package com.zeus.android.mydeputy.app.local;

import com.zeus.android.mydeputy.app.App;
import com.zeus.android.mydeputy.app.model.Deputy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/2/15.
 */
public class LocalManager {

    private List<Deputy> deputyList;
    private Deputy currentDeputy;

    private int currentOpenFragmentId;

    public int getCurrentOpenFragmentId() {
        return currentOpenFragmentId;
    }

    public void setCurrentOpenFragmentId(int currentOpenFragmentId) {
        this.currentOpenFragmentId = currentOpenFragmentId;
    }

    public void setDeputyList(List<Deputy> deputyList) {
        this.deputyList = deputyList;
    }

    public List<Deputy> getDeputyList() {
        if (deputyList == null){
            deputyList = new ArrayList<>();
        }
        return deputyList;
    }

    public void setCurrentDeputy(Deputy currentDeputy) {
        if (getCurrentDeputy() != null){
            if (getCurrentDeputy().getId() != currentDeputy.getId()){
                this.currentDeputy = null;
                this.currentDeputy = currentDeputy;
            }
        }
        this.currentDeputy = currentDeputy;
    }

    public Deputy getCurrentDeputy() {
        if (currentDeputy == null ){
            currentDeputy = new Deputy(App.getInstance().getPreferencesManager().getDeputyId());
        }
        return currentDeputy;
    }
}
