package com.myapp.zhengyang.Mappple.view.shotDetail;

import android.support.v4.app.Fragment;

import com.myapp.zhengyang.Mappple.view.base.SingleFragmentActivity;

public class ShotActivity extends SingleFragmentActivity{

    public static final String KEY_SHOT_TITLE = "shot_title";

    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
