package com.zeus.android.mydeputy.app.api.response;

import com.zeus.android.mydeputy.app.model.Appeal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/11/15.
 */
public class AppealListResponse {

    private List<Appeal> appeals;

    public List<Appeal> getAppeals() {
        if (appeals == null){
            appeals = new ArrayList<>();
        }
        return appeals;
    }

    public void setAppeals(List<Appeal> appeals) {
        this.appeals = appeals;
    }
}
