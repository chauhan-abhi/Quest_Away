package com.abhi.questaway.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

public abstract class BaseFragment<VH extends BaseViewHolder> extends Fragment {

    abstract protected VH setupViewHolder(View view);

    abstract protected void setUpComponent();

    protected VH viewHolder;
    protected Context context;

    protected View setupUI(LayoutInflater inflater, ViewGroup container, int layoutId,
                           Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(layoutId, container, false);
        ButterKnife.bind(this, fragmentView);
        setUpComponent();
        Bundle b = getArguments();
        viewHolder = setupViewHolder(fragmentView);
        viewHolder.onCreateView(savedInstanceState, b);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewHolder.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewHolder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewHolder.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewHolder.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewHolder.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public VH getViewHolder() {
        return viewHolder;
    }

}
