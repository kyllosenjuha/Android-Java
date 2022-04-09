package juha.redmi.bluetoothremote;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

        static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        Button onButton1, onButton2, onButton3, onButton4;
        Button offButton1, offButton2, offButton3, offButton4;
        Button onBluetoothButton, offBluetoothButton;

        @SuppressLint("StaticFieldLeak")
        static TextView textView1;
        @SuppressLint("StaticFieldLeak")
        static TextView textView2;
        @SuppressLint("StaticFieldLeak")
        static TextView textView3;
        @SuppressLint("StaticFieldLeak")
        static TextView textView4;
        @SuppressLint("StaticFieldLeak")
        static TextView bluetoothStatusTextView;

        static byte[] relStatus = new byte[]{'e', 'f', 'g', 'h'};

        Thread thread1;
        Thread thread2;
        Thread thread3;
        Thread thread4;
        Thread thread5;
        Thread thread6;
        Thread thread7;
        Thread thread8;
        Thread thread9;
        Thread thread10;

        static BluetoothSocket btSocket = null;
        static boolean bluetoothConnected = false;

        final static Lock lock = new ReentrantLock();

        public class BluetoothReceiver extends BroadcastReceiver
        {

            BluetoothReceiver() {

            }

            @Override
            public void onReceive(Context context, Intent intent) {

                System.out.println("Bluetooth Intent received.");

                String action = intent.getAction();
                System.out.println("Bluetooth called: Action:" + action);

                if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
                {
                    System.out.println("BLUETOOTH CONNECTED RECEIVED.");

                    lock.lock();
                    bluetoothConnected = true;
                    bluetoothState();
                    lock.unlock();
                }

                if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
                {
                    System.out.println("BLUETOOTH DISCONNECTED RECEIVED.");

                    lock.lock();
                    bluetoothConnected = false;
                    bluetoothState();
                    lock.unlock();
                }
            }
        }

        @SuppressLint("SetTextI18n")
        public static void bluetoothState() {
            lock.lock();
            GradientDrawable gradientDrawable = (GradientDrawable) bluetoothStatusTextView.getBackground().mutate();

            if (bluetoothConnected) {
                gradientDrawable.setColor(Color.GREEN);
                bluetoothStatusTextView.setText("CONNECTED");
            } else {
                gradientDrawable.setColor(Color.RED);
                bluetoothStatusTextView.setText("CLOSED");
            }
            lock.unlock();
        }

        @SuppressLint("SetTextI18n")
        public static void printRelState() {
            lock.lock();
            GradientDrawable gradientDrawable1 = (GradientDrawable) textView1.getBackground().mutate();
            GradientDrawable gradientDrawable2 = (GradientDrawable) textView2.getBackground().mutate();
            GradientDrawable gradientDrawable3 = (GradientDrawable) textView3.getBackground().mutate();
            GradientDrawable gradientDrawable4 = (GradientDrawable) textView4.getBackground().mutate();

            if (relStatus[0] == 'a') {
                gradientDrawable1.setColor(Color.GREEN);
                textView1.setText("CH1 ON");
            } else if (relStatus[0] == 'e') {
                gradientDrawable1.setColor(Color.RED);
                textView1.setText("CH1 OFF");
            }

            if (relStatus[1] == 'b') {
                gradientDrawable2.setColor(Color.GREEN);
                textView2.setText("CH2 ON");
            } else if (relStatus[1] == 'f') {
                gradientDrawable2.setColor(Color.RED);
                textView2.setText("CH2 OFF");
            }

            if (relStatus[2] == 'c') {
                gradientDrawable3.setColor(Color.GREEN);
                textView3.setText("CH3 ON");
            } else if (relStatus[2] == 'g') {
                gradientDrawable3.setColor(Color.RED);
                textView3.setText("CH3 OFF");
            }

            if (relStatus[3] == 'd') {
                gradientDrawable4.setColor(Color.GREEN);
                textView4.setText("CH4 ON");
            } else if (relStatus[3] == 'h') {
                gradientDrawable4.setColor(Color.RED);
                textView4.setText("CH4 OFF");
            }
            lock.unlock();

        }


        public static byte[] sendASCIICommand(int commandASCII, BluetoothSocket myBtSocket) {

            InputStream myInputStream;
            OutputStream myOutputStream;

            byte[] myRelStatus = {'e', 'f', 'g', 'h'};

            try {
                myOutputStream = myBtSocket.getOutputStream();
                myInputStream = myBtSocket.getInputStream();
                myOutputStream.write(commandASCII);

                for (int i = 0; i < 4; i++) {
                    myRelStatus[i] = (byte) myInputStream.read();
                    System.out.println((char) myRelStatus[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return myRelStatus;
        }

        public void bluetoothDisabled() {
            Context context = getApplicationContext();
            CharSequence myBluetoothDisabled = "Enable Bluetooth first!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, myBluetoothDisabled, duration);
            toast.show();
        }

        public void pressBluetoothConnect() {
            Context context = getApplicationContext();
            CharSequence myPressBluetoothConnect = "Press Bluetooth Connect first!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, myPressBluetoothConnect, duration);
            toast.show();
        }

        public void bluetoothAllreadyClosed() {
            Context context = getApplicationContext();
            CharSequence myBluetoothAllreadyDisconnected = "Bluetooth allready closed!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, myBluetoothAllreadyDisconnected, duration);
            toast.show();
        }

        public void bluetoothAllreadyConnected() {
            Context context = getApplicationContext();
            CharSequence myBluetoothAllreadyConnected = "Bluetooth allready connected!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, myBluetoothAllreadyConnected, duration);
            toast.show();
        }

        public void bluetoothConnected(boolean connected) {

            Context context = getApplicationContext();
            CharSequence myBluetoothConnected;
            if (connected)
                myBluetoothConnected = "Bluetooth connected!";
            else
                myBluetoothConnected = "Bluetooth closed!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, myBluetoothConnected, duration);
            toast.show();
        }

        @SuppressLint("MissingPermission")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            onButton1 = findViewById(R.id.onButton1);
            onButton2 = findViewById(R.id.onButton2);
            onButton3 = findViewById(R.id.onButton3);
            onButton4 = findViewById(R.id.onButton4);

            onBluetoothButton = findViewById(R.id.onBluetoothButton);
            offBluetoothButton = findViewById(R.id.offBluetoothButton);

            offButton1 = findViewById(R.id.offButton1);
            offButton2 = findViewById(R.id.offButton2);
            offButton3 = findViewById(R.id.offButton3);
            offButton4 = findViewById(R.id.offButton4);

            textView1 = findViewById(R.id.textView1);
            textView2 = findViewById(R.id.textView2);
            textView3 = findViewById(R.id.textView3);
            textView4 = findViewById(R.id.textView4);
            bluetoothStatusTextView = findViewById(R.id.bluetoothStatusTextView);

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);

            BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
            registerReceiver(bluetoothReceiver, filter);

            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            System.out.println(btAdapter.getBondedDevices());

            BluetoothDevice hc06 = btAdapter.getRemoteDevice("93:F3:E9:EC:B6:01");
            System.out.println(hc06.getName());

            thread1 = null;
            thread2 = null;
            thread3 = null;
            thread4 = null;
            thread5 = null;
            thread6 = null;
            thread7 = null;
            thread8 = null;
            thread9 = null;
            thread10 = null;

            onBluetoothButton.setOnClickListener(view -> {

                if (btAdapter.isEnabled()) {

                    if (!bluetoothConnected) {

                        thread9 = new Thread(() -> {

                            try {
                                btSocket = hc06.createRfcommSocketToServiceRecord(mUUID);
                                System.out.println(btSocket);
                                btSocket.connect();
                                System.out.println(btSocket.isConnected());

                                Looper.prepare();

                                lock.lock();
                                bluetoothConnected(true);
                                relStatus = sendASCIICommand(122, btSocket);
                                printRelState();
                                lock.unlock();

                            } catch (IOException e) {
                                e.printStackTrace();
                                lock.lock();
                                System.out.println("Bluetooth connection did not succeed.");
                                lock.unlock();
                            }

                        });

                        thread9.start();

                    } else {
                        bluetoothAllreadyConnected();
                        System.out.println("Bluetooth is allready connected.");
                    }
                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");
                }
            });

            offBluetoothButton.setOnClickListener(view -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread10 = new Thread(() -> {

                            try {

                                btSocket.close();
                                System.out.println(btSocket.isConnected());

                                Looper.prepare();

                                lock.lock();
                                bluetoothConnected(false);
                                lock.unlock();

                            } catch (IOException e) {

                                e.printStackTrace();

                            }


                        });

                        thread10.start();

                    } else {
                        bluetoothAllreadyClosed();
                        System.out.println("Bluetooth is allready closed.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");
                }

            });

            onButton1.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread1 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(97, btSocket);
                            lock.unlock();
                            printRelState();

                        });
                        thread1.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH1 - ON, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");
                }

            });

            onButton2.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread2 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(98, btSocket);
                            lock.unlock();

                            printRelState();

                        });

                        thread2.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH2 - ON, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            onButton3.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread3 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(99, btSocket);
                            lock.unlock();

                            printRelState();

                        });
                        thread3.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH3 - ON, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            onButton4.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread4 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(100, btSocket);
                            lock.unlock();

                            printRelState();

                        });
                        thread4.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH4 - ON, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            offButton1.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread5 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(101, btSocket);
                            lock.unlock();

                            printRelState();

                        });

                        thread5.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH1 - OFF, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            offButton2.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread6 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(102, btSocket);
                            lock.unlock();

                            printRelState();

                        });

                        thread6.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH2 - OFF, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            offButton3.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread7 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(103, btSocket);
                            lock.unlock();

                            printRelState();

                        });

                        thread7.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH3 - OFF, No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");

                }
            });

            offButton4.setOnClickListener(v -> {

                if (btAdapter.isEnabled()) {

                    if (bluetoothConnected) {

                        thread8 = new Thread(() -> {

                            lock.lock();
                            relStatus = sendASCIICommand(104, btSocket);
                            lock.unlock();

                            printRelState();

                        });

                        thread8.start();

                    } else {
                        pressBluetoothConnect();
                        System.out.println("CH4 - OFF , No Bluetooth connection.");
                    }

                } else {
                    bluetoothDisabled();
                    System.out.println("Phone's Bluetooth is disabled, You have to enable it.");
                }
            });
        }
}
