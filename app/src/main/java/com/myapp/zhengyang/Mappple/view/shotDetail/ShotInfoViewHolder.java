package com.myapp.zhengyang.Mappple.view.shotDetail;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

public class ShotInfoViewHolder extends BaseViewHolder{
    @BindView(R.id.shot_title) TextView title;
    @BindView(R.id.shot_description) TextView description;
    @BindView(R.id.shot_author_picture) SimpleDraweeView authorPicture;
    @BindView(R.id.shot_author_name) TextView authorName;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_action_like) ImageButton likeButton;
    @BindView(R.id.shot_action_bucket) ImageButton bucketButton;
    @BindView(R.id.shot_action_share) TextView shareButton;

    public ShotInfoViewHolder(View itemView) {
        super(itemView);
    }
}
