package com.myapp.zhengyang.Mappple.view.bucketList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.model.Bucket;
import com.myapp.zhengyang.Mappple.view.base.BaseViewHolder;
import com.myapp.zhengyang.Mappple.view.base.InfiniteAdapter;
import com.myapp.zhengyang.Mappple.view.shotList.ShotListFragment;

import java.text.MessageFormat;
import java.util.List;

public class BucketListAdapter extends InfiniteAdapter<Bucket>{
    private boolean isChoosingMode;

    public BucketListAdapter(Context context, List<Bucket> data, LoadMoreListener loadMoreListener, boolean isChoosingMode) {
        super(context, data, loadMoreListener);
        this.isChoosingMode = isChoosingMode;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketListViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);
        BucketListViewHolder bucketViewHolder = (BucketListViewHolder) holder;

        // 0 -> 0 shot
        // 1 -> 1 shot
        // 2 -> 2 shots
        //根据shot数量和规则进行选择显示
        String bucketShotCountString = MessageFormat.format(
                holder.itemView.getContext().getResources().getString(R.string.shot_count),
                bucket.shots_count);

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(bucketShotCountString);

        if(isChoosingMode){
            bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);
            bucketViewHolder.bucketChosen.setImageDrawable(
                    bucket.isChoosing? ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp)
                            : ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bucket.isChoosing = !bucket.isChoosing;
                    notifyItemChanged(position);
                }
            });
        }else{
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), BucketShotActivity.class);
                    intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.id);
                    intent.putExtra(BucketShotActivity.KEY_BUCKET_NAME, bucket.name);
                    getContext().startActivity(intent);
                }
            });
        }
    }
}
