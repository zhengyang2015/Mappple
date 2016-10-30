package com.myapp.zhengyang.Mappple.view.base;

import android.os.AsyncTask;

import com.myapp.zhengyang.Mappple.Dribbble.DribbbleException;

import java.io.IOException;

public abstract class DribbbleTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private DribbbleException exception;

    protected abstract Result doJob(Params... params) throws DribbbleException, IOException;

    protected abstract void onSuccess(Result result);

    protected abstract void onFailed(DribbbleException e);

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doJob(params);
        } catch (DribbbleException e) {
            e.printStackTrace();
            exception = e;
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (exception != null) {
            onFailed(exception);
        } else {
            onSuccess(result);
        }
    }
}
