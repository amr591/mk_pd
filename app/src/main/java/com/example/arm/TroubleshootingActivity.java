package com.example.arm;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.arm.adapter.ExtendedSimpleAdapter;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.arm.Constants.*;

public class TroubleshootingActivity extends AppCompatActivity {

    Activity currentActivity = this;

    TextView tvParams;
    Button btnBack;
    ListView lvTroubleshooting;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    ExtendedSimpleAdapter adapter;

    JsonParser parser;
    JsonObject jsonTableOfTroubles;

    String fTableTroubles = "troubles.json";
    String station = ChooseObjectActivity.station;
    String park = ChooseObjectActivity.park;
    String switch_ = ChooseObjectActivity.switch_;
    String user = ChooseWorkerActivity.user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troubleshooting);
        setTitle(getResources().getText(R.string.title) + "(устранение неисправностей) - " + user);

        //Открытие файла
        //=====================================================================
        String temp = FileParser.loadFileFromInternalStorage(this, fTableTroubles);
        if (temp == null) {
            FileParser.saveFileToInternalStorage(this, fTableTroubles, TROUBLE_FIRST_CREATION);
            temp = FileParser.loadFileFromInternalStorage(this, fTableTroubles);
        }
        //=====================================================================

        //JSON-объекты
        //=====================================================================
        parser = new JsonParser();
        jsonTableOfTroubles = parser.parse(temp).
                getAsJsonObject();
        //=====================================================================

        //BUTTON - назад
        //=====================================================================
        btnBack = (Button)findViewById(R.id.btnBackFromTroubleshooting);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileParser.saveFileToInternalStorage(currentActivity, fTableTroubles, jsonTableOfTroubles.toString());
                currentActivity.finish();
            }
        });
        //=====================================================================

        //TEXTVIEW - строка с параметрами объекта
        //=====================================================================
        tvParams = (TextView)findViewById(R.id.tvParamsTroubleshooting);
        tvParams.setText(station + " - " + park + " - " + switch_);
        //=====================================================================

        //LISTVIEW - таблица
        //=====================================================================
        lvTroubleshooting = (ListView)findViewById(R.id.lvTroubleshooting);
        list = FileParser.getUnsolvedTroubles(jsonTableOfTroubles.getAsJsonArray("troubles"), station, park, switch_);
        adapter = new ExtendedSimpleAdapter(
                currentActivity,
                list,
                R.layout.list_item_troubleshooting,
                new String[] {ELEMENT, TROUBLE, DATE, SOLVING_DATE, RANG, SOLVING_USER},
                new int[] {R.id.tvElement, R.id.tvTrouble, R.id.tvViewDate, R.id.tvSolvingDate, R.id.tvPost, R.id.tvUser});
        lvTroubleshooting.setAdapter(adapter);

        lvTroubleshooting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> clickedMap = (HashMap<String, String>)lvTroubleshooting.getItemAtPosition(position);
                /*clickedMap.remove(SOLVING_DATE);
                clickedMap.put(SOLVING_DATE, FileParser.getDate());
                clickedMap.remove(SOLVING_USER);
                clickedMap.put(SOLVING_USER, user);*/


                JsonArray troubles = jsonTableOfTroubles.getAsJsonArray("troubles");
                JsonArray newTroubles = new JsonArray();
                for (JsonElement elem: troubles) {
                    if (elem.getAsJsonObject().get("station").getAsString().equals(station) &&
                            elem.getAsJsonObject().get("park").getAsString().equals(park) &&
                            elem.getAsJsonObject().get("switch").getAsString().equals(switch_) &&
                            elem.getAsJsonObject().get("element").getAsString().equals(clickedMap.get("element")) &&
                            elem.getAsJsonObject().get("trouble").getAsString().equals(clickedMap.get("trouble"))) {
                        JsonObject newTrouble = elem.getAsJsonObject();
                        if(elem.getAsJsonObject().get("solved").getAsBoolean()) {
                            newTrouble.addProperty("solved", "false");
                            clickedMap.remove(SOLVING_DATE);
                            clickedMap.put(SOLVING_DATE, "");
                            clickedMap.remove(SOLVING_USER);
                            clickedMap.put(SOLVING_USER, "");
                        }

                        else {
                            newTrouble.addProperty("solved", "true");
                            clickedMap.remove(SOLVING_DATE);
                            clickedMap.put(SOLVING_DATE, FileParser.getDate());
                            clickedMap.remove(SOLVING_USER);
                            clickedMap.put(SOLVING_USER, user);
                        }
                        newTroubles.add(newTrouble);
                    }

                    else newTroubles.add(elem);
                }

                jsonTableOfTroubles.add("troubles", newTroubles);

                //list = FileParser.getUnsolvedTroubles(jsonTableOfTroubles.getAsJsonArray("troubles"), station, park, switch_);
                adapter.setSelection(position);
                //FileParser.saveFileToInternalStorage(currentActivity, fTableTroubles, jsonTableOfTroubles.toString());


            }
        });
        //=====================================================================

    }
}
