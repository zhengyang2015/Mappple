package com.myapp.zhengyang.Mappple.view.shotDetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.model.Shot;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import butterknife.ButterKnife;

public class ShotAdapter extends RecyclerView.Adapter{
    //recyclerview的position0放image，position1放info
    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_INFO = 1;

    private final Shot shot;

    private final ShotFragment shotFragment;

    public ShotAdapter(ShotFragment shotFragment, Shot shot) {
        this.shotFragment = shotFragment;
        this.shot = shot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(getContext()).inflate(R.layout.shot_item_image, parent, false);
                ButterKnife.bind(this,view);
                return new ShotImageViewHolder(view);
            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(getContext()).inflate(R.layout.shot_item_info, parent, false);
                ButterKnife.bind(this, view);
                return new ShotInfoViewHolder(view);
            default:
                return null;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewtype = getItemViewType(position);
        switch (viewtype){
            case VIEW_TYPE_SHOT_IMAGE:
                // play gif automatically
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(shot.getImageUrl()))
                        .setAutoPlayAnimations(true)
                        .build();
                ((ShotImageViewHolder) holder).imageView.setController(controller);
                break;
            case VIEW_TYPE_SHOT_INFO:
                ShotInfoViewHolder infoViewHolder = (ShotInfoViewHolder) holder;

                infoViewHolder.title.setText(shot.title);
                infoViewHolder.authorName.setText(shot.user.name);
                infoViewHolder.description.setText(Html.fromHtml(
                        shot.description == null ? "" : shot.description));
                //?
                infoViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());
                infoViewHolder.authorPicture.setImageURI(Uri.parse(shot.user.avatar_url));

                infoViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
                infoViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
                infoViewHolder.viewCount.setText(String.valueOf(shot.views_count));

                infoViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点了like以后和之前like值相反
                        shotFragment.like(shot.id, !shot.liked);
                    }
                });
                infoViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.share();
                    }
                });
                infoViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.bucket();
                    }
                });

                Drawable likeDrawable = shot.liked
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_red_18dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_18dp);
                infoViewHolder.likeButton.setImageDrawable(likeDrawable);

                Drawable bucketDrawable = shot.bucketed
                        ? ContextCompat.getDrawable(getContext(), R.drawable.bucket_red)
                        : ContextCompat.getDrawable(getContext(), R.drawable.bucket_gray);
                infoViewHolder.bucketButton.setImageDrawable(bucketDrawable);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_SHOT_IMAGE;
        }else{
            return VIEW_TYPE_SHOT_INFO;
        }
    }

    @NonNull
    private Context getContext() {
        return shotFragment.getContext();
    }
}
