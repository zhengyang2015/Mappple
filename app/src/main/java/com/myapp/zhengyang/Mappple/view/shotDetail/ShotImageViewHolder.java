package com.myapp.zhengyang.Mappple.view.shotDetail;

import android.view.View;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

public class ShotImageViewHolder extends BaseViewHolder{
    @BindView(R.id.shot_detail_image) SimpleDraweeView imageView;

    public ShotImageViewHolder(View itemView) {
        super(itemView);
    }
}
