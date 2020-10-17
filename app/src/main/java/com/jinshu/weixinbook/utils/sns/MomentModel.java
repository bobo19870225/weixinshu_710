package com.jinshu.weixinbook.utils.sns;

import java.util.ArrayList;

/**
 * Created by laidayuan on 2017/5/10.
 */

public class MomentModel {

    public String userName;
    public String nickName;
    public String sha1;
    public int type;
    public ArrayList<String> imageList = new ArrayList<String>();
    public int createTime;
    public String content;
    public String seq;  // stringSeq
    public String sep;  // stringSeq
    public String video="0";  // video=1表示视频，不传或video=0就是图片


}
