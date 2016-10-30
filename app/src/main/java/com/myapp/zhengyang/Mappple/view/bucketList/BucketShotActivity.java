package com.myapp.zhengyang.Mappple.view.bucketList;

import android.support.v4.app.Fragment;

import com.myapp.zhengyang.Mappple.view.base.SingleFragmentActivity;
import com.myapp.zhengyang.Mappple.view.shotList.ShotListFragment;

public class BucketShotActivity extends SingleFragmentActivity{
    public static final String KEY_BUCKET_NAME = "bucketName";

    @Override
    protected Fragment newFragment() {
        //用shotlistfragment来显示该bucket里的shots
        String bucketId = getIntent().getStringExtra(ShotListFragment.KEY_BUCKET_ID);
        return bucketId == null
                ? ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR)
                : ShotListFragment.newBucketListInstance(bucketId);
    }

    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }
}
