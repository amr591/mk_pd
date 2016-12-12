package com.example.arm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.arm.data.RailwayData;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;


public class ChooseWorkerActivity extends AppCompatActivity {

    Intent iGoToMenu;
    ListView lvWorkers;

    JsonParser parser;
    JsonObject mainObject;

    ArrayList<String> names;
    public static String user;
    public static String session;
    public static RailwayData userData;

    public static String[][] loadedData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_worker);


        //JSON-Объекты
        //===================================================================
        parser = new JsonParser();
        mainObject = parser.parse(FileParser.loadFileFromAsset(this, "data_workers.json")).
                getAsJsonObject();
        names = new ArrayList<>();
        names = FileParser.getNames(mainObject);
        //====================================================================

        //LISTVIEW - Список работников
        //=====================================================================
        lvWorkers = (ListView)findViewById(R.id.lvWorkers);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_workers, names);
        lvWorkers.setAdapter(adapter);
        lvWorkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iGoToMenu = new Intent(ChooseWorkerActivity.this, MenuActivity.class);
                user = lvWorkers.getItemAtPosition(position).toString();
                session = String.valueOf(Calendar.getInstance().getTimeInMillis());
                startActivity(iGoToMenu);
                //startActivity(new Intent(ChooseWorkerActivity.this, OutputActivity.class));

            }
        });
        //====================================================================
    }
}
