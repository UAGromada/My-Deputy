package com.zeus.android.mydeputy.app.api.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/16/15.
 */
public class QuizCreateRequest extends BaseRequest {

    private String text;
    private String title;
    private long enddate;
    private List<String> variants;

    public void setEnddate(long enddate) {
        this.enddate = enddate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addVariant(String variant){
        if (variants == null){
            variants = new ArrayList<>();
        }
        variants.add(variant);
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
}
