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
    protected PermissionListener permissionListener;

    protected View setupUI(LayoutInflater inflater, ViewGroup container, int layoutId,
                           Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(layoutId, container, false);
        ButterKnife.bind(this, fragmentView);
        setUpComponent();
        Bundle b = getArguments();
        viewHolder = setupViewHolder(fragmentView);
        viewHolder.onCreateView(savedInstanceState,b);
        return fragmentView;
    }

    @Override public void onStart() {
        super.onStart();
        viewHolder.onStart();
    }

    @Override public void onResume() {
        super.onResume();
        viewHolder.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        viewHolder.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        viewHolder.onStop();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        viewHolder.onDestroy();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public VH getViewHolder() {
        return viewHolder;
    }

    protected void requestPermissions(String permission, Integer requestCode, PermissionListener permissionListener) {
        this.permissionListener = permissionListener;
        if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                // called when user has denied permission before
                ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
            }
        } else {
            permissionListener.onGranted(requestCode);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getContext(), permissions[0])
                == PackageManager.PERMISSION_GRANTED && permissionListener != null) {
            switch (requestCode) {
                case 1:
                    //ask for gps
                    if (permissionListener != null) {
                        permissionListener.onGranted(requestCode);
                    } else {
                        permissionListener.onRejected(requestCode);
                    }
                    break;
                case 2:
                    // ask for call
                    if (permissionListener != null) {
                        permissionListener.onGranted(requestCode);
                    } else {
                        permissionListener.onRejected(requestCode);
                    }
                    break;
                case 3:
                    //ask for receive sms
                    if (permissionListener != null) {
                        permissionListener.onGranted(requestCode);
                    } else {
                        permissionListener.onRejected(requestCode);
                    }
                    break;
                case 4:
                    // ask for camera
                    if (permissionListener != null) {
                        permissionListener.onGranted(requestCode);
                    } else {
                        permissionListener.onRejected(requestCode);
                    }
                    break;
                case 5:
                    // ask for write storage
                    if (permissionListener != null) {
                        permissionListener.onGranted(requestCode);
                    } else {
                        permissionListener.onRejected(requestCode);
                    }
                    break;
                default:
                    viewHolder.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
