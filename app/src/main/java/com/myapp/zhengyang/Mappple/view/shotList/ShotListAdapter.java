package com.myapp.zhengyang.Mappple.view.shotList;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.model.Shot;
import com.myapp.zhengyang.Mappple.utils.ModelUtils;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;
import com.myapp.zhengyang.Mappple.view.base.InfiniteAdapter;
import com.myapp.zhengyang.Mappple.view.shotDetail.ShotActivity;
import com.myapp.zhengyang.Mappple.view.shotDetail.ShotFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ShotListAdapter extends InfiniteAdapter<Shot>{
    private final ShotListFragment shotListFragment;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data,
                           @NonNull LoadMoreListener loadMoreListener) {
        super(shotListFragment.getContext(), data, loadMoreListener);
        this.shotListFragment = shotListFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_shot, parent, false);
        return new ShotListViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        final Shot shot = getData().get(position);

        ShotListViewHolder shotViewHolder = (ShotListViewHolder) holder;
        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));

        // play gif automatically
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(shot.getImageUrl()))
                .setAutoPlayAnimations(true)
                .build();
        shotViewHolder.image.setController(controller);

        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShotActivity.class);
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>() {
                        }));
                intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.title);
                shotListFragment.startActivity(intent);
            }
        });
    }

    @Override
    public void append(@NonNull List<Shot> data) {
        super.append(data);
        if(getData().size() == 0){
            Snackbar.make(shotListFragment.getView(), shotListFragment.getString(R.string.empty_alart), Snackbar.LENGTH_LONG).show();
        }
    }
}