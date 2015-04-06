package com.example.bhawani_pt538.adaptersbasics;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    public static SQLiteDatabase sqLiteDatabase;
    int orderNo = 0;
//    ArrayList<List<Integer>> indexContainer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        HashMap<Integer,List<HashMap<Integer,List<Integer>>>> phaadHashMap = new HashMap<>();


        //open or create database
        sqLiteDatabase = openOrCreateDatabase("systemdb", MODE_PRIVATE,null);

        //create table and initialize table
        createAndInitTable();


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
//        prepareListData();
        prepareListDataFromDB();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        final Intent intent = new Intent(this,OrderHistory.class);


        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
//                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select * from OrderHistoryTable;",null);
//                while(cursor.moveToNext()) {
//                    Log.d("orderHistoryTable",cursor.getInt(0)+","+cursor.getInt(1)+","+cursor.getInt(2)+","+cursor.getInt(3));
//                }
                startActivity(intent);
            }
        });

//        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                // TODO Auto-generated method stub
//                Toast.makeText(
//                        getApplicationContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });
    }
//    public  void updateIndexContainer(int groupIndex,int childIndex,int noChilds) {
//
//
//    }
    private  void createAndInitTable() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT tbl_name from sqlite_master where tbl_name = 'MenuVerTable'", null);
        if(false) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS MenuVerTable( version FLOAT, group_name VARCHAR, child_name VARCHAR, price FLOAT);");

            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','MILK SHAKES','CAFE MOCHA','145');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','MILK SHAKES','NUTTY AFFAIR','145');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','MILK SHAKES','OREO O OREO','165');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','MILK SHAKES','CHOCOLATE BANANA SHAKE','145');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','MILK SHAKES','MADRAS MILKSHAKE','145');");

            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','HOT BEVERAGES','CUTTING EDGE CHAI','75');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','HOT BEVERAGES','FILTER KAAPI','75');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','HOT BEVERAGES','DIVINE HOT CHOCOLATE','145');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','HOT BEVERAGES','HAND BEATEN COFFEE','85');");

            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','SALADS','THE TOADSTOOL SALAD','159');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','SALADS','ROASTED EGGPLANT SALAD','159');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','SALADS','CHICKEN CAME FIRST','179');");
            sqLiteDatabase.execSQL("INSERT INTO MenuVerTable VALUES('0.0','SALADS','GRACIAS','179');");

        }
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS OrderHistoryTable(order_no INT, group_index INT, child_index INT, no_of_childs INT);");

    }
    private void prepareListDataFromDB() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        HashMap<String,List<String>> hashMap = new HashMap<>();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MenuVerTable",null);
        int i =1;
        while(cursor.moveToNext()) {
//            Log.d("working","version: "+cursor.getFloat(0)+",group name:"+cursor.getString(1)+",child name:"+cursor.getString(2)+cursor.getFloat(3));
            if(hashMap.containsKey(cursor.getString(1))) {
                ArrayList<String> arrayList = (ArrayList)hashMap.get(cursor.getString(1));
                arrayList.add(cursor.getString(2));
            } else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(cursor.getString(2));
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
    }

    /*
     * Preparing the list data
     */
//    private void prepareListData() {
//        listDataHeader = new ArrayList<String>();
//        listDataChild = new HashMap<String, List<String>>();
//
//        // Adding child data
//        listDataHeader.add("Top 250");
//        listDataHeader.add("Now Showing");
//        listDataHeader.add("Coming Soon..");
//
//
//
//        // Adding child data
//        List<String> top250 = new ArrayList<String>();
//        top250.add("The Shawshank Redemption");
//        top250.add("The Godfather");
//        top250.add("The Godfather: Part II");
//        top250.add("Pulp Fiction");
//        top250.add("The Good, the Bad and the Ugly");
//        top250.add("The Dark Knight");
//        top250.add("12 Angry Men");
//
//        List<String> nowShowing = new ArrayList<String>();
//        nowShowing.add("The Conjuring");
//        nowShowing.add("Despicable Me 2");
//        nowShowing.add("Turbo");
//        nowShowing.add("Grown Ups 2");
//        nowShowing.add("Red 2");
//        nowShowing.add("The Wolverine");
//
//        List<String> comingSoon = new ArrayList<String>();
//        comingSoon.add("2 Guns");
//        comingSoon.add("The Smurfs 2");
//        comingSoon.add("The Spectacular Now");
//        comingSoon.add("The Canyons");
//        comingSoon.add("Europa Report");
//
//        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
//        listDataChild.put(listDataHeader.get(1), nowShowing);
//        listDataChild.put(listDataHeader.get(2), comingSoon);
//    }
}

class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    static public int orderNo = 0;



    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select MAX(order_no) from OrderHistoryTable;",null);
        cursor.moveToFirst();
        if(cursor.getCount() !=0) {

            orderNo = cursor.getInt(0);
            orderNo++;
            Log.d("no of rows","" +orderNo);
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        final View cView = convertView;

        // initializing itemTextView

        TextView textView = (TextView) convertView.findViewById(R.id.itemTextView);
        String childName = (String) getChild(groupPosition,childPosition);
        textView.setText(childName);


        Button plusButton = (Button) convertView.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
//                Toast.makeText(cView.getContext(), "plus" + " group-position: "+ groupPosition + "child-position: "+ childPosition, Toast.LENGTH_LONG).show();
                TextView textView =(TextView) cView.findViewById(R.id.countTextView);
                int currentValue = Integer.parseInt((String)textView.getText());
                currentValue++;
                textView.setText(Integer.toString(currentValue));

                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select * from OrderHistoryTable where order_no='"+orderNo+"'and group_index='"+groupPosition+"' and child_index='"+childPosition+"';",null);
                cursor.moveToFirst();
                if(cursor.getCount() !=0) {
                    MainActivity.sqLiteDatabase.execSQL("UPDATE OrderHistoryTable SET no_of_childs ='" + currentValue + "' where order_no='" + orderNo + "'and group_index='" + groupPosition + "' and child_index='" + childPosition + "';");
                } else {
                    MainActivity.sqLiteDatabase.execSQL("INSERT INTO OrderHistoryTable VALUES('"+orderNo+"','"+groupPosition+"','"+childPosition+"','"+currentValue+"');");
                }

            }


        });

        Button minusButton = (Button) convertView.findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
//                Toast.makeText(cView.getContext(), "plus" + " group-position: "+ groupPosition + "child-position: "+ childPosition, Toast.LENGTH_LONG).show();
                TextView textView =(TextView) cView.findViewById(R.id.countTextView);
                int currentValue = Integer.parseInt((String)textView.getText());
                currentValue--;
                if(currentValue >= 0) {
                    textView.setText(Integer.toString(currentValue));
                }

                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("select * from OrderHistoryTable where order_no='"+orderNo+"'and group_index='"+groupPosition+"' and child_index='"+childPosition+"';",null);
                cursor.moveToFirst();
                if(cursor.getCount() !=0) {
                    MainActivity.sqLiteDatabase.execSQL("UPDATE OrderHistoryTable SET no_of_childs ='"+currentValue+"' where order_no='"+orderNo+"'and group_index='"+groupPosition+"' and child_index='"+childPosition+"';");
                } else {
                    MainActivity.sqLiteDatabase.execSQL("INSERT INTO OrderHistoryTable VALUES('"+orderNo+"','"+groupPosition+"','"+childPosition+"','"+currentValue+"');");
                }

            }
        });


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

