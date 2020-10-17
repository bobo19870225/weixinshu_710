package com.jinshu.weixinbook.mvp.Host;

import com.jinshu.weixinbook.mvp.IBasePresenter;
import com.jinshu.weixinbook.mvp.IBaseView;
import com.jinshu.weixinbook.utils.sns.UserModel;

import java.util.List;

/**
 * Created by jinshu on 2017/9/20.
 */

public class ISearchContract {
    interface View extends IBaseView {
        public void setMyList(List lists);
        void hideWaitDialogImpl();

        void showErrorDialogImpl(String str);

        void showWaitDialogImpl(String str);
        void jump(UserModel um);

    }

    interface Presenter extends IBasePresenter  {
        public void search(String str );
        public void onItemClick(UserModel um);
    }
}
