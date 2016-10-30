package com.myapp.zhengyang.Mappple.view.shotList;

import android.view.View;
import android.widget.TextView;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

public class ShotListViewHolder extends BaseViewHolder{
    @BindView(R.id.shot_clickable_cover) public View cover;
    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_image) public SimpleDraweeView image;

    public ShotListViewHolder(View itemView) {
        super(itemView);
    }
}
