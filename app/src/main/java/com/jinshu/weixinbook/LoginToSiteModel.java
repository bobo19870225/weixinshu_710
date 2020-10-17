package com.jinshu.weixinbook;

import com.jinshu.weixinbook.jsonParsing.BaseModel;
import com.jinshu.weixinbook.jsonParsing.JSONKey;

/**
 * Created by jinshu on 2017/6/29.
 */

public class LoginToSiteModel extends BaseModel {
    //    "siteID":"8a2f462a5ce292ca015ce424f7a13077",
//            "siteName":"贵州微信书",
//            "shortName":"gz",
//            "domain":"wxbook.haoju.me/sgybook/public/index.php",
//            "ipaddr":"wxbook.haoju.me/sgybook/public/index.php",
//            "weixinID":null,
//            "weixinName":null,
//            "masterURL":"wxbook.haoju.me/sgybook/public/index.php",
//            "resourceURL":"4",
//            "userDataURL":"5",
//            "isLock":0,
//            "isValid":1
    @JSONKey(keys = "masterURL", type = String.class)
    public String masterURL;
    @JSONKey(keys = "siteID", type = String.class)
    public String siteID;
    @JSONKey(keys = "listImage", type = String.class)
    public String listImage;
    @JSONKey(keys = "faceImage", type = String.class)
    public String faceImage;
}
