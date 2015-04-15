package com.zeus.android.mydeputy.app.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zeus.android.mydeputy.app.R;
import com.zeus.android.mydeputy.app.api.request.BaseRequest;
import com.zeus.android.mydeputy.app.api.response.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2/10/15.
 */
public class RequestManager {

  private final String SERVER_INFO = "http://my-deputy.test.zeuselectronics.biz/web/";

    public static final String URI_BASE = "http://my-deputy.test.zeuselectronics.biz/web/";


    public final static int STATUS_OK = 200;
    public final static int STATUS_PARAMETERS_NOT_SET = 400;
    public final static int STATUS_NOT_FOUND  = 404;
    public final static int STATUS_NOT_ALLOWED  = 413;
    public final static int STATUS_ERROR  = 500;
    public final static int STATUS_FAILED  = 501;

    /*
     * URIs
     */
    public final static String LOGIN_URL = URI_BASE + "user/login";
    public final static String REGISTER_URL = URI_BASE + "user/register";
    public final static String DEPUTIES_LIST_URL = URI_BASE + "user/list-deputies";
    public final static String CITIZEN_INFO_URL = URI_BASE + "user/citizen-info";
    public final static String DEPUTY_INFO_URL = URI_BASE + "user/deputy-info";
    public final static String APPEAL_LIST_URL = URI_BASE + "appeal/list";
    public final static String APPEAL_CREATE_URL = URI_BASE + "appeal/create";
    public final static String QUIZ_LIST_URL = URI_BASE + "poll/list";
    public final static String APPEAL_EDIT_URL = URI_BASE + "appeal/edit";
    public final static String QUIZ_SINGLE_URL = URI_BASE + "/poll/get";
    public final static String QUIZ_VOTE_URL = URI_BASE + "poll/vote";
    public final static String QUIZ_CREATE_URL = URI_BASE + "poll/create";
    public final static String NEWS_LIST_URL = URI_BASE + "news/list";
    public final static String NEWS_CREATE_URL = URI_BASE + "news/create";
    public final static String NEWS_SINGLE_URL = URI_BASE + "news/get";
    public final static String NEWS_ADD_COMMENT_URL = URI_BASE + "news/comment";
    public final static String USER_INFO_EDIT_URL = URI_BASE + "user/edit";
    public final static String USER_INFO_URL = URI_BASE + "news/comment";


    public final static int REQUEST_LOGIN = 1;
    public final static int REQUEST_REGISTER = 2;
    public final static int REQUEST_DEPUTY_LIST = 3;
    public final static int REQUEST_DEPUTY_INFO = 4;
    public final static int REQUEST_APPEAL_LIST = 5;
    public final static int REQUEST_APPEAL_SINGLE = 6;
    public final static int REQUEST_APPEAL_CREATE = 7;
    public final static int REQUEST_APPEAL_EDIT = 8;
    public final static int REQUEST_QUIZ_LIST = 9;
    public final static int REQUEST_QUIZ_SINGLE = 10;
    public final static int REQUEST_QUIZ_CREATE = 11;
    public final static int REQUEST_QUIZ_VOTE = 12;
    public final static int REQUEST_NEWS_LIST = 13;
    public final static int REQUEST_NEWS_SINGLE = 14;
    public final static int REQUEST_NEWS_CREATE = 15;
    public final static int REQUEST_NEWS_ADD_COMMENT = 16;
    public final static int REQUEST_CITIZEN_INFO = 17;
    public final static int REQUEST_DEPUTY_INFO_EDIT = 17;


    private RequestQueue queue;
    private Context context;

    private String activityTag;
    private List<OnResponseListener> listeners;

    public interface OnResponseListener{

        public void onRequestSuccess(JSONObject response, int type);

        public void onRequestFail(String message, int type);
    }

    public RequestManager(Context mContext) {

        queue = Volley.newRequestQueue(mContext);
        this.context = mContext;
    }

    public void addListener(OnResponseListener listener){
        if (listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    public void removeListener(OnResponseListener listener){
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListenersOnSuccess(JSONObject jsonObject, int requestType){
        if (listeners != null){
            for (OnResponseListener listener: listeners){
                listener.onRequestSuccess(jsonObject, requestType);
            }
        }
    }

    private void notifyListenersOnFail(String message,int requestType){
        if (listeners != null){
            for (OnResponseListener listener: listeners){
                listener.onRequestFail(message, requestType);
            }
        }
    }

    public void sendRequest(BaseRequest obj, String uri, String tag, final int type) {

        this.activityTag = tag;
        final int requestType = type;
        String gson = new Gson().toJson(obj);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest( Request.Method.POST, uri, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BaseResponse baseResponse = new Gson().fromJson(response.toString(), BaseResponse.class);

                        if (baseResponse.isSuccess()) {
                            notifyListenersOnSuccess(response, type);

                        }else{
                            switch (baseResponse.getStatus()){
                                case STATUS_FAILED:
                                    notifyListenersOnFail("Authorization failed", requestType);
                                    break;
                                case STATUS_PARAMETERS_NOT_SET:
                                    notifyListenersOnFail("Required input parameter not set", requestType);
                                    break;
                                case STATUS_NOT_ALLOWED:
                                    notifyListenersOnFail("Action not allowed for this user", requestType);
                                    break;
                                case STATUS_NOT_FOUND:
                                    notifyListenersOnFail("Record not found", requestType);
                                    break;
                                case STATUS_ERROR:
                                    notifyListenersOnFail("Unknown error", requestType);
                                    break;
                            }
                        }
                    }},

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notifyListenersOnFail(context.getResources().getString(R.string.load_error), requestType);
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                return headers;
            }

        };
        request.setTag(tag);
        queue.add(request);
    }

    public void cancelAllRequest(String tag){
        queue.cancelAll(tag);
    }

}

