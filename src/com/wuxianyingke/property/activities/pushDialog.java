package com.wuxianyingke.property.activities;

import com.mantoto.property.R;

import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class pushDialog extends Dialog  implements android.view.View.OnClickListener {

	public static ImageView push_img =null;
	public static TextView push_tv =null;
    Context context;
    pushDialogListener listener1;
    
    public interface pushDialogListener{   
        public void onClick(View view);   
    }   
    
    public pushDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    
    public pushDialog(Context context, pushDialogListener listener1){
    	//two button
        super(context,R.style.normal_dialog);
        this.context = context;
        this.listener1 =listener1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.push_dialog);
        initWidget();
    }

    private void initWidget()
    {
        
        push_img = (ImageView)findViewById(R.id.push_img);
        push_tv = (TextView)findViewById(R.id.push_tv);
        TextView dialog_button_1 = (TextView)findViewById(R.id.dialog_button_1);
        dialog_button_1.setClickable(true);
        dialog_button_1.setOnClickListener(this);
        TextView dialog_button_2 = (TextView)findViewById(R.id.dialog_button_2);
        dialog_button_2.setClickable(true);
        dialog_button_2.setOnClickListener(this);
    }
    
    public ImageView getImage() {
		return push_img;  
    }  
    public TextView getText() {
		return push_tv;  
    }  
    @Override  
    public void onClick(View v) {  
        // TODO Auto-generated method stub  
    	listener1.onClick(v);  
        dismiss();  
    }  
    
}