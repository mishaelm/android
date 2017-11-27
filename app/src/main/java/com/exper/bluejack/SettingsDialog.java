package com.exper.bluejack;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by mishael on 11/23/17.
 */

public class SettingsDialog extends Dialog {
    EditText ipText;
    EditText portText;
    Button submitBtn;


    public SettingsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_layout);
        ipText = findViewById(R.id.dialog_ip_text);
        portText = findViewById(R.id.dialog_port_text);
        submitBtn = findViewById(R.id.dialog_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.IP = ipText.getText().toString();
                MainActivity.PORT = Integer.valueOf(portText.getText().toString());
                dismiss();
            }
        });



    }
}
