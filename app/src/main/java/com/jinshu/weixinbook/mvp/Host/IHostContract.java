package com.jinshu.weixinbook.mvp.Host;


import com.android.volley.RequestQueue;
import com.jinshu.weixinbook.mvp.IBaseModel;
import com.jinshu.weixinbook.mvp.IBasePresenter;
import com.jinshu.weixinbook.mvp.IBaseView;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.Map;

/**
 * Created by jinshu on 2017/8/22.
 */

public interface IHostContract {
    interface View extends IBaseView {
        void hideWaitDialogImpl();

        void showErrorDialogImpl(String str);

        void showWaitDialogImpl(String str);

        void logSetText(String str);

        void nameSetText(String str);

        String logGetText();
    }

    interface Presenter extends IBasePresenter {
        void refresh();

        void appAttendancerun();//上下班

        void upload();
    }

}
