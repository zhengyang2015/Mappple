package com.myapp.zhengyang.Mappple.view.bucketList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;

import butterknife.BindView;

public class BucketListViewHolder extends BaseViewHolder{
    @BindView(R.id.bucket_layout) View bucketLayout;
    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(R.id.bucket_shot_chosen) ImageView bucketChosen;

    public BucketListViewHolder(View itemView) {
        super(itemView);
    }
}
