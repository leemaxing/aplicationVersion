package com.max.appversion.listenner;

import android.content.Intent;

/*
* 页面返回值的监听
* */
public interface ActivityOnResultListenner {
    abstract public void onActivityResult(int requestCode, int resultCode, Intent data);
}
