package com.abhi.questaway.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.ButterKnife;


public abstract class BaseViewHolder<T> {

    protected T data;
    protected View view;
    protected Context context;

    public BaseViewHolder(View view, @Nullable T data, Context context) {
        this.view = view;
        this.data = data;
        this.context = context;
        ButterKnife.bind(this, view);
        setUpComponent();
        initializeView(data);
        attachListeners(data);
    }

    protected abstract void setUpComponent();
    /**
     * call this method to set reload view
     */
    public void setData(@NonNull T data) {
        this.data = data;
        initializeView(data);
        attachListeners(data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

    }

    protected abstract void attachListeners(T data);

    protected abstract void initializeView(@NonNull T data);

    //public abstract void refreshView();

    public void onCreateView(Bundle savedInstanceState, Bundle args) {

    }

    public void onStop() {

    }

    public void onResume() {
    }

    public void onPause(){

    }

    public void onStart() {
    }

    public void onDestroy() {

    }

    public Context getContext() {
        return context;
    }
}
