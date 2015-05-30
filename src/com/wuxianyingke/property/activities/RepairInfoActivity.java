package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.RepairPicture;
import com.wuxianyingke.property.threads.GetRepairInfoThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;


public class RepairInfoActivity extends Activity {

    private MyGallery mMyGallery = null;
    private IndicationDotList mDotList = null;

    private ImageAdapter mAdapter = null ;
    private View mImgView = null;
    private ProgressDialog mWaitLoading = null ;
    private int propertyid;
    private ScrollView mAllViewSv = null ;
    ImageView cartImageview;
    private Button topbar_left;
    private TextView topbar_txt ,repair_desc ,repair_ctime;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();
    private float latitude,longitude;
    private long repairId,userid;
    private String mRepairBody , mRepairCTime;
    private ArrayList<RepairPicture> repairPicList;

    private GetRepairInfoThread mThread;


    private LinearLayout repairInfo;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR:
                    if(mWaitLoading != null && mWaitLoading.isShowing()){
                        mWaitLoading.dismiss() ;
                    }
                    mAllViewSv.setVisibility(View.GONE) ;
                    View view = (View) findViewById(R.id.view_network_error);
                    view.setVisibility(View.VISIBLE);
                    break ;
                case Constants.MSG_GET_REPAIR_PIC_LIST_FINSH:

                    if(mThread != null && mThread.getRepairImgItem() != null){
                        repairPicList = mThread.getRepairImgItem();
                        for(int i = 0; i<repairPicList.size();i++)
                        {
                            RepairPicture repairPic = repairPicList.get(i);
                            View v = getLayoutInflater().inflate(R.layout.canyin_detail_own_content, null);

//                            int serverImageWidth = promotion.Width;
//                            int serverImageHeight = promotion.Height;

                            ImageView canyinImg = (ImageView) v.findViewById(R.id.canyinImg);

//						Log.i("ACTIVITY_IMG_FINISH", "图片尺寸: 宽度 = " + canyinImg.getWidth() + "高度 = :" + canyinImg.getHeight());
                            canyinImg.setScaleType(ImageView.ScaleType.FIT_XY);
                            Display display = getWindowManager().getDefaultDisplay();
//                            LayoutParams params = canyinImg.getLayoutParams();
//                            params.width = display.getWidth()-20;
//                            params.height = (display.getWidth()-20)*serverImageHeight/serverImageWidth;

//                            canyinImg.setLayoutParams(params);
                            canyinImg.setVisibility(View.GONE);
                            activityImgList.add(canyinImg);
                            repairInfo.addView(v);
                        }

                    }
                    break;
                case Constants.MSG_GET_ACTIVITY_IMG_FINISH:
                {
                    ImageView canyinImg= activityImgList.get(msg.arg1);
                    ViewGroup.LayoutParams pr=canyinImg.getLayoutParams();
                    canyinImg.setImageDrawable(mThread.getDrawable(msg.arg2));
                    canyinImg.setLayoutParams(pr);
                    canyinImg.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    } ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.repair_info) ;
        SharedPreferences saving = this.getSharedPreferences(
                LocalStore.USER_INFO, 0);
        propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);

        topbar_txt= (TextView) findViewById(R.id.topbar_txt) ;
        topbar_left=(Button)findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_txt.setText("报修详情");
        topbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        repairId =(long) bundle.getLong("repairId");

        mRepairBody = bundle.getString("repairDesc");
        mRepairCTime = bundle.getString("repairCTime");
        repair_desc = (TextView)findViewById(R.id.repair_desc);
        repair_ctime = (TextView)findViewById(R.id.repair_ctime);
        repair_desc.setText(mRepairBody);
        repair_ctime.setText(mRepairCTime);

        repairInfo = (LinearLayout)findViewById(R.id.repair_info);

        userid = LocalStore.getUserInfo().userId;
        mThread = new GetRepairInfoThread(this,mHandler,propertyid,repairId,userid);
        mThread.start() ;

    }
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void changeTo(int i)
    {
        repairInfo.setVisibility(View.VISIBLE);
    }

    private void showDialog(){
        mWaitLoading = new ProgressDialog(RepairInfoActivity.this);
        String msg = getResources().getString(R.string.txt_loading) ;
        mWaitLoading.setMessage(msg);
        mWaitLoading.setCancelable(false);
        mWaitLoading.show();
    }

}
