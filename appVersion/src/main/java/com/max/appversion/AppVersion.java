package com.max.appversion;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.max.appversion.listenner.ActivityOnResultListenner;
import com.max.appversion.utils.InstallUtil;
import com.max.appversion.update.ToolClass;
import com.max.appversion.update.UpdateTools;
import com.max.appversion.view.UpdateDialog;

public class AppVersion implements ActivityOnResultListenner {


    private Activity mContext;
    static final AppVersion instance = new AppVersion();


//    更新模版
    private UpdateTools updateTools;
    /*
    * 更新对话框
    * */
    private UpdateDialog updateDialog;
    /*
    * 安装工具
    * */
    InstallUtil installUtil;

    public AppVersion(){

    }

    public static void init(Activity activity){
        getInstance().mContext = activity;
        getInstance().initTools();
    }

    public static AppVersion getInstance() {
        return instance;
    }

    void checkStorePermisstion(){

    }

    void initTools(){

        updateTools = new UpdateTools(mContext);
        updateDialog = new UpdateDialog(mContext);
        updateDialog.setCancelable(false);
        installUtil = new InstallUtil(mContext,UpdateTools.getDiskCachePath(mContext) + "/" + "app.apk");

        updateTools.checkVersion(new UpdateTools.UpdateToolsCallBack() {
            @Override
            public void needUpdate(String msg, final boolean must, String serverVersion) {
                String localVersion = updateTools.getLocolVersion();
                if (localVersion != null){
                    int code = updateTools.versionCompare(serverVersion,localVersion);
                    if (code  == 0){
                        installUtil.install();
                        return;
                    }
                }

                final String  message = msg;
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        PackageManager manager = mContext.getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(mContext.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String version = info.versionName;
                        String newMsg = message.replace("#必须更新#","");
                        newMsg = "版本更新：当前版本" + version + "\n" + newMsg;

                        updateDialog.setMustUpdate(must);
                        updateDialog.setMsg(newMsg);
                        updateDialog.show();

                        updateDialog.setDialogClicklistener(new UpdateDialog.ChooseDialogClicklistener() {
                            @Override
                            public void onFirstButtonClick() {
                                updateDialog.dismiss();
                            }

                            @Override
                            public void onSecButtonClick() {
                                updateDialog.setInUpdating(true);
                                updateTools.getDowLoadToken();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onNoUpdate() {

            }

            @Override
            public void onDownLoadProgress(int progress) {
                updateDialog.setProgress(progress);
            }

            @Override
            public void onDownLoaded(String path) {
                updateDialog.dismiss();
                ToolClass.mainTread(new ToolClass.ToolMainThreadCall() {
                    @Override
                    public void call() {
                        installUtil.install();
                    }
                });
            }
        });
    }


    public void checkCurrentVesion(){
        updateTools.checkVersion(new UpdateTools.UpdateToolsCallBack() {
            @Override
            public void needUpdate(String msg, final boolean must, String serverVersion) {
                final String  message = msg;
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        PackageManager manager = mContext.getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(mContext.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String version = info.versionName;
                        String newMsg = message.replace("#必须更新#","");
                        newMsg = "版本更新：当前版本" + version + "\n" + newMsg;

                        updateDialog.setMustUpdate(must);
                        updateDialog.setMsg(newMsg);
                        updateDialog.show();

                        updateDialog.setDialogClicklistener(new UpdateDialog.ChooseDialogClicklistener() {
                            @Override
                            public void onFirstButtonClick() {
                                updateDialog.dismiss();
                            }

                            @Override
                            public void onSecButtonClick() {
                                updateDialog.setInUpdating(true);
                                updateTools.getDowLoadToken();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onNoUpdate() {

            }

            @Override
            public void onDownLoadProgress(int progress) {
                updateDialog.setProgress(progress);
            }

            @Override
            public void onDownLoaded(String path) {
                updateDialog.dismiss();
                ToolClass.mainTread(new ToolClass.ToolMainThreadCall() {
                    @Override
                    public void call() {
                        installUtil.install();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == InstallUtil.UNKNOWN_CODE){
            installUtil.install();
        }
    }
}
