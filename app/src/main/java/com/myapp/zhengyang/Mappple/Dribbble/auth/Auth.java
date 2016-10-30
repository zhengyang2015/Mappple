package com.myapp.zhengyang.Mappple.Dribbble.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Auth {
    public static final int REQ_CODE = 100;

    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_SCOPE= "scope";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String SCOPE = "public+write";

    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";

    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    private static final String CLIENT_ID = "fcbdd8b961b1ad943e8ec375054941954dcf456562c8d0e0cce7838bc6579c15";

    private static final String CLIENT_SECRET = "7e99599c0591651a73d62e7b4d579ada1e21f8b8fe220b75a928c3a605f61be6";

    public static final String REDIRECT_URI = "http://example.com/path";

    public static void openAuthActivity(Activity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL, getAuthorizeUrl());
        activity.startActivityForResult(intent, REQ_CODE);
    }

    private static String getAuthorizeUrl() {
        String url = Uri.parse(URI_AUTHORIZE).buildUpon().
                appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID).build().toString();

        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        return url;
    }


    public static String fetchAccessToken(String authCode) throws IOException {
        OkHttpClient okhttpclient = new OkHttpClient();
        RequestBody requestbody = new FormBody.Builder().add(KEY_CLIENT_ID, CLIENT_ID).add(KEY_CLIENT_SECRET, CLIENT_SECRET).
                add(KEY_CODE, authCode).add(KEY_REDIRECT_URI, REDIRECT_URI).build();

        Request request = new Request.Builder().url(URI_TOKEN).post(requestbody).build();

        Response response = okhttpclient.newCall(request).execute();

        String responseString = response.body().string();

        try {
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getString(KEY_ACCESS_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
