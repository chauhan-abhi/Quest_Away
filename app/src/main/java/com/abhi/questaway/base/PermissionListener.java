package com.abhi.questaway.base;

public interface PermissionListener {

    void onGranted(int requestCode);

    void onRejected(int requestCode);
}