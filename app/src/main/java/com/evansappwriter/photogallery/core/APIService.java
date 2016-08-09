package com.evansappwriter.photogallery.core;

import android.content.res.Resources;
import android.os.Bundle;

import com.evansappwriter.photogallery.PhotoGalleryApplication;
import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.util.Keys;
import com.evansappwriter.photogallery.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by markevans on 8/3/16.
 */
public class APIService {
    private static final String TAG = "APISERVICE";

    private static final String HTTP_AUTH_ERROR = "authorization_failure";
    private static final String HTTP_UNKNOWNHOST_EXCEPTION = "UnknownHostException";
    private static final String HTTP_TIMEOUT_EXCEPTION = "ConnectTimeoutException";

    private static APIService mInstance = null;

    private static final int TIMEOUT_READ = 60000; // ms
    private static final int TIMEOUT_CONNECT = 15000; // ms

    private static final String API_KEY = "f07f7bc1501b4354836b5b6bb773c660";

    @SuppressWarnings("ConstantConditions")
    private static final String REST_API = "https://api.flickr.com";

    public static final String ENDPOINT_PHOTOS_RECENT = "/services/rest/?method=flickr.photos.getrecent&format=json&nojsoncallback=1&extras=url_t,url_c,url_l,url_o,date_taken";

    private static Resources mRes;

    public interface OnUIResponseHandler {
        void onSuccess(String payload);
        void onFailure(String errorTitle, String errorText, int dialogId);
    }

    // private constructor prevents instantiation from other classes
    private APIService() {

    }

    /**
     * Creates a new instance of TelmateService.
     */
    public static APIService getInstance() {

        if (mInstance == null) {
            mInstance = new APIService();
        }

        mRes = PhotoGalleryApplication.getContext().getResources();

        return mInstance;
    }

    /**
     * *******************************************************************************************************
     */

    public void get(final String endpoints, Bundle params, final OnUIResponseHandler handler) {
        Bundle urlParams = getAuthBundle();
        if (params != null) {
            urlParams.putAll(params);
        }

        String uri = REST_API + endpoints;
        uri += "&" + encodeUrl(urlParams);

        Utils.printLogInfo(TAG, "API URL: " + uri);
        AsyncHttpClient aClient = new AsyncHttpClient();
        aClient.setTimeout(TIMEOUT_READ);
        aClient.get(uri, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Utils.printLogInfo(TAG, "- Successful !: " + statusCode);

                processSuccessRepsonse(handler, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Utils.printLogInfo(TAG, "- Failed !: " + statusCode);

                processFailureRepsonse(handler, responseBody != null ? new String(responseBody) : null, e.toString());
            }
        });
    }

    public void post(String endpoints, Bundle params, final OnUIResponseHandler handler) {
        Bundle urlParams = getAuthBundle();

        String uri = REST_API + endpoints;
        uri += "?" + encodeUrl(urlParams);

        RequestParams requestparams = new RequestParams();
        for (String key : params.keySet()) {
            requestparams.put(key, params.get(key).toString());
        }

        Utils.printLogInfo(TAG, "API URL: " + uri);
        AsyncHttpClient aClient = new AsyncHttpClient();
        aClient.setTimeout(TIMEOUT_READ);
        aClient.post(uri, requestparams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Utils.printLogInfo(TAG, "- Successful !: " + statusCode);

                processSuccessRepsonse(handler, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Utils.printLogInfo(TAG, "- Failed !: " + statusCode);

                processFailureRepsonse(handler, new String(responseBody), e.toString());
            }
        });
    }

    public void post(String endpoints, String entity, String contentType, final OnUIResponseHandler handler) {
        Bundle urlParams = getAuthBundle();

        String uri = REST_API + endpoints;
        uri += "?" + encodeUrl(urlParams);

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Utils.printLogInfo(TAG, "API URL: " + uri);
        AsyncHttpClient aClient = new AsyncHttpClient();
        aClient.setTimeout(TIMEOUT_READ);
        aClient.post(PhotoGalleryApplication.getContext(), uri, stringEntity, contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Utils.printLogInfo(TAG, "- Successful !: " + statusCode);

                processSuccessRepsonse(handler, new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Utils.printLogInfo(TAG, "- Failed !: " + statusCode);

                processFailureRepsonse(handler, new String(responseBody), e.toString());
            }
        });
    }

    private Bundle getAuthBundle() {
        Bundle params = new Bundle();

        params.putString(PARAM_API_KEY, API_KEY);

        return params;
    }

    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(200);
        boolean first = true;
        Set<String> keySet = parameters.keySet();

        for (String key : keySet) {
            Object parameter = parameters.get(key);

            if (!(parameter instanceof String)) {
                continue;
            }

            if (first) {
                first = false;
            } else {
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(key, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Utils.printStackTrace(e);
            }
            sb.append('=');
            try {
                sb.append(URLEncoder.encode(parameters.getString(key), HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Utils.printStackTrace(e);
            }
        }
        return sb.toString();
    }

    private void processSuccessRepsonse(OnUIResponseHandler handler, String payload) {
        handler.onSuccess(payload);
    }

    private void processFailureRepsonse(OnUIResponseHandler handler, String payload, String exception) {
        String errorTitle = "";
        String errorText = "";
        int dialogId = Keys.DIALOG_GENERAL_ERROR;
        if (payload != null) {
            BundledData data = new BundledData(APIParser.TYPE_PARSER_ERROR);
            data.setHttpData(payload);
            APIParser.parseResponse(data);

            errorTitle = (String) data.getAuxData()[0];
            errorText = (String) data.getAuxData()[1];
            Utils.printLogInfo(TAG, "API error: ", errorText);
        } else {
            if (exception.contains(HTTP_UNKNOWNHOST_EXCEPTION)) {
                errorTitle = mRes.getString(R.string.error_no_connection_title);
                errorText = mRes.getString(R.string.error_no_connection);
            } else if (exception.contains(HTTP_TIMEOUT_EXCEPTION)){
                errorTitle = mRes.getString(R.string.error_title);
                errorText = mRes.getString(R.string.error_text) + ": " + mRes.getString(R.string.error_timeout);
            } else {
                errorTitle = mRes.getString(R.string.error_title);
                errorText = mRes.getString(R.string.photo_get_error);
            }
            dialogId = Keys.DIALOG_GENERAL_ERROR;
            Utils.printLogInfo(TAG, "API error: ", exception.toString());
        }

        handler.onFailure(errorTitle, errorText, dialogId);
    }

    // PARAMS >>>>>>>>>

    public static final String PARAM_API_KEY = "api_key";
    public static final String PARAM_PAGE = "page";
}
