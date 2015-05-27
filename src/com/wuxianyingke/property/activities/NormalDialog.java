package com.wuxianyingke.property.activities;

import com.mantoto.property.R;

import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NormalDialog extends Dialog  implements android.view.View.OnClickListener {

    Context context;
    NormalDialogListener listener1;
    String title,content,buttontext1,buttontext2;
    public int buttonNum = 2;
    
    public interface NormalDialogListener{   
        public void onClick(View view);   
    }   
    
    public NormalDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public NormalDialog(Context context, NormalDialogListener listener1,String title, String content,String buttontext1){
    	//one button
        super(context,R.style.normal_dialog);
        this.context = context;
        this.listener1 =listener1;
        this.title =title;
        this.content =content;
        this.buttontext1 =buttontext1;
        this.buttonNum=1;
    }
    
    public NormalDialog(Context context, NormalDialogListener listener1,String title, String content,String buttontext1,String buttontext2){
    	//two button
        super(context,R.style.normal_dialog);
        this.context = context;
        this.listener1 =listener1;
        this.title =title;
        this.content =content;
        this.buttontext1 =buttontext1;
        this.buttontext2 =buttontext2;
        this.buttonNum=2;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.normal_dialog);
        if(buttonNum==1)
        {
        	initWidget1();
        }
        else if(buttonNum==2)
        {
        	initWidget2();
        }
    }

    private void initWidget1()
    {
    	TextView normal_dialog_title,normal_dialog_content,dialog_button_1,dialog_button_2,dialog_button_3;
        LinearLayout normal_dialog_1,normal_dialog_2;
        
        normal_dialog_title = (TextView)findViewById(R.id.normal_dialog_title);
        normal_dialog_content = (TextView)findViewById(R.id.normal_dialog_content);
        normal_dialog_title.setText(title);
        normal_dialog_content.setText(content);
        
        normal_dialog_1 = (LinearLayout)findViewById(R.id.normal_dialog_1);
        normal_dialog_2 = (LinearLayout)findViewById(R.id.normal_dialog_2);
        normal_dialog_1.setVisibility(View.VISIBLE);
        normal_dialog_2.setVisibility(View.GONE);

        dialog_button_3 = (TextView)findViewById(R.id.dialog_button_3);
        dialog_button_3.setClickable(true);
        dialog_button_3.setOnClickListener(this);
        dialog_button_3.setText(buttontext1);
    }
    
    private void initWidget2()
    {
    	TextView normal_dialog_title,normal_dialog_content,dialog_button_1,dialog_button_2,dialog_button_3;
        LinearLayout normal_dialog_1,normal_dialog_2;
        
        normal_dialog_title = (TextView)findViewById(R.id.normal_dialog_title);
        normal_dialog_content = (TextView)findViewById(R.id.normal_dialog_content);
        normal_dialog_title.setText(title);
        normal_dialog_content.setText(content);
        
        normal_dialog_1 = (LinearLayout)findViewById(R.id.normal_dialog_1);
        normal_dialog_2 = (LinearLayout)findViewById(R.id.normal_dialog_2);
        normal_dialog_1.setVisibility(View.GONE);
        normal_dialog_2.setVisibility(View.VISIBLE);

        dialog_button_1 = (TextView)findViewById(R.id.dialog_button_1);
        dialog_button_1.setClickable(true);
        dialog_button_1.setOnClickListener(this);
        dialog_button_2 = (TextView)findViewById(R.id.dialog_button_2);
        dialog_button_2.setClickable(true);
        dialog_button_2.setOnClickListener(this);

        dialog_button_1.setText(buttontext1);
        dialog_button_2.setText(buttontext2);
    }
    
    @Override  
    public void onClick(View v) {  
        // TODO Auto-generated method stub  
    	listener1.onClick(v);  
        dismiss();  
    }  
    
}