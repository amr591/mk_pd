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
import android.widget.Toast;

import com.example.arm.adapter.ExtendedSimpleAdapter;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.arm.Constants.DATE;
import static com.example.arm.Constants.ELEMENT;
import static com.example.arm.Constants.RANG;
import static com.example.arm.Constants.SOLVING_DATE;
import static com.example.arm.Constants.SOLVING_USER;
import static com.example.arm.Constants.TROUBLE;
import static com.example.arm.Constants.TROUBLE_FIRST_CREATION;
import static com.example.arm.Constants.VALUE;

public class ReportTroubleActivity extends AppCompatActivity {

    Activity currentActivity = this;

    TextView tvParams;
    Button btnBack;
    Button btnSave;
    ListView lvTroubleshooting;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    SimpleAdapter adapter;

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
        setContentView(R.layout.activity_report_trouble);
        setTitle(getResources().getText(R.string.title) + "(отчет по неисправностям) - " + user);

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
        btnBack = (Button)findViewById(R.id.btnBackFromReportLast);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity.finish();
            }
        });
        //=====================================================================

        //TEXTVIEW - строка с параметрами объекта
        //=====================================================================
        tvParams = (TextView)findViewById(R.id.tvParamsReportLast);
        tvParams.setText(station + " - " + park + " - " + switch_);
        //=====================================================================

        //BUTTON - сохранить
        //=====================================================================
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(c.getTime());
                String filename = "ОТЧЕТ_" + station + "_" + park + "_" + switch_ + "_" + date + ".xls";
                Toast.makeText(currentActivity, "Отчет сохранен под именем\n" + filename, Toast.LENGTH_LONG).show();
            }
        });
        //=====================================================================

        //LISTVIEW - таблица
        //=====================================================================
        lvTroubleshooting = (ListView)findViewById(R.id.lvReportLast);
        list = FileParser.getUnsolvedTroubles(jsonTableOfTroubles.getAsJsonArray("troubles"), station, park, switch_);
        adapter = new SimpleAdapter(
                currentActivity,
                list,
                R.layout.list_item_report_trouble,
                new String[] {ELEMENT, TROUBLE, VALUE, DATE},
                new int[] {R.id.tvElement, R.id.tvTemplate, R.id.tvLevel, R.id.tvDate});
        lvTroubleshooting.setAdapter(adapter);
        //=====================================================================
    }
}
