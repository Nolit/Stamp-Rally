package com.om1.stamp_rally.model;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import database.entities.Sample;

/**
 * Created by b3176 on 2016/11/15.
 */

public class SampleModel {

    private static SampleModel instance = new SampleModel();
    private AsyncTask<Void, Void, Sample> task = new MyTask();
    private Sample entity;

    private SampleModel(){}
    public static SampleModel getInstance() {
        return instance;
    }

    private class MyTask extends AsyncTask<Void, Void, Sample> {

        @Override
        protected Sample doInBackground(Void... params) {
            // TODO ここで通信処理を行う
            return new Sample();
        }

        @Override
        protected void onPostExecute(Sample entity) {
            super.onPostExecute(entity);
            setEntity(entity);
            EventBus.getDefault().post(new ButtonClickEvent(mClickCount++));
        }
    }

    private void setEntity(Sample sample){
        entity = sample;
    }
}
