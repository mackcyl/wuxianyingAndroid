package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;

public class AddBianqianActivity extends Activity {
	
    private TextView topbar_txt,topbar_right;
    private Button topbar_left;
    private LocalStore localstore;
    private String bianqianid=null;
    private String intentcontent=null;
    
	private EditText content;	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbianqian);
		localstore = new LocalStore();
		bianqianid = getIntent().getStringExtra("id");
		intentcontent = getIntent().getStringExtra("content");
		initWidgets();
		setResult(0,getIntent());
		
	}
	
	private void initWidgets() {
		
		content = (EditText)findViewById(R.id.content);
		if(bianqianid!=null)
			content.setText(intentcontent);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("新建便签");
		 topbar_left = (Button) findViewById(R.id.topbar_left);
		 topbar_left.setVisibility(View.GONE);
		 topbar_left.setEnabled(false);
		 topbar_right = (TextView) findViewById(R.id.topbar_right);
		 topbar_right.setText("保存");
		 topbar_right.setVisibility(View.VISIBLE);
		 topbar_right.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	            	//save()
	            	if(content.getText().toString().equals(""))
	            	{
	            		Toast.makeText(getApplicationContext(), "请输入便签内容。。。",
	            			     Toast.LENGTH_SHORT).show();
	            		return;
	            	}
	            	else
	            	{
	            		if(bianqianid==null)
	            			localstore.addBianqian(AddBianqianActivity.this, content.getText().toString(), content.getText().toString());
	            		else
	            			localstore.editBianqian(AddBianqianActivity.this, content.getText().toString(), content.getText().toString(),bianqianid);
	            		Toast.makeText(getApplicationContext(), "已保存",
	            			     Toast.LENGTH_SHORT).show();
	            	}
	                finish();
	            }
	        });

		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}