package com.myapp.zhengyang.Mappple.model;

import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Map;

public class Shot {
    //image存成两种形式：normal和hidpi，如果是animated动图，则只有normal一种，否则返回didpi或者normal（如果hidpi不存在）
    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_HIDPI = "hidpi";

    public String id;
    public String title;
    public String description;
    public String html_url;

    public int width;
    public int height;
    //imgae被存为type,uri的形式
    public Map<String, String> images;
    public boolean animated;

    public int views_count;
    public int likes_count;
    public int buckets_count;

    public Date created_at;

    public User user;

    public boolean liked;
    public boolean bucketed;

    @Nullable
    public String getImageUrl() {
        if (images == null) {
            return "";
        }

        String url = images.containsKey(IMAGE_HIDPI)
                ? images.get(IMAGE_HIDPI)
                : images.get(IMAGE_NORMAL);
        return url == null ? "" : url;
    }
}
