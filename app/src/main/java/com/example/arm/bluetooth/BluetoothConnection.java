package com.example.arm.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.arm.ChooseObjectActivity;
import com.example.arm.DeviceListActivity;
import com.example.arm.MeasureActivity;
import com.example.arm.data.DeviceData;

import java.util.HashMap;

import com.example.arm.parser.CommandParser;

import static com.example.arm.Constants.*;
import static com.example.arm.MeasureActivity.globalLength;
import static com.example.arm.MeasureActivity.ind;
import static com.example.arm.MeasureActivity.last1Button;
import static com.example.arm.MeasureActivity.last2Button;
import static com.example.arm.MeasureActivity.last3Button;
import static com.example.arm.MeasureActivity.lastButton;
import static com.example.arm.MeasureActivity.now1Button;
import static com.example.arm.MeasureActivity.now2Button;
import static com.example.arm.MeasureActivity.now3Button;
import static com.example.arm.MeasureActivity.nowButton;
import static com.example.arm.R.id.tableScroll;

public class BluetoothConnection {

    private static Activity activity;

    private static BluetoothService connector;
    private  BluetoothResponseHandler handler;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;


    public BluetoothConnection(Activity activity) {
        this.activity = activity;

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (handler == null) handler = new BluetoothResponseHandler(this.activity);

        if (!isAdapterReady()) {
            enableBluetooth();
        }
        else {
            Intent serverIntent = new Intent(activity, DeviceListActivity.class);
            activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }

    private void enableBluetooth() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        };

        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        });

        backgroundThread.start();
    }



    public void startConnection(String address) {
        device = btAdapter.getRemoteDevice(address);
        DeviceData data = new DeviceData(device, "emptyName");
        stopConnection();
        try {
            connector = new BluetoothService(data, handler);
            connector.connect();
        } catch (Exception e) {
            //Toast.makeText(activity, "CONNECTION FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    static void sendMessage(byte[] message) {
        try {
            if (isConnected())
                connector.write(message);
            else Toast.makeText(activity, "OUT FAILED", Toast.LENGTH_SHORT);
        }
        catch (Exception e) {
            //Toast.makeText(activity, "send failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
        }
    }

    private static boolean isConnected() {
        return (connector != null) && (connector.getState() == STATE_CONNECTED);
    }

    private boolean isAdapterReady() {
        return (btAdapter != null) && (btAdapter.isEnabled());
    }

   public static void insertDataFromBluetooth(String template, String level){
       Button lastBut1 = (Button) activity.findViewById( lastButton);
       Button lastBut2 = (Button) activity.findViewById( last1Button);
       Button lastBut3 = (Button) activity.findViewById( last2Button);
       Button lastBut4 = (Button) activity.findViewById( last3Button);

       lastBut1.setBackgroundColor(Color.rgb(203, 205, 209));
       lastBut2.setBackgroundColor(Color.rgb(203, 205, 209));
       lastBut3.setBackgroundColor(Color.rgb(203, 205, 209));
       lastBut4.setBackgroundColor(Color.rgb(203, 205, 209));

       Button but1 = (Button) activity.findViewById( nowButton);
       Button but2 = (Button) activity.findViewById( now1Button);
       Button templateBut = (Button) activity.findViewById( now2Button);
       Button levelBut = (Button) activity.findViewById( now3Button);
       but1.setBackgroundColor(Color.WHITE);
       but2.setBackgroundColor(Color.WHITE);
       templateBut.setBackgroundColor(Color.WHITE);
       levelBut.setBackgroundColor(Color.WHITE);
       templateBut.setText(template);
       levelBut.setText(level);

       //Button btn = (Button) findViewById(now2Button+1);
       //btn.callOnClick();
       final ScrollView scrTabl = (ScrollView)activity.findViewById(tableScroll) ;
       lastButton=nowButton;
       last1Button=now1Button;
       last2Button=now2Button;
       last3Button=now3Button;
       if (ind==0) {

           if (nowButton + 1 <= globalLength) {
               nowButton++;
               now1Button++;
               now2Button++;
               now3Button++;
           }

       }

       if(ind==1) {
           //длина массива, посмотри как у тебя она там азывается, может по другому
           if (nowButton + 1 <= globalLength) {
               nowButton++;
               now1Button++;
               now2Button++;
               now3Button++;
           }
       }
       if(ind==2) {
           //длина массива, посмотри как у тебя она там азывается, может по другому
           if (nowButton -1 >= 0) {
               nowButton--;
               now1Button--;
               now2Button--;
               now3Button--;
           }
       }
       scrTabl.scrollTo(0, 115*(nowButton-2));
       Button btn = (Button)activity.findViewById(nowButton);
       MeasureActivity.elementNumber = btn.getText().toString();

   }

    private static void startConversationWithBluetoothDevice() {
        String command = "110";
        String comment = null;
        HashMap<String, String> params = new HashMap<>();

        params.put("40", ChooseObjectActivity.dataStation.getId());
        params.put("45", ChooseObjectActivity.dataStation.getShortName());
        params.put("50", ChooseObjectActivity.dataPark.getId());
        params.put("55", ChooseObjectActivity.dataPark.getShortName());
        params.put("60", ChooseObjectActivity.dataSwitch.getId());
        params.put("65", ChooseObjectActivity.dataSwitch.getShortName());
        params.put("70", ChooseObjectActivity.type);
        params.put("80", MeasureActivity.elementNumber);

        sendMessage(new CommandParser(command, comment, params).setCommand().getBytes());

        //Toast.makeText(activity, new CommandParser(command, comment, params).setCommand(), Toast.LENGTH_LONG).show();
    }

    private static void actionOnReceivedMessage(String msg) {
        CommandParser parser = new CommandParser(msg);
        try {

            if (parser.getCommand().equals("120")) {
                HashMap<String, String> params = parser.getParams();

                String template = params.get("128");
                String level = params.get("129");
                insertDataFromBluetooth(template, level);

                sendMessage(new CommandParser("130", parser.getComment(), parser.getParams()).setCommand().getBytes());
                //Toast.makeText(activity, new CommandParser("130", parser.getComment(), parser.getParams()).setCommand(), Toast.LENGTH_LONG).show();
                startConversationWithBluetoothDevice();
            }

            else if (parser.getCommand().equals("110")) {
                //Toast.makeText(activity, parser.getCommand(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            //Toast.makeText(activity, "GET: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private static class BluetoothResponseHandler extends Handler {
        private Activity mActivity;

        BluetoothResponseHandler(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case STATE_CONNECTED:
                                mActivity.setTitle("ПОДКЛЮЧЕНО");
                                startConversationWithBluetoothDevice();
                                break;
                            case STATE_CONNECTING:
                                mActivity.setTitle("ПОДКЛЮЧЕНИЕ...");
                                break;
                            case STATE_NONE:
                                mActivity.setTitle("НЕ ПОДКЛЮЧЕНО");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        final String readMessage = (String) msg.obj;
                        if (readMessage != null) {
                            //Toast.makeText(mActivity, readMessage, Toast.LENGTH_LONG).show();
                            actionOnReceivedMessage(readMessage);
                            //actionOnReceivedMessage("120%65#78/80$80#1$128#1520$129#0$");

                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        break;

                    case MESSAGE_WRITE:
                        // stub
                        break;

                    case MESSAGE_TOAST:
                        //Toast.makeText(mActivity, msg.getData().getString(TOAST),
                               //Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
}
