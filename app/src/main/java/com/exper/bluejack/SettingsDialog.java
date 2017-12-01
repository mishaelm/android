package com.exper.bluejack;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by mishael on 11/23/17.
 */

public class SettingsDialog extends Dialog {
    final  String TAG =  "mmmmDialog";
    EditText ipText;
    EditText portText;
    Button submitBtn;
    Button cancelBtn;


    public SettingsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_layout);
        ipText = findViewById(R.id.dialog_ip_text);
        portText = findViewById(R.id.dialog_port_text);
        submitBtn = findViewById(R.id.dialog_submit_btn);
        cancelBtn = findViewById(R.id.dialog_cancel_btn);
        ipText.setHint(MainActivity.IP);
        portText.setHint(String.valueOf(MainActivity.PORT) );

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipInputText = ipText.getText().toString();
                if (ipInputText != null && ipInputText != ""){
                    MainActivity.IP = ipInputText;
                }
                String portInput = portText.getText().toString();
                Log.v(TAG, "port input  = "+portInput);
                if (portInput != null && !portInput.equals("")){
                    MainActivity.PORT = Integer.valueOf(portInput);
                }
                dismiss();
            }
        });



    }
}
