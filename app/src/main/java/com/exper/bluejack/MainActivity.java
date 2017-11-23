package com.exper.bluejack;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "mmmMainActivity";
    private static boolean enableAttack = false;

    private static Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = findViewById(R.id.lisView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(Color.parseColor("#3F7AE0"));
                enableAttack = true;
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                ImageButton btn =  findViewById(R.id.attack_indicator);
                btn.setImageDrawable(getResources().getDrawable(R.drawable.btn_off3));

                Log.v(TAG, "handler received message");
                if (message.arg1 == 0){
                    Bundle temp = message.getData();
                    TextView textView = findViewById(R.id.server_response_text);
                    textView.setText(temp.getString("server_response"));;
                }
                return false;
            }
        });


    }



    public void scanBtnClicked(View v){
        ListView list = findViewById(R.id.lisView);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            s.add(bt.getName());

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_layout, s));
    }



    public void attackBtnClicked(View v){
        if (enableAttack){
            ImageButton attackIndicator =  findViewById(R.id.attack_indicator);
            attackIndicator.setImageDrawable(getResources().getDrawable(R.drawable.btn_on3));
            new Thread(new Run()).start();
        }else{
            Toast.makeText(this, "you must choose a device to attack", Toast.LENGTH_LONG).show();
        }


    }




    public void resetBtnClicked(View v){

    }


    public void settingsBtnClicked(View v){

    }

    private void setServerResponse(String response){
        TextView textView =  findViewById(R.id.server_response_text);
        textView.setText(response);
    }


    static class Run implements Runnable{
        static boolean running = false;
        @Override
        public synchronized void run() {
            enableAttack = false;
            //Create socket connection
            String serverResponse = null;
            Message msg = new Message();
            try{
                Log.v(TAG,"creating socket" );
                Socket socket = new Socket("192.168.1.103", 4321);
//                socket.setSoTimeout(10000);
                if (socket.isConnected()){
                    Log.v(TAG, "is connected");
                }
                if (socket.isBound()){
                    Log.v(TAG, "is bound");
                }
                if (socket.isInputShutdown()){
                    Log.v(TAG, "InputShutdown");
                }

                if (socket.isOutputShutdown()){
                    Log.v(TAG, "OutputShutdown");
                }


                PrintWriter out = new PrintWriter(socket.getOutputStream(),    true);
                BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
                Log.v(TAG,"sending data" );
                out.println("hallo there");
                try{
                    serverResponse = in.readLine();
                    Log.v(TAG,"Text received: " + serverResponse);
                    if (serverResponse != null){
                        msg.arg1 = 0;
                    }
                } catch (IOException e){
                    msg.arg1 = -1;
                    Log.v(TAG,"Read failed");
                }
            } catch (UnknownHostException e) {
                msg.arg1 = -1;
                Log.v(TAG,"Unknown host: 192.168.1.103" );
                System.out.println("Unknown host: kq6py");
            } catch  (IOException e) {
                msg.arg1 = -1;
                Log.v(TAG,"No I/O" );
            }
            if (msg.arg1 == 0){
                Bundle bundle = new Bundle();
                bundle.putString("server_response", serverResponse);
                msg.setData(bundle);
            }

            handler.sendMessage(msg);



        }
    }

}
