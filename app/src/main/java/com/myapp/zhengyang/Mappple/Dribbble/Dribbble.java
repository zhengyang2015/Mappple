package com.myapp.zhengyang.Mappple.Dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.myapp.zhengyang.Mappple.model.Bucket;
import com.myapp.zhengyang.Mappple.model.Like;
import com.myapp.zhengyang.Mappple.model.Shot;
import com.myapp.zhengyang.Mappple.model.User;
import com.myapp.zhengyang.Mappple.utils.ModelUtils;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dribbble {
    private static final String TAG = "Dribbble API";

    private static final String API_URL = "https://api.dribbble.com/v1/";

    private static final String USER_END_POINT = API_URL + "user";
    private static final String USERS_END_POINT = API_URL + "users";
    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";

    private static final String SP_AUTH = "auth";

    public static final String KEY_USER = "user";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";

    public static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    public static final TypeToken<List<Shot>> SHOTS_TYPE = new TypeToken<List<Shot>>(){};
    public static final TypeToken<List<Bucket>> BUCKETS_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};
    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};

    private static final String KEY_SHOT_ID = "shot_id";
    public static final int COUNT_PER_LOAD = 12;

    private static String accessToken;
    private static User user;

    private static OkHttpClient client = new OkHttpClient();

    public static android.webkit.CookieManager cookieManager;
    public static int COUNT_PER_PAGE = 12;

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if(accessToken != null){
            user = loadUser(context);
        }
    }

    private static User loadUser(Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    private static void storeUser(Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    private static String loadAccessToken(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    private static void storeAccessToken(Context context, String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static boolean isLoggedin() {
        return accessToken != null;
    }

    public static void login(Context context, String token) throws DribbbleException {
        accessToken = token;
        storeAccessToken(context, token);
        
        user = getUser();
        storeUser(context, user);
    }

    @SuppressWarnings("deprecation")
    public static void logout(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);

        accessToken = null;
        user = null;
        cookieManager = android.webkit.CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }else{
            cookieManager.removeAllCookie();
        }
    }

    private static User getUser() throws DribbbleException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    private static <T> T parseResponse(Response response, TypeToken<T> typetoken) throws DribbbleException {
        String responseString = null;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, responseString);
        return ModelUtils.toObject(responseString, typetoken);
    }

    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response makeGetRequest(String userEndPoint) throws DribbbleException {
        Request.Builder builder = authRequestBuilder(userEndPoint);
        Request request = builder.build();
        return makeRequest(request);
    }

    private static Response makePostRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url).put(requestBody).build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url, RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url).delete(requestBody).build();
        return makeRequest(request);
    }

    private static Response makeRequest(Request request) throws DribbbleException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, response.header("X-RateLimit-Remaining"));
        return response;
    }

    public static User getCurrentUser() {
        return user;
    }

    public static List<Shot> getLikedShots(int page) throws DribbbleException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }

    private static List<Like> getLikes(int page) throws DribbbleException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }

    public static List<Shot> getBucketShots(String bucketId, int page) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots?page=" + page;
        return parseResponse(makeGetRequest(url), SHOTS_TYPE);
    }

    public static List<Shot> getShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOTS_TYPE);
    }

    //get the user's all the buckets
    public static List<Bucket> getUserBuckets() throws DribbbleException, JsonSyntaxException {
        String url = USER_END_POINT + "/" + "buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKETS_TYPE);
    }

    public static List<Bucket> getUserBuckets(@NonNull String userId,
                                               int page) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKETS_TYPE);
    }

    public static List<Bucket> getUserBuckets(int page) throws DribbbleException {
        String url = USER_END_POINT + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKETS_TYPE);
    }

    //get all the buckets of a specifical shot
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws DribbbleException, JsonSyntaxException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKETS_TYPE);
    }

    public static Bucket newBucket(String bucketName, String bucketDescription) throws DribbbleException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, bucketName)
                .add(KEY_DESCRIPTION, bucketDescription)
                .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
    }

    //add a shot to a bucket
    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws DribbbleException, JsonSyntaxException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makePutRequest(url, formBody);
        try {
            checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //remove a shot from a bucket
    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws DribbbleException, JsonSyntaxException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
        try {
            checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws IOException {
        if (response.code() != statusCode) {
            throw new IOException(response.message());
        }
    }

    public static Boolean isLikingShot(String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }

    public static void likeShot(String id) throws DribbbleException, IOException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makePostRequest(url, new FormBody.Builder().build());

        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);
    }

    public static void unlikeShot(String id) throws DribbbleException, IOException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeDeleteRequest(url, new FormBody.Builder().build());

        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }
}
