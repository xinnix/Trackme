package com.cloudbean.trackme.dialog;


import com.cloudbean.trackme.R;
import com.cloudbean.trackme.TrackApp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IPDialog extends Dialog{
	
	public IPDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public interface OnCustomDialogListener{
        public void back(String name);
	}
	
	
	
    EditText dip;
    EditText cip;
    Button btSet;
	
	 @Override
     protected void onCreate(Bundle savedInstanceState) { 
             super.onCreate(savedInstanceState);
             setContentView(R.layout.dialog_setserver);
             //…Ë÷√±ÍÃ‚
            
             dip = (EditText)findViewById(R.id.dserver);
             cip = (EditText)findViewById(R.id.cserver);
             dip.setText(TrackApp.dServerAddr);
             cip.setText(TrackApp.cServerAddr);
             btSet = (Button) findViewById(R.id.btSetServer);
             btSet.setOnClickListener(clickListener);
     }
     
     private View.OnClickListener clickListener = new View.OnClickListener() {
             
             @Override
             public void onClick(View v) {
                  TrackApp.dServerAddr =  dip.getText().toString();
                  TrackApp.cServerAddr =  cip.getText().toString();
                  
                  IPDialog.this.dismiss();
             }
     };

}
