package com.myapp.zhengyang.Mappple.view.bucketList;

import android.support.v4.app.Fragment;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.view.base.SingleFragmentActivity;

import java.util.ArrayList;

public class BucketListActivity extends SingleFragmentActivity{
    @Override
    protected Fragment newFragment() {
        boolean isChoosingMode = getIntent().getExtras().getBoolean(
                BucketListFragment.KEY_CHOOSING_MODE);
        ArrayList<String> chosenBucketIds = getIntent().getStringArrayListExtra(
                BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
        return BucketListFragment.newInstance(null, isChoosingMode, chosenBucketIds);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
