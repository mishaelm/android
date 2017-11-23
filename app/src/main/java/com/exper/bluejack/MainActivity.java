package com.exper.bluejack;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "mmmMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        new Thread(new run()).start();

    }

    public void resetBtnClicked(View v){

    }

    class run implements Runnable{
        @Override
        public void run() {
            //Create socket connection
            try{
                Log.v(TAG,"creating socket" );
                Socket socket = new Socket("192.168.1.103", 4321);

                PrintWriter out = new PrintWriter(socket.getOutputStream(),    true);
                BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
                Log.v(TAG,"sending data" );
                out.println("hey there");






                try{
                    String line = in.readLine();
                    Log.v(TAG,"Text received: " + line);
                } catch (IOException e){
                    Log.v(TAG,"Read failed");
                    System.exit(1);
                }
            } catch (UnknownHostException e) {
                Log.v(TAG,"Unknown host: 192.168.1.103" );
                System.out.println("Unknown host: kq6py");
                System.exit(1);
            } catch  (IOException e) {
                Log.v(TAG,"No I/O" );
                System.exit(1);
            }
        }
    }

}
