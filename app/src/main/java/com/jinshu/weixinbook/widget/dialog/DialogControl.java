package com.jinshu.weixinbook.widget.dialog;

/**
 * Created by admin on 2016/11/23.
 *
 */
public interface DialogControl {
    public abstract void hideWaitDialog();

    public abstract MyProgressDialog showWaitDialog();

    public abstract MyProgressDialog showWaitDialog(int resid);

    public abstract MyProgressDialog showWaitDialog(String text);

    public abstract void hideErrorDialog();

    public abstract CenterDialog showErrorDialog(int resid);

    public abstract CenterDialog showErrorDialog(String text);

    public abstract CenterDialog showErrorDialog(String text, CenterDialog.OnCenterItemClickListener listener);

    public abstract OkDialog showOkDialog(String text, OkDialog.OnCenterItemClickListener listener);

}
