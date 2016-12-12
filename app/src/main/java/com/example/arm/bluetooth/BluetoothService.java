/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.arm.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.arm.data.DeviceData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.arm.Constants.*;


public class BluetoothService {

    private int mState;

    private final BluetoothAdapter btAdapter;
    private final BluetoothDevice connectedDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private final String deviceName;
    // ==========================================================================


    public BluetoothService(DeviceData deviceData, Handler handler) {
        mHandler = handler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedDevice = btAdapter.getRemoteDevice(deviceData.getAddress());
        deviceName = (deviceData.getName() == null) ? deviceData.getAddress() : deviceData.getName();
        mState = STATE_NONE;
    }
    // ==========================================================================


    private void toast(String string) {
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, string);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Запрос на соединение с устойством
     */
    public synchronized void connect() {
        //toast("connect to: " + connectedDevice);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                //toast("cancel mConnectThread");
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            //toast("cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(connectedDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    // ==========================================================================

    /**
     * Завершение соединения
     */
    public synchronized void stop() {
        //toast("stop");

        if (mConnectThread != null) {
            //toast("cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            //toast("cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }
    // ==========================================================================


    /**
     * Установка внутреннего состояния устройства
     *
     * @param state - состояние
     */
    private synchronized void setState(int state) {
        //toast("setState() " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
    // ==========================================================================


    /**
     * Получение состояния устройства
     */
    public synchronized int getState() {
        return mState;
    }
    // ==========================================================================


    public synchronized void connected(BluetoothSocket socket) {
        toast("connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            //toast("cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            //toast("cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_CONNECTED);

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME, deviceName);
        mHandler.sendMessage(msg);

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }
    // ==========================================================================

    public void write(byte[] data) {
        /*synchronized (this) {
            if (mState == STATE_CONNECTED) mConnectedThread.writeData(data);
        }*/
        if (mConnectedThread != null)
            mConnectedThread.writeData(data);
        else ;//toast("connectedThread fault");
    }
    // ==========================================================================


    private void connectionFailed() {
        //toast("connectionFailed");

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_NONE);
    }
    // ==========================================================================


    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_NONE);
    }
    // ==========================================================================


    /**
     * Класс потока для соединения с BT-устройством
     */
    // ==========================================================================
    private class ConnectThread extends Thread {
        private static final String TAG = "ConnectThread";
        private static final boolean D = false;

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            //toast("create ConnectThread");
            mmDevice = device;
            mmSocket = BluetoothUtils.createRfcommSocket(mmDevice);
        }
        // ==========================================================================

        /**
         * Основной рабочий метод для соединения с устройством.
         * При успешном соединении передаёт управление другому потоку
         */
        public void run() {
            //toast("ConnectThread run");
            btAdapter.cancelDiscovery();
            if (mmSocket == null) {
                //toast("unable to connect to device, socket isn't created");
                connectionFailed();
                return;
            }

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                //toast("cannot connect socket");
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    //toast("unable to close() socket during connection failure");
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }
        // ==========================================================================


        /**
         * Отмена соединения
         */
        public void cancel() {
            //toast("ConnectThread cancel");

            if (mmSocket == null) {
                //toast("unable to close null socket");
                return;
            }
            try {
                mmSocket.close();
            } catch (IOException e) {
                //toast("close() of connect socket failed");
            }
        }
        // ==========================================================================
    }
    // ==========================================================================


    /**
     * Класс потока для обмена данными с BT-устройством
     */
    // ==========================================================================
    private class ConnectedThread extends Thread {
        private static final String TAG = "ConnectedThread";
        private static final boolean D = false;

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //toast("create ConnectedThread");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //toast("temp sockets not created");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        // ==========================================================================

        /**
         * Основной рабочий метод - ждёт входящих команд от потока
         */
        public void run() {
            //toast("ConnectedThread run");
            byte[] buffer = new byte[512];
            int bytes;
            StringBuilder readMessage = new StringBuilder();
            while (true) {
                try {
                    // считываю входящие данные из потока и собираю в строку ответа
                    bytes = mmInStream.read(buffer);
                    String readed = new String(buffer, 0, bytes);
                    readMessage.append(readed);

                    // маркер конца команды - вернуть ответ в главный поток
                    if (readed.contains("\n")) {
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, readMessage.toString()).sendToTarget();
                        toast("IN: " + readMessage.toString());
                        readMessage.setLength(0);
                    }

                } catch (IOException e) {
                    //toast("disconnected" );
                    connectionLost();
                    break;
                }
            }
        }
        // ==========================================================================


        /**
         * Записать кусок данных в устройство
         */
        public void writeData(byte[] chunk) {

            try {
                //toast("OUT: "+chunk.toString());
                mmOutStream.write(chunk);
                mmOutStream.flush();
                //Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, chunk.toString()).sendToTarget();
            } catch (Exception e) {
                //toast("Exception during write" );
            }
        }
        // ==========================================================================


        /**
         * Записать байт
         */
        public void write(byte command) {
            byte[] buffer = new byte[1];
            buffer[0] = command;

            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                //toast("Exception during write" );
            }
        }
        // ==========================================================================


        /**
         * Отмена - закрытие сокета
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //toast("close() of connect socket failed" );
            }
        }
        // ==========================================================================
    }
    // ==========================================================================
}