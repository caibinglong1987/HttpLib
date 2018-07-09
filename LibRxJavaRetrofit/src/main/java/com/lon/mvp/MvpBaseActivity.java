package com.lon.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lon.utils.ActivityManager;

/*
 * 项目名:    BaseLib

 * 文件名:    MvpBaseActivity
 * 创建者:    long
 * 创建时间:  2017/9/7 on 14:17
 * 描述:     TODO 基类Activity
 */
public abstract class MvpBaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected P mvpPresenter;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ActivityManager.getAppInstance().addActivity(this);//将当前activity添加进入管理栈
        mvpPresenter = createPresenter();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getAppInstance().removeActivity(this);//将当前activity移除管理栈
        if (mvpPresenter != null) {
            mvpPresenter.detach();//在presenter中解绑释放view
            mvpPresenter = null;
        }
        super.onDestroy();
    }

    /**
     * 在子类中初始化对应的presenter
     *
     * @return 相应的presenter
     */
    public abstract P createPresenter();

    @Override
    public void hideLoadingDialog() {

    }

    @Override
    public void showLoadingDialog(String msg) {

    }
}
