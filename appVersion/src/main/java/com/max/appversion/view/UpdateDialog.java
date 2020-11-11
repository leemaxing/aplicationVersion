package com.max.appversion.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.max.appversion.R;

import java.io.File;


public class UpdateDialog extends Dialog {

    public interface ChooseDialogClicklistener{
       void onFirstButtonClick();
       void onSecButtonClick();
       void onCancel();
    }

    TextView progressTextView;
    ImageView updownImageView;
    ImageView logoImageView;

    private TextView titleTextView;
    private TextView msgTextView;
    private TextView fistBtn;
    private Button secBtn;

    private String firstBtnTitle;
    private String secBtnTitle;

    private String title;
    private String msg;
    private boolean mustUpdate;

    boolean isInUpdating = false;

    long progress;

    private ChooseDialogClicklistener dialogClicklistener;

    Context mContext;
    public UpdateDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog_layout);
        initView();
    }

    void initView(){

        titleTextView = findViewById(R.id.dialog_title);
        msgTextView = findViewById(R.id.dialog_message);
        fistBtn = findViewById(R.id.dialog_first_btn);
        secBtn = findViewById(R.id.dialog_sec_btn);

        if (mustUpdate){
            fistBtn.setVisibility(View.GONE);
        }else {
            fistBtn.setVisibility(View.VISIBLE);
        }

        if (msg != null){
            msgTextView.setText(msg);
        }

        fistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClicklistener != null){
                    dialogClicklistener.onFirstButtonClick();
                }
            }
        });
        secBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogClicklistener != null){
                    dialogClicklistener.onSecButtonClick();
                }
            }
        });



         logoImageView = findViewById(R.id.update_logo);
         progressTextView = findViewById(R.id.update_progress);
         updownImageView = findViewById(R.id.update_updown);

        setUpdowViewAnimation();
    }

    public void setInUpdating(boolean inUpdating) {
        isInUpdating = inUpdating;
        Activity activity = (Activity)mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInUpdating){
                    logoImageView.setVisibility(View.GONE);
                    updownImageView.setVisibility(View.VISIBLE);
                    progressTextView.setVisibility(View.VISIBLE);
                    secBtn.setText("正在下载，请稍候...");
                    secBtn.setEnabled(false);
                    fistBtn.setText("点击后台更新");
                }else {
                    logoImageView.setVisibility(View.VISIBLE);
                    fistBtn.setVisibility(View.VISIBLE);
                    secBtn.setVisibility(View.VISIBLE);
                    secBtn.setText("立即更新");
                    secBtn.setEnabled(true);
                    fistBtn.setText("以后再说");
                }
            }
        });

    }

    public void setProgress(long progress) {
        this.progress = progress;
        Activity activity = (Activity)mContext;
        final  long preg = progress;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressTextView.setText(preg + "%");
            }
        });
    }

    //    设置动画
    void setUpdowViewAnimation(){
        int[] location = new int[2];
        updownImageView.getLocationOnScreen(location);//获取视图位置
        int x = location[0];
        int y = location[1];
        TranslateAnimation tAnim = new TranslateAnimation(x, x, y, y+15);//设置视图上下移动的位置
        tAnim .setDuration(1000);
        tAnim .setRepeatCount(Animation.INFINITE);
        tAnim .setRepeatMode(Animation.REVERSE);
        updownImageView.setAnimation(tAnim);
        tAnim .start();

    }

    public void setMustUpdate(boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
        if (fistBtn != null){
            if (mustUpdate){
                fistBtn.setVisibility(View.GONE);
            }else {
                fistBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setFirstBtnTitle(String firstBtnTitle) {
        this.firstBtnTitle = firstBtnTitle;
        if (fistBtn != null) {
            fistBtn.setText(firstBtnTitle);
        }
    }

    public void setSecBtnTitle(String secBtnTitle) {
        this.secBtnTitle = secBtnTitle;
        if (secBtn != null){
            secBtn.setText(secBtnTitle);
        }
    }

    public void setMsg(String msg) {
        this.msg = msg;
        if (msgTextView != null){
            msgTextView.setText(msg);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogClicklistener !=null){
            dialogClicklistener.onCancel();
        }
    }


    public void setDialogClicklistener(ChooseDialogClicklistener dialogClicklistener) {
        this.dialogClicklistener = dialogClicklistener;
    }
}
