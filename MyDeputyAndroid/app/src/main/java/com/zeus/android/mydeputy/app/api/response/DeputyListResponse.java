package com.zeus.android.mydeputy.app.api.response;

import com.zeus.android.mydeputy.app.model.Deputy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/11/15.
 */
public class DeputyListResponse {

    private List<Deputy> deputies;

    public List getDeputies() {
        if (deputies == null)
            deputies = new ArrayList();
        return deputies;
    }

    public void setDeputies(List deputies) {
        this.deputies = deputies;
    }
}
