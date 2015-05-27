package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;

public class MainActivity2 extends Activity {
	private GridView mGridView; // MyGridView
	private LinearLayout  mTuichuLinearLayout,mShouyeLinearLayout,mBohaoLinearLayout;
	// 定义图标数组
	private int[] imageRes = { R.drawable.style_tongzhi,R.drawable.style_xinxi,
			R.drawable.style_daishoukuaidi, R.drawable.style_tiaozhaoshichang,
			R.drawable.style_canyin, R.drawable.style_gouwu,
			R.drawable.style_jiaotong, R.drawable.style_yiliao,
			R.drawable.style_shenghuofuwu, R.drawable.style_changyongxinxi,
			R.drawable.style_youchangfuwu, R.drawable.style_xiugaimima,
			R.drawable.style_shenghuobianqian, R.drawable.style_shezhi
			 };
	
	// 定义标题数组
	private int[] itemName = {
			R.string.gridview_tongzhi,
			R.string.gridview_xinxi,
			R.string.gridview_daishoukuaidi,
			R.string.gridview_tiaozhaoshichang,
			R.string.gridview_canyin,
			R.string.gridview_gouwu,
			R.string.gridview_jiaotong,
			R.string.gridview_yiliao,
			R.string.gridview_shenghuofuwu,
			R.string.gridview_changyongxinxi,
			R.string.gridview_youchangfuwu,
			R.string.gridview_xiugaimima,
			R.string.gridview_shenghuobianqian,
			R.string.gridview_shezhi
			};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE); //
		// 设置Activity标题不显示
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
		Log.d("MyTag","getString(R.string.gridview_tongzhi)"+getResources().getString(R.string.about));
		setContentView(R.layout.gridview_main);
		initWidget();
		
	}
    private void initWidget()
    {
        mTuichuLinearLayout= (LinearLayout) findViewById(R.id.TuichuLinearLayout);
        mShouyeLinearLayout= (LinearLayout) findViewById(R.id.ShouyeLinearLayout);
        mBohaoLinearLayout= (LinearLayout) findViewById(R.id.BohaoLinearLayout);
        mTuichuLinearLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                confirmLogouDialog();
            }
        });
        mShouyeLinearLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity2.this , 
                        Radio1Activity.class) ;
                startActivity(intent) ;
            }
        });
        mBohaoLinearLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse("tel:"
                        + LocalStore.getInvitationCode(MainActivity2.this).phoneNumber);
                Intent it = new Intent(Intent.ACTION_CALL, uri);
                startActivity(it);
            }
        });
        
        mGridView = (GridView) findViewById(R.id.MyGridView);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setNumColumns(4);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int length = itemName.length;
        for (int i = 0; i < length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImageView", imageRes[i]);
            map.put("ItemTextView", getResources().getText(itemName[i]));
            data.add(map);
        }
        // 为itme.xml添加适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity2.this,
                data, R.layout.gridview_item, new String[] { "ItemImageView",
                        "ItemTextView" }, new int[] { R.id.ItemImageView,
                        R.id.ItemTextView });
        mGridView.setAdapter(simpleAdapter);
        // 为mGridView添加点击事件监听器
        mGridView.setOnItemClickListener(new GridViewItemOnClick());
    }
	// 定义点击事件监听器
	public class GridViewItemOnClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
		    Intent intent = new Intent();
		    switch(position){
            case 0:
               
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, Radio2Activity.class);
                startActivity(intent);
                break;
            case 1:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, Radio3Activity.class);
                startActivity(intent);
                break;
            case 2:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, Radio4Activity.class);
                startActivity(intent);
                break;
            case 3:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, ProductListActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, CanYinListActivity.class);
                startActivity(intent);
                break;
            case 5:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, GouWuListActivity.class);
                startActivity(intent);
                break;
            case 6:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, JiaoTongActivity.class);
                startActivity(intent);
                break;
            case 7:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, YiLiaoActivity.class);
                startActivity(intent);
                break;
            case 8:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, ShengHuoFuWuListActivity.class);
                startActivity(intent);
                break;
            case 9:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, CommonInfomationActivity.class);
                startActivity(intent);
                break;
            case 10:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, PaidServicesActivity.class);
                startActivity(intent);
                break;
            case 11:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, ModifyPasswordActivity.class);
                startActivity(intent);
                break;
            case 12:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, StickerActivity.class);
                startActivity(intent);
                break;
            case 13:
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(MainActivity2.this, LivingCircleActivity.class);
                startActivity(intent);
                break;
		    }
			Toast.makeText(getApplicationContext(), position + "",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	protected void confirmLogouDialog() {
        AlertDialog.Builder builder = new Builder(this);

        String strTitle = getResources().getString(R.string.txt_tips);
        String strOk = getResources().getString(R.string.txt_ok);
        String strCancel = getResources().getString(R.string.txt_cancel);

        builder.setTitle(strTitle);
        builder.setMessage("确认退出登录吗？");

        builder.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                LocalStore.logout(MainActivity2.this);

                Intent intent = new Intent();
                intent.setClass(MainActivity2.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });

        builder.setNegativeButton(strCancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

}