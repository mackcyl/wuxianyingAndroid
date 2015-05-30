package com.wuxianyingke.property.common;

public class Constants {
    public static boolean DEBUG = true;
    public static String ROLEID = "property";
    public static String PLATFORM = "android";
    public static final String VERSION_CODE = "2.0.2";
    public static final String PACKAGENAME = "com.mantoto.property";
    public static final int HTTP_SO_TIMEOUT = 30 * 1000;
    public static final int HTTP_LO_TIMEOUT = 50 * 1000;
    public static final int ACTION_TYPE_SHARE = 1;
    public static final int ACTION_TYPE_ADD = 2;
    public static final int ACTION_TYPE_BUY = 3;
    public static final int ACTION_TYPE_LIKE = 4;
    public static final int ACTION_TYPE_COMMENT = 5;
    public static final int MESSAGE_IN_BOX_STATUS = 1;// 收件箱
    public static final int MESSAGE_OUT_BOX_STATUS = 2;// 发件箱
    public static final String URL_SETTING = "urlSetting";
    public static final String BANGCLE_URL_SETTING = "bangcleUrl";
    public static final String PRODUCT_URL_SETTING = "productUrl";
    public static final String SHARE_URL_SETTING = "shareUrl";
    public static final String SHOP_URL_SETTING = "shopUrl";
    public static final String SERVER_VERSION = "version";
    //public static final String URL = "http://221.0.78.196:8080/propertymanagement";
    //public static final String URL = "http://42.96.187.128/";

    //public static final String URL = "http://service.mantoto.com/";//正式版本包地址
//    public static final String URL = "http://dev.mantoto.com/";//正式版本包地址


    //public static final String URL = "http://service.51wuye.cn/";//正式版本包地址

    public static final String URL = "http://dev.51wuye.cn";//开发地址
    //public static final String URL = "http://beta.51wuye.cn/";//发布测试包地址

    // http://api.map.baidu.com/geocoder?location=39.920061,116.608678&coord_type=wgs84&output=html&src=wuxianying|propertyManager
    // 公交：http://api.map.baidu.com/place/search?query=公交站&location=%@&coord_type=wgs84&radius=1000&region=%@&output=html&src=wuxianying|propertyManager
    // 医疗：http://api.map.baidu.com/place/search?query=医院&location=%@&coord_type=wgs84&radius=1000&output=html&src=wuxianying|propertyManager



    public static final String LOADING_PIC_PATH = "/data/data/" + PACKAGENAME
            + "/files/";
    public static final String LOADING_PIC_FILENAME = "loading.png";
    public static final String LOGIN_PIC_FILENAME = "login_logo.png";
    // public static final String URL =
    // "http://221.0.78.196:8080/Test/RePara.aspx" ;
    // public static final String ORIGINAL_SHOP_URL =
    // "http://kuba.3gpingtai.cn:8580/";

    // public static final String ORIGINAL_SHOP_URL =
    // "http://192.168.1.155:8081/";

    public static final String ORIGINAL_VERSION = "0";
    public static final String POLICYSERVER_FOLLOW = "/management/programManage.do?method=getServerConfig&configversion=";
    public static final String UPDATESERVER_FOLLOW = "/ProgramManage.aspx";
    public static final String SUBMITCHANNEL_FOLLOW = "/management/mobileUserInfo.do";
    public static final String APK_CHECK_URL = "protectApk/uploadProtectApkInfo.action";
    public static final String MP_URL = "/management/";
    public static final String URL_HELP = "file:///android_asset/html/sec_opt_question.html";
    public static final String MAINACTIVITYINDEXACTION = "main_activity_index_action";

    // 优惠券类型
    public static final int COUPON_TYPE_UNUSED = 0;
    public static final int COUPON_TYPE_USED = 1;
    public static final int COUPON_TYPE_OUTDATED = 2;
    public static final boolean QUN_TEST_MODE = false;

    // 分享类型
    public static final int SHARE_TYPE_PRODUCT = 0;
    public static final int SHARE_TYPE_WEIBO = 1;
    public static final int SHARE_TYPE_SMS = 2;
    public static final int SHARE_TYPE_EMAIL = 3;

    // Loading启动页面图片
    public static final String GET_LOADING_URL = "/Json/Start.aspx";
    // 验证小区验证码
    public static final String INVITATION_CODE_URL = "/Json/propertyCode.aspx";
    // 登陆注册
    public static final String COO8_USER_LOGIN_URL = "login";
    public static final String USER_LOGIN_URL = "/Json/UserVerify.aspx";
    public static final String USER_REGISTER_URL = "/Json/UserCreate.aspx";
    public static final String USER_CENTER_URL = "/Json/JsonUserCenter.aspx";
    public static final String MODIFY_PASSWORD_URL = "/Json/UserResetPassword.aspx";
    public static final String CHECKCODE_URL = "/Json/MobileCheckCodeGet.aspx";
    // 用户中心

    public static final String GET_USER_CENTER_URL = "/Json/JsonUserCenter.aspx";
    public static final String GET_PERSONALINFOMATION_URL = "/Json/UserGet.aspx";
    public static final String UPDATE_PERSONALINFOMATION_URL = "/Json/UserInfoUpdate.aspx";
    public static final String USER_FIND_PASSWORD_URL = "/koo8_findPwd.action";
    public static final String COO8_LOGIN_BYPHONE_URL = "memhome/user/user_loginByMobile.action";
    public static final String COO8_REG_BYPHONE_URL = "memhome/user/user_registerByMobile.action";
    public static final String COO8_GET_VALIDATE_CODE_URL = "getValidateCode";
    public static final String LOGIN_TYPE_TAG = "loginTAG";
    public static final String REGISTER_TYPE_TAG = "registerTAG";
    public static final String FORGET_TYPE_TAG = "forgetTAG";
    public static final String READ_TYPE_TAG = "readTAG";
    public static final int MSG_LOCATION_READY = 300;

    // 首页
    public static final String GET_HOME_MSG_URL = "/Json/Home.aspx";
    // 常用信息
    public static final String GET_INFOMATIONS_URL = "/Json/Informations.aspx";
    // 常用信息
    public static final String GET_PAID_SERVICES_URL = "/Json/Services.aspx";
    // 物业通知
    public static final String GET_PROPERTY_NOTIFICATION_URL = "/Json/Notes.aspx";
    // 通知物业代收
    public static final String SEND_PROPERTY_COLLECTION_URL = "/Json/ExpressNew.aspx";
    // 获取通知物业代收
    public static final String GET_PROPERTY_COLLECTION_URL = "/Json/Expresses.aspx";
    // 获取消息分类列表
    public static final String GET_MESSAGE_TYPE_URL = "/Json/MessageTypes.aspx";
    // 发送消息
    public static final String SEND_MESSAGE_URL = "/Json/MessageSend.aspx";
    // 回复消息
    public static final String SEND_MESSAGE_REPLY_URL = "/Json/MessageReply.aspx";
    // 获取收件箱消息列表
    public static final String SEND_MESSAGE_IN_BOX_URL = "/Json/MessageInbox.aspx";
    // 获取收件箱消息列表
    public static final String SEND_MESSAGE_IN_BOX_CONTENT_URL = "/Json/MessagesByRoot.aspx";
    // 获取发件箱消息列表
    //public static final String SEND_MESSAGE_OUT_BOX_URL = "/Json/MessageOutbox.aspx";
    // rootlist
    public static final String SEND_MESSAGE_OUT_BOX_URL = "/Json/MessageRootList.aspx";
    // 联系发布人
    public static final String SEND_MESSAGE_FROM_FLEA_URL = "/Json/MessageSendFromFlea.aspx";
    // 发布跳骚商品
    public static final String SEND_NEW_FLEA_URL = "/Json/FleaNew.aspx";
    // 编辑跳骚商品
    public static final String EDIT_FLED_URL = "/Json/FleaEdit.aspx";
    // 删除跳骚商品
    public static final String DELETE_FLEA_URL = "/Json/FleaDelete.aspx";
    // 获取跳骚商品列表
    public static final String GET_FLEA_BY_PROPERTY_URL = "/Json/FleaGetByProperty.aspx";
    // 我的跳骚商品列表
    public static final String GET_FLEA_GET_OWNER_URL = "/Json/FleaGetByOwner.aspx";
    // 跳骚商品详情
    public static final String GET_FLEA_URL = "/Json/FleaGet.aspx";
    // wifi密码
    public static final String SEND_GET_WIFI_PASSWORD_URL = "/Json/wifiPasswordGet.aspx";

    // 分类
    public static final String GET_CATEGORY_URL = "/Json/CategoryRoots.aspx";
    public static final String GET_CHILDREN_CATEGORY_URL = "/Json/CategoryChildren.aspx";

    // 获取商品列表
    public static final String GET_PRODUCT_LIST_URL = "/Json/JsonCategoryProduct.aspx";

    // 获取商品详情
    public static final String GET_PRODUCT_DETAIL = "/Json/JsonProductDetails.aspx";
    // 商家快报
    public static final String GET_BUSINESSESEXPRESS = "/Json/productMessageGet.aspx";
    // 站内信
    public static final String GET_NOTE_MESSAGE = "/Json/NoteGet.aspx";
    public static final String NOTESREMOVE_URL = "/Json/NotesRemove.aspx";
    // push
    public static final String GET_PUSH_MSG = "/Json/PushMessages.aspx";
    // 4.8.2	获取生活项列表
    public static final String LIVING_ITEMS = "/Json/LivingItems.aspx";
    // 4.8.3	获取指定生活项
    public static final String GET_LIVING_ITEM_GET = "/Json/LivingItemGet.aspx";
    // 4.8.4	获取指定生活项的图片列表
    public static final String GET_LIVING_ITEM_PICTURE_GET = "/Json/LivingItemPictureGet.aspx";
    // 4.8.4	获取指定生活项的图片列表
    public static final String GET_YINGYONGTUIJIAN_GET = "/Json/AppPackageAndroid.aspx";
    // 4.8.5 6
    public static final String GET_PROMOTTION_ACTIVITY_GET = "/Json/PromotionActivityGet.aspx";
    public static final String GET_PROMOTTION_PRODUCT_GET = "/Json/PromotionProductGet.aspx";
    // 4.7.4	TIANQI
    public static final String GET_WEATHER_GET = "/Json/GetWeather.aspx";

    // 广场使用
    public static final String GET_APP_INFO_URL = "user/appInfoAndUsers.action";
    public static final String GET_RECOM_APP_URL = "app/getKuBa.action";
    public static final String GET_ALL_LOGS_URL = "memhome/act/act_getuserAct.action";
    public static final String GET_RECOM_USER_URL = "memhome/act/act_getUserInfo.action";
    public static final String UPLOAD_USER_LOG_URL = "memhome/act/act_uploadUserAct.action";

    public static final String COO8_QUCIK_SEARCH_URL = "matchKeyword";
    public static final String COO8_NORMAL_SEARCH_URL = "searchProduct";
    public static final String COO8_GET_INDEX_INFO_URL = "getHotSales";
    public static final String COO8_GET_PRODUCT_DETAIL_URL = "getProductDetail";
    public static final String COO8_GET_CATE_URL = "getCateList";
    public static final String COO8_GET_PRODUCT_LIST_URL = "getProductList";
    public static final String COO8_GET_BRAND_LIST_URL = "getBrandList";

    // 优惠券相关
    public static final String COO8_GET_COUPON_LIST_URL = "getCouponList";
    public static final String COO8_ACTIVATE_COUPON_URL = "activateCoupon";

    public static final String COO8_GET_ORDER_LIST_URL = "getOrderList";
    public static final String COO8_GET_ORDER_DETAIL_URL = "getOrderDetail";
    public static final String GET_FAVORITES_URL = "/Json/ProductCollectGetAll.aspx";
    public static final String DELETE_FROM_FAVORITE_URL = "/Json/ProductCollectRemove.aspx";
    public static final String ADD_TO_FAVORITES_URL = "/Json/ProductCollectAdd.aspx";

    public static final String SUBMIT_FEEDBACK_URL = "/Json/NotesSubmitFeedback.aspx";// 提交用户反馈信息
    public static final String COO8_GET_INVOICE_URL = "getInvoice";
    public static final String COO8_UPDATE_INVOICE_URL = "updateInvoice";
    public static final String COO8_QUERY_STORAGE_URL = "queryStorage";

    public static final String COO8_COMMENT_URL = "getComments";
    public static final String COO8_TOPICS_URL = "getSpecicalList";

    // 地址管理

    public static final String GET_AREA_LIST_URL = "/Json/getAreaList.aspx";

    public static final String COO8_GET_AREA_LIST_URL = "getAreaList";
    public static final String COO8_GET_ADDRESS_LIST_URL = "getAddressList";
    public static final String COO8_ADD_ADDRESS_URL = "addAddress";
    public static final String COO8_UPDATE_ADDRESS_URL = "updateAddress";
    public static final String COO8_DELETE_ADDRESS_URL = "deleteAddress";
    public static final String COO8_SET_DEFALUT_ADDRESS_URL = "setDefaultAddress";
    // 所有用到的消息写在这里，int值递增！
    public static final int MSG_NETWORK_ERROR = 101;
    public static final int MSG_GET_ALL_LOGS_FINISH = 102;
    public static final int MSG_GET_LOGICON_FINISH = 103;
    public static final int MSG_GET_RECOM_APPS_FINISH = 104;
    public static final int MSG_GET_APP_ICON_FINISH = 105;
    public static final int MSG_GET_APPINFO_FINISH = 106;
    public static final int MSG_GET_RECOM_USERS_FINISH = 107;
    public static final int MSG_GET_USER_ICON_FINISH = 108;
    public static final int MSG_GET_SEARCH_RESULT_FINISH = 109;
    public static final int MSG_GET_SEARCH_ICON_FINISH = 110;
    public static final int MSG_GET_QUICK_SEARCH_FINISH = 111;
    public static final int MSG_GET_INFOMATION_FINISH = 114;
    public static final int MSG_GET_PRODUCTMESSAGE_FINISH = 112;
    public static final int MSG_GET_PAID_SERVICES_FINISH = 113;
    public static final int MSG_GET_NOTEAGE_FINISH = 115;
    public static final int MSG_GET_USERCENTER_FINISH = 116;
    public static final int MSG_GET_PERSONALINFOMATION_FINISH = 117;
    public static final int MSG_UPADTE_PERSONALINFOMATION_FINISH = 118;
    public static final int MSG_PROPERTY_NOTIFICATION_FINISH = 119;
    public static final int MSG_PROPERTY_COLLECTION_FINISH = 200;
    public static final int MSG_GET_TUIJIANYINGYONG_FINISH = 201;
    public static final int MSG_GET_MESSAGE_TYPE_FINISH = 90;
    public static final int MSG_MESSAGE_IN_BOX_FINISH = 91;
    public static final int MSG_MESSAGE_OUT_BOX_FINISH = 92;
    public static final int MSG_MESSAGE_IN_BOX_CONTENT_FINISH = 93;

    public static final int MSG_CHECK_VERSION_FINISH = 201;
    public static final int MSG_SUBMIT_FEEDBACK_FINISH = 202;
    public static final int MSG_GET_FAVORITES_FINISH = 203;
    public static final int MSG_GET_FAVORITE_IMG_FINISH = 204;
    public static final int MSG_ADDTO_FAVORITE_FINISH = 205;
    public static final int MSG_DELETE_FAVORITE_FINISH = 206;
    public static final int MSG_GET_ORDER_LIST_FINISH = 207;
    public static final int MSG_GET_ORDER_DETAIL_FINISH = 208;
    public static final int MSG_GET_COUPON_FINISH = 209;
    public static final int MSG_UPDATE_COUPON_FINISH = 210;
    public static final int MSG_GET_ADDRESS_LIST_FINISH = 211;
    public static final int MSG_ADD_ADDRESS_FINISH = 212;
    public static final int MSG_UPDATE_ADDRESS_FINISH = 213;
    public static final int MSG_DELETE_ADDRESS_FINISH = 214;
    public static final int MSG_GET_AREA_LIST_FINISH = 215;
    public static final int MSG_DELETE_FAVORITE = 216;
    public static final int MSG_GOTO_PRODUCT_DETAIL = 217;
    public static final int MSG_GET_INVOICE_FINISH = 218;
    public static final int MSG_UPDATE_INVOICE_FINISH = 219;

    public static final int MSG_GET_INDEX_INFO_FINISH = 220;
    public static final int MSG_GET_INDEX_INFO_FAILD = 221;
    public static final int MSG_GET_INDEX_INFO_NET_ERROR = 222;
    public static final int MSG_GET_INDEX_GALLERY_IMG_FINISH = 223;
    public static final int MSG_GET_INDEX_FLIPPER_IMG_FINISH = 224;

    public static final int MSG_GET_PRODUCT_DETAIL_FINISH = 231;
    public static final int MSG_GET_PRODUCT_DETAIL_FAILD = 232;
    public static final int MSG_GET_PRODUCT_DETAIL_NET_ERROR = 233;
    public static final int MSG_GET_PRODUCT_DETAIL_IMG_FINISH = 234;
    public static final int MSG_GET_PRODUCT_FINISH = 236;
    public static final int MSG_GET_ACTIVITY_FINISH = 237;
    public static final int MSG_GET_PRODUCT_IMG_FINISH = 238;
    public static final int MSG_GET_ACTIVITY_IMG_FINISH = 239;
    public static final int MSG_ADD_FAVORITE_FINISH = 235;
    public static final int MSG_GET_COUPON_LIST_FINISH = 241;
    public static final int MSG_SET_DEFALUT_ADDRESS_NETWORK_ERROR = 242;
    public static final int MSG_SET_DEFALUT_ADDRESS_FINISH = 243;

    public static final int MSG_GET_FAVORITES_EMPTY = 244;

    public static boolean productItem = false;
    public static final int MSG_LIST_CHANGE = 154;
    public static final int MSG_GET_ALL_CART_FINISH = 111;
    public static final String DELIBER_GOODS_TYPE_URL = "getShipType";
    public static final String DEFRAY_TYPE_URL = "getPayType";
    public static final String PRODUCT_ID_ACTION = "product_id_action";
    public static final String CANYIN_ID_ACTION = "canyin_id_action";
    public static final String CANYIN_SOURCE_ACTION = "canyin_source_action";
    public static final String SEND_ORDER_URL = "submitOrder";
    public static final String SEND_BANGBANG_ORDER_URL = "/memhome/order/backupOrder.action";
    public static final String ORDER_SETTING_URL = "getLastOrder";
    public static final String CALCORDER_URL = "checkShoppingCart";
    public static final String CALCULATE_ORDER_URL = "calcOrder";
    public static final int MSG_ORDER_ERROR = 153;
    public static final int MSG_GET_ORDER_SUCCESS_FINISH = 145;
    public static final int MSG_GET_ALL_ADDRESS_FINISH = 107;
    public static final int MSG_GET_ORDER_SETTING_FINISH = 108;
    public static final int MSG_FROMCART = 109;
    public static final int MSG_FROMORDERCART = 110;
    public static final int MSG_CART_NULL = 112;
    public static final int MSG_FAVORITE_NETWORK_ERROR = 113;

    public static final int MSG_GET_CATE_FINISH = 241;
    public static final int MSG_GET_CATE_FAILD = 242;
    public static final int MSG_GET_CATE_NET_ERROR = 243;
    public static final int MSG_GET_CATE_IMG_FINISH = 244;
    public static final int MSG_CLICK_PRODUCT_LIST_ITEM = 245;

    public static final int MSG_GET_PRODUCT_LIST_FINISH = 251;
    public static final int MSG_GET_PRODUCT_LIST_FAILD = 252;
    public static final int MSG_GET_PRODUCT_LIST_NET_ERROR = 253;
    public static final int MSG_GET_PRODUCT_LIST_IMG_FINISH = 254;
    public static final int MSG_GET_PRODUCT_LIST_EMPTY = 275;

    public static final int MSG_GET_COMMENT_LIST_FINISH = 261;
    public static final int MSG_GET_COMMENT_LIST_FAILD = 262;
    public static final int MSG_GET_COMMENT_LIST_NET_ERROR = 263;

    public static final int MSG_GET_TOPICS_LIST_FINISH = 271;
    public static final int MSG_GET_TOPICS_LIST_FAILD = 272;
    public static final int MSG_GET_TOPICS_LIST_NET_ERROR = 273;
    public static final int MSG_GET_TOPICS_LIST_IMG_FINISH = 274;

    public static final int MSG_GET_CANYIN_LIST_FINISH = 280;
    public static final int MSG_GET_CANYIN_LIST_EMPTY = 281;
    public static final int MSG_GET_CANYIN_DETAIL_IMG_FINISH = 282;

    public static final String SINA_WEIBO_INFO = "kuba_sina_weibo_info";
    public static final String ACCESS_TOKEN = "weibotoken";
    public static final String ACCESS_TOKEN_SECRET = "weibotokenSecret";
    public static final int MSG_LOGIN_SUCCESS = 0;
    public static final int MSG_LOGIN_FAILED = -1;
    public static final int MSG_LOGIN_ERROR = -2;

    public static final String CATE_ID_ACTION = "cate_id_action";
    public static final String COMMENT_TYPE_ACTION = "comment_type_action";
    public static final String PARENT_ID_ACTION = "parent_id_action";
    public static final String CATE_NAME_ACTION = "cate_name_action";
    public static final String TPOICS_ID_ACTION = "tpoics_id_action";
    public static final String BRAND_NAME_ACTION = "brand_name_action";
    public static final String PRODUCT_NAME_ACTION = "product_name_action";

    public static final String ALARM_INTENT_FILTER11 = "alarm_intent_filter11";
    public static final String ALARM_INTENT_FILTER17 = "alarm_intent_filter17";

    public static int RADIO_CHECKED = 1;// 记录底部radio被点击

    public static String SHOUCANG_FLAT="shoucang_flag";
    public static String FAVORITE_FLAT= "favorite_flag";

//    public static int FAVORITE_FLAT = 0;//收藏餐饮

    public static int FLAG_CANYIN = 0;//收藏餐饮
    public static int FLAG_GOUWU = 1;// 收藏购物
    public static int FLAG_SHENGHUOFUWU = 2;// 收藏生活服务

    //获取最新的已派单报修日志接口地址
    public static final String REPAIR_LOG_LASTEST_URL = "/Json/RepairLogLastest.aspx";
    public static final String REPAIR_TYPE_LIST_URL = "/Json/RepairTypesGet.aspx";
    public static final String REPAIR_LIST_URL = "/Json/RepairsGet.aspx";
    public static final String REPAIR_DETAIL_URL = "/Json/RepairDetailGet.aspx";
    public static final String REPAIR_CREATE_URL = "/Json/RepairCreate.aspx";
    public static final String REPAIR_SOLVED_URL = "/Json/RepairSolved.aspx";
    public static final String REPAIR_UNSOLVED_URL = "/Json/RepairUnsolved.aspx";
    public static final String REPAIR_REMOVE_URL = "/Json/RepairRemove.aspx";

    public static final int MSG_GET_REPAIR_LOG_LAST_FINSH = 282;
    public static final int MSG_GET_REPAIR_LOG_LAST_ERROR = 283;
    public static final int MSG_GET_REPAIR_TYPE_LIST_FINSH = 284;
    public static final int MSG_GET_REPAIR_TYPE_LIST_ERROR = 285;
    public static final int MSG_GET_REPAIR_LIST_FINSH = 286;
    public static final int MSG_GET_REPAIR_DETAIL_FINSH = 288;
    public static final int MSG_GET_REPAIR_PIC_FINSH = 300;
    public static final int MSG_GET_REPAIR_PIC_LIST_FINSH = 301;
}
