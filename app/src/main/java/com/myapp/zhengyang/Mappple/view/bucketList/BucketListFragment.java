package com.myapp.zhengyang.Mappple.view.bucketList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.zhengyang.Mappple.R;
import com.myapp.zhengyang.Mappple.Dribbble.Dribbble;
import com.myapp.zhengyang.Mappple.Dribbble.DribbbleException;
import com.myapp.zhengyang.Mappple.model.Bucket;
import com.myapp.zhengyang.Mappple.view.base.DribbbleTask;
import com.myapp.zhengyang.Mappple.view.base.InfiniteAdapter;
import com.myapp.zhengyang.Mappple.view.base.spaseItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BucketListFragment extends Fragment{
    public static final int REQ_CODE_NEW_BUCKET = 100;

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_CHOOSING_MODE = "choose_mode";
    //save之后的bucketid
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen_bucket_ids";
    //save之前的bucketid
    public static final String KEY_COLLECTED_BUCKET_IDS = "collected_bucket_ids";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab) FloatingActionButton fab;

    private BucketListAdapter adapter;

    private String userId;
    private boolean isChoosingMode;
    private Set<String> collectedBucketIdSet;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadBucketsTask(false));
        }
    };

    public static BucketListFragment newInstance(@Nullable String userId,
                                                 boolean isChoosingMode,
                                                 @Nullable ArrayList<String> chosenBucketIds) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_COLLECTED_BUCKET_IDS, chosenBucketIds);

        BucketListFragment fragment = new BucketListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //建立菜单栏
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //如果是选择模式就有save菜单
        if(isChoosingMode){
            inflater.inflate(R.menu.bucket_list_choose_mode_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //save之后的返回
        if(item.getItemId() == R.id.save){
            //save之后的bucketid,看data里面哪些buckets被chose
            ArrayList<String> chosenBucketIds = new ArrayList<>();
            for (Bucket bucket : adapter.getData()) {
                if (bucket.isChoosing) {
                    chosenBucketIds.add(bucket.id);
                }
            }

            //返回到调用者shotadapter
            Intent result = new Intent();
            result.putExtra(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);
            getActivity().setResult(Activity.RESULT_OK, result);
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_fab, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Bundle args = getArguments();
        isChoosingMode = args.getBoolean(KEY_CHOOSING_MODE);

        if (isChoosingMode) {
            List<String> chosenBucketIdList = args.getStringArrayList(KEY_COLLECTED_BUCKET_IDS);
            if (chosenBucketIdList != null) {
                collectedBucketIdSet = new HashSet<>(chosenBucketIdList);
            }
        } else {
            collectedBucketIdSet = new HashSet<>();
        }

        //initial UI
        //开始的是时候加载bucket，此时不能刷新，否则会重叠
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadBucketsTask(true));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new spaseItemDecoration(getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new BucketListAdapter(getContext(), new ArrayList<Bucket>(), onLoadMore, isChoosingMode);

        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //新建bucket之后的返回
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (!TextUtils.isEmpty(bucketName)) {
                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
            }
        }
    }

    private class LoadBucketsTask extends DribbbleTask<Void, Void, List<Bucket>> {

        boolean refresh;

        public LoadBucketsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException, IOException {
            final int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return Dribbble.getUserBuckets(page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            //当得到的数据数量少于每页最大显示数量时，证明取到最后一页，不用再显示loading
            adapter.setShowLoading(buckets.size() >= Dribbble.COUNT_PER_LOAD);

            for (Bucket bucket : buckets) {
                if (collectedBucketIdSet.contains(bucket.id)) {
                    bucket.isChoosing = true;
                }
            }

            if (refresh) {
                adapter.setData(buckets);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                adapter.append(buckets);
            }
            //在加载完数据之前不能刷新
            swipeRefreshLayout.setEnabled(true);
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public class NewBucketTask extends DribbbleTask<Void, Void, Bucket>{
        private String bucketName;
        private String bucketDescription;

        public NewBucketTask(String bucketName, String bucketDescription) {
            this.bucketName = bucketName;
            this.bucketDescription = bucketDescription;
        }

        @Override
        protected Bucket doJob(Void... params) throws DribbbleException, IOException {
            return Dribbble.newBucket(bucketName, bucketDescription);
        }

        @Override
        protected void onSuccess(Bucket bucket) {
//            bucket.isChoosing = true;
            adapter.prepend(Collections.singletonList(bucket));
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
