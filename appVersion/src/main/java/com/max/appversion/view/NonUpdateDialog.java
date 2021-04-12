package com.max.appversion.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

/*
 *create by lmx  2021/4/12:4:12 PM
 */
public class NonUpdateDialog extends Dialog {

    public NonUpdateDialog(@NonNull Context context) {
        super(context);
    }

    public void cancel(){
        dismiss();
    }
}
