package com.exper.bluejack;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mmmMainActivity";
    private static boolean enableAttack = false;

    public static String IP = "192.168.1.102";
    public static int PORT = 4321;

    private static Handler handler = null;

    private ListView listView;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mDeviceList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lisView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(Color.parseColor("#3F7AE0"));
                enableAttack = true;
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);



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
                }else{
                    Toast.makeText(getApplicationContext(), "An Error accuired, check your connection and try again", Toast.LENGTH_LONG).show();
                }
                unSelectListOptions();
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onstart");




        if (ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)){

            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH}, 4251);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR}, 4251);
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_CALENDAR}, 4251);




    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4251){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Button button =  findViewById(R.id.scan_btn);
                button.setEnabled(true);
            }
        }
    }

    public void scanBtnClicked(View v){
        if (!mBluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Enable bluetooth before this operation", Toast.LENGTH_LONG).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    Log.v(TAG, "scanBtnClicked -->pemission denied");
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>To find nearby bluetooth devices please click \"Allow\" on the runtime permissions popup.</p>" +
                                    "<p>For more info see <a href=\"http://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id\">here</a>.</p>"))
                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }




        mBluetoothAdapter.startDiscovery();
        ListView list = findViewById(R.id.lisView);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mDeviceList = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices){
            mDeviceList.add(bt.getName());
        }
        list.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_layout, mDeviceList));
    }

    private void unSelectListOptions(){
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_layout, mDeviceList));
    }


    private void updateList(){
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_layout, mDeviceList));

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
        TextView textView = findViewById(R.id.server_response_text);
        textView.setText("");
        IP = "192.168.1.102";
        PORT = 4321;
        mDeviceList = new ArrayList<>();
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_layout, mDeviceList));
    }


    public void settingsBtnClicked(View v){
        SettingsDialog dialog = new SettingsDialog(this);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        dialog.getWindow().setLayout(width - (width/4), height - (height / 2));

        dialog.show();

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
                Socket socket = new Socket(IP, PORT);
                socket.setSoTimeout(5000);
                Log.v(TAG,"timeout was set" );
//                socket.setSoT  imeout(10000);
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

                Log.v(TAG,"sending data" );
                out.println("hallo there bla bla\r\n");
                BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
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




    @Override
    protected void onDestroy() {

//        if (mReceiver != null){
//            unregisterReceiver(mReceiver);
//        }
        super.onDestroy();
    }




    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.v(TAG, "starting discovery");
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.v(TAG, "finished discovery");
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName());

                //bluetooth device found
                Log.v(TAG, "found bluetooth device");
                Log.v(TAG, "found bluetooth device");
                Log.v(TAG, "found bluetooth device");
                Log.v(TAG, "found bluetooth device");
                updateList();

            }
        }
    };
}
