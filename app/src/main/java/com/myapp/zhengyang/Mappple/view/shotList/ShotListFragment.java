package com.myapp.zhengyang.Mappple.view.shotList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.Dribbble.Dribbble;
import com.myapp.zhengyang.Mappple.Dribbble.DribbbleException;
import com.myapp.zhengyang.Mappple.model.Shot;
import com.myapp.zhengyang.Mappple.utils.ModelUtils;
import com.myapp.zhengyang.Mappple.view.base.DribbbleTask;
import com.myapp.zhengyang.Mappple.view.base.InfiniteAdapter;
import com.myapp.zhengyang.Mappple.view.base.spaseItemDecoration;
import com.myapp.zhengyang.Mappple.view.shotDetail.ShotFragment;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListFragment extends Fragment{
    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKE = 2;
    public static final int LIST_TYPE_BUCKET = 3;

    private static final String KEY_LIST_TYPE = "list_type";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final int REQ_CODE_SHOT = 100;

    @BindView(R.id.recycler_view) RecyclerView recyclerview;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ShotListAdapter adapter;

    private int listType;

    public static Fragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //对bucketlist做点击事件时的构造函数，显示该bucket中的shots
    public static ShotListFragment newBucketListInstance(@NonNull String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                    new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedin()) {
                //第一次加载时调用，因此refresh为false
                AsyncTaskCompat.executeParallel(new LoadShotsTask(false));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);

        //在第一次加载时不允许刷新，以防止两个refresh重叠
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //一定时刷新时调用，因此refresh为true
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.addItemDecoration(new spaseItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));


        adapter = new ShotListAdapter(this, new ArrayList<Shot>(), onLoadMore);
        recyclerview.setAdapter(adapter);
    }

    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Shot>> {

        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            //如果是refresh则读取第一页，否则就是loading
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    return Dribbble.getShots(page);
                case LIST_TYPE_LIKE:
                    return Dribbble.getLikedShots(page);
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return Dribbble.getBucketShots(bucketId, page);
                default:
                    return Dribbble.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
