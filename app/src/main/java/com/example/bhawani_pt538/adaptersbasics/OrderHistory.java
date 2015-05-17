package com.example.bhawani_pt538.adaptersbasics;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//imports for bluetooth
//imports for bluetooth

public class OrderHistory extends ActionBarActivity {
    List<String> listDataHeader;
    LinkedHashMap<String, LinkedHashMap<String,Float>> listDataChild;
    LinkedHashMap<Integer,LinkedHashMap<Integer,LinkedHashMap<Integer,Integer>>> orderGrAndChildIndex;
    LinkedHashMap<Integer,LinkedHashMap<Integer,List<String>>> indexNameMap;
    public static SQLiteDatabase sqLiteDatabase;
    //bluetooth module variables
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    //bluetooth module variable
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // Do some stuff that you want to do here

            // You could do this call if you wanted it to be periodic:
            mHandler.postDelayed(this, 5000 );

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        // bluetooth HC-05

        findBT();
        try {
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // bluetooth HC-05

        sqLiteDatabase = openOrCreateDatabase("systemdb", MODE_PRIVATE,null);
        prepareListDataFromDB();
        prepareOrderGrAndChildIndex();

//        for(Integer orderNO:orderGrAndChildIndex.keySet()) {
////            Log.d("orderNo",""+orderNO);
//            for(Integer groupIndex:orderGrAndChildIndex.get(orderNO).keySet()) {
////                Log.d("groupIndex",""+groupIndex);
//                for(Integer childIndex:orderGrAndChildIndex.get(orderNO).get(groupIndex).keySet()) {
////                    Log.d("childIndex , noOFChilds",""+childIndex+","+orderGrAndChildIndex.get(orderNO).get(groupIndex).get(childIndex));
//                }
//            }
//        }
//
        int groupIndex = 0;
        indexNameMap = new LinkedHashMap<>();
        for(String grName:listDataChild.keySet()) {
//            Log.d("groupName",grName);
            LinkedHashMap<Integer,List<String>> temHasMap = new LinkedHashMap<>();
            int childIndex = 0;
            for(String chName:listDataChild.get(grName).keySet()) {
//                Log.d("childName,price",chName+","+listDataChild.get(grName).get(chName));
                List<String> temList = new ArrayList<>();
                temList.add(chName);
                temList.add(Float.toString(listDataChild.get(grName).get(chName)));
                temHasMap.put(childIndex++,temList);
            }
            indexNameMap.put(groupIndex++,temHasMap);
        }

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        String btOrderNo = ""; //for arduino bluetooth
        List<String> lastOrderItems = new ArrayList<>();
        List<String> noOfItems = new ArrayList<>();

        for(Integer orderNO:orderGrAndChildIndex.keySet()) {
//            Log.d("orderNo",""+orderNO);
            float total = 0;
            LinearLayout temLinearLayout = new LinearLayout(this);
            temLinearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView orderNoTextView = new TextView(this);
            orderNoTextView.setBackgroundColor(Color.GRAY);
            orderNoTextView.setText("Order Number :" + orderNO);

            btOrderNo = String.valueOf(orderNO);
            lastOrderItems = new ArrayList<>();
            noOfItems = new ArrayList<>();

            temLinearLayout.addView(orderNoTextView);
            if(orderGrAndChildIndex.containsKey(orderNO))
                if(orderGrAndChildIndex.get(orderNO).keySet() != null)
            for(Integer grIndex:orderGrAndChildIndex.get(orderNO).keySet()) {
//                Log.d("groupIndex",""+groupIndex);
                if(orderGrAndChildIndex.get(orderNO).containsKey(grIndex))
                    if(orderGrAndChildIndex.get(orderNO).get(grIndex).keySet() != null)
                for(Integer childIndex:orderGrAndChildIndex.get(orderNO).get(grIndex).keySet()) {
//                    Log.d("childIndex , noOFChilds",""+childIndex+","+orderGrAndChildIndex.get(orderNO).get(groupIndex).get(childIndex));
                    if(indexNameMap.get(grIndex).containsKey(childIndex)) {
                        float itemTotal = 0;
//                        Log.d("childIndex,childName",childIndex+indexNameMap.get(grIndex).get(childIndex).get(0));

                        Cursor menuCursor = sqLiteDatabase.rawQuery("select child_name,price from MenuVerTable where group_id='"+grIndex+"' and child_id='"+childIndex+"';",null);
                        menuCursor.moveToFirst();
                        LinearLayout horLinearLayout = new LinearLayout(this);
                        horLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                        TextView chNameTextView = new TextView(this);
                        chNameTextView.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,0.1f));
                        chNameTextView.setBackgroundColor(Color.WHITE);
                        chNameTextView.setText(menuCursor.getString(0));
                        horLinearLayout.addView(chNameTextView);
                        lastOrderItems.add(menuCursor.getString(0));


                        chNameTextView = new TextView(this);
                        chNameTextView.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,0.2f));
                        chNameTextView.setBackgroundColor(Color.YELLOW);
                        chNameTextView.setText(menuCursor.getFloat(1)+"");
                        horLinearLayout.addView(chNameTextView);

                        chNameTextView = new TextView(this);
                        chNameTextView.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,0.2f));
                        chNameTextView.setBackgroundColor(Color.MAGENTA);
                        chNameTextView.setText("" + orderGrAndChildIndex.get(orderNO).get(grIndex).get(childIndex));
                        horLinearLayout.addView(chNameTextView);
                        noOfItems.add(String.valueOf(orderGrAndChildIndex.get(orderNO).get(grIndex).get(childIndex)));

                        chNameTextView = new TextView(this);
                        chNameTextView.setLayoutParams(new TableLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,0.2f));
                        chNameTextView.setBackgroundColor(Color.DKGRAY);
                        itemTotal = menuCursor.getFloat(1)*orderGrAndChildIndex.get(orderNO).get(grIndex).get(childIndex);
                        chNameTextView.setText(""+itemTotal);
                        total+=itemTotal;
                        horLinearLayout.addView(chNameTextView);

                        temLinearLayout.addView(horLinearLayout);


                    }
                }

            }
            TextView chNameTextView = new TextView(this);
            chNameTextView.setBackgroundColor(Color.RED);
            chNameTextView.setText("Total:"+total);
            temLinearLayout.addView(chNameTextView);
            linearLayout.addView(temLinearLayout);


        }

        String btDataSend = "*Order No: ";
        btDataSend += btOrderNo;
        btDataSend += "$";
        int temCount = 0;
        for(String itemName:lastOrderItems) {
            btDataSend += (temCount+1)+" "+itemName + " " + noOfItems.get(temCount)+"$";
            temCount++;
        }

        try {
            sendData(btDataSend);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        final Intent intent = new Intent(this,MainActivity.class);

        Button submitButton = (Button) findViewById(R.id.newOrderButton);
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
//                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select * from OrderHistoryTable;",null);
//                while(cursor.moveToNext()) {
//                    Log.d("orderHistoryTable",cursor.getInt(0)+","+cursor.getInt(1)+","+cursor.getInt(2)+","+cursor.getInt(3));
//                }

                startActivity(intent);
            }
        });
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

//        mHandler.postDelayed(mUpdateTimeTask, 5000);
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareListDataFromDB() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new LinkedHashMap<String, LinkedHashMap<String,Float>>();
        LinkedHashMap<String,LinkedHashMap<String,Float>> hashMap = new LinkedHashMap<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MenuVerTable",null);
        int i =1;
        while(cursor.moveToNext()) {
//            Log.d("working","version: "+cursor.getFloat(0)+",group name:"+cursor.getString(1)+",child name:"+cursor.getString(2)+cursor.getFloat(3));
            if(hashMap.containsKey(cursor.getString(1))) {
                LinkedHashMap<String,Float> arrayList = (LinkedHashMap)hashMap.get(cursor.getString(1));
                arrayList.put(cursor.getString(2), cursor.getFloat(3));
            } else {
                LinkedHashMap<String,Float> arrayList = new LinkedHashMap<>();
                arrayList.put(cursor.getString(2), cursor.getFloat(3));
                hashMap.put(cursor.getString(1),arrayList);
            }

        }
//        Iterator<String> iterator = (Iterator<String>) hashMap.keySet();
//        while (iterator.hasNext()) {
//            Log.d("hasMap keys",iterator.toString());
//            iterator.next();
//        }
//        Log.d("size", "" +hashMap.size());
        for(String key: hashMap.keySet()) {
//            Log.d("group name", "" +key);
            listDataHeader.add(key);
            listDataChild.put(key,hashMap.get(key));
//            for(String value: hashMap.get(key)) {
//                Log.d("child name",value);
//            }
        }
        for(String key:listDataChild.keySet()) {
            Log.d("group",key);
            for(String value:listDataChild.get(key).keySet()) {
                Log.d("child-",value);
            }
        }
    }

    void prepareOrderGrAndChildIndex() {
        orderGrAndChildIndex = new LinkedHashMap<>();
        Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select * from OrderHistoryTable;",null);
        while(cursor.moveToNext()) {
//            Log.d("orderHistoryTable", cursor.getInt(0) + "," + cursor.getInt(1) + "," + cursor.getInt(2) + "," + cursor.getInt(3));
                if(orderGrAndChildIndex.containsKey(cursor.getInt(0))) {
                    if(orderGrAndChildIndex.get(cursor.getInt(0)).containsKey(cursor.getInt(1))) {
                        LinkedHashMap<Integer,Integer> temList = orderGrAndChildIndex.get(cursor.getInt(0)).get(cursor.getInt(1));
                        temList.put(cursor.getInt(2),cursor.getInt(3));

                    } else {
                        LinkedHashMap<Integer,Integer> temList = new LinkedHashMap<>();
                        temList.put(cursor.getInt(2),cursor.getInt(3));
                        LinkedHashMap<Integer,LinkedHashMap<Integer,Integer>> temLinkedHashMap = orderGrAndChildIndex.get(cursor.getInt(0));
                        temLinkedHashMap.put(cursor.getInt(1),temList);
                    }
                }   else {
                    LinkedHashMap<Integer,Integer> temList = new LinkedHashMap<>();
                    temList.put(cursor.getInt(2), cursor.getInt(3));
                    LinkedHashMap<Integer,LinkedHashMap<Integer,Integer>> temLinkedHashMap = new LinkedHashMap<>();
                    temLinkedHashMap.put(cursor.getInt(1),temList);
                    orderGrAndChildIndex.put(cursor.getInt(0),temLinkedHashMap);

                }

        }
    }


    //bluetooth connection

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
//            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-05"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
//        myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

//        beginListenForData();

//        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
//                                            myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msg) throws IOException
    {
//        String msg = "this is right";//myTextbox.getText().toString();
//        msg += "\n";
        mmOutputStream.write(msg.getBytes());
//        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
//        myLabel.setText("Bluetooth Closed");
    }
}
