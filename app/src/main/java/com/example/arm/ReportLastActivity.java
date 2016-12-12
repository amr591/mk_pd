package com.example.arm;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arm.adapter.ExtendedSimpleAdapter;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.example.arm.Constants.DATE;
import static com.example.arm.Constants.ELEMENT;
import static com.example.arm.Constants.LEVEL;
import static com.example.arm.Constants.RANG;
import static com.example.arm.Constants.SOLVING_DATE;
import static com.example.arm.Constants.SOLVING_USER;
import static com.example.arm.Constants.TEMPLATE;
import static com.example.arm.Constants.TROUBLE;
import static com.example.arm.Constants.TROUBLE_FIRST_CREATION;

public class ReportLastActivity extends AppCompatActivity {

    String fMeasurements = "data_object.json";
    ArrayList<String> elements;
    String user = ChooseWorkerActivity.user;
    String station = ChooseObjectActivity.station;
    String park = ChooseObjectActivity.park;
    String switch_ = ChooseObjectActivity.switch_;
    String id = ChooseObjectActivity.type;
    JsonObject jsonTableOfTroubles;
    JsonObject mainObject;

    String fTableTroubles = "troubles.json";
    String fDataObject = "data_object.json";

    Button btnBack;
    TextView tvParams;
    ListView lvTable;
    Button btnSave;

    SimpleAdapter adapter;
    Activity currentActivity = this;

    ArrayList<String> switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_last);
        setTitle(getResources().getText(R.string.title) + "(отчет по неисправностям) - " + user);

        //JSON-файлы
        //==========================================================================================
        JsonParser parser = new JsonParser();
        JsonObject jsSwitches = parser.parse(FileParser.loadFileFromAsset(this, "data_switch_type.json")).getAsJsonObject();
        elements = FileParser.getElements(jsSwitches,id);
        String sMeasurements = FileParser.loadFileFromInternalStorage(this, fMeasurements);
        if (sMeasurements == null) {
            sMeasurements = Constants.MEASURE_FIRST_CREATION;
        }
        JsonObject jMeasurements = parser.parse(sMeasurements).getAsJsonObject();
        String temp = FileParser.loadFileFromInternalStorage(this, fTableTroubles);
        if (temp == null) {
            FileParser.saveFileToInternalStorage(this, fTableTroubles, TROUBLE_FIRST_CREATION);
            temp = FileParser.loadFileFromInternalStorage(this, fTableTroubles);
        }
        jsonTableOfTroubles = parser.parse(temp).
                getAsJsonObject();
        JsonObject obj1 = FileParser.getLastObject(jMeasurements, "measurements", station, park, switch_);
        JsonObject obj2 = FileParser.getScnLastObject(jMeasurements, "measurements", station, park, switch_);
        mainObject = parser.parse(FileParser.loadFileFromAsset(this, fDataObject)).
                getAsJsonObject();
        switches = FileParser.getSwitches(mainObject, station, park);
        //==========================================================================================

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
        tvParams.setText(station + " - " + park);
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

        lvTable = (ListView)findViewById(R.id.lvReportLast);
        ArrayList<HashMap<String, String>> list = FileParser.getListForReport(jMeasurements, jsonTableOfTroubles.getAsJsonArray("troubles"), station, park, switches);
        adapter = new ExtendedSimpleAdapter(
                currentActivity,
                list,
                R.layout.list_item_report_last,
                new String[] {ELEMENT, TEMPLATE, LEVEL, DATE},
                new int[] {R.id.tvElement, R.id.tvTemplate, R.id.tvLevel, R.id.tvDate});
        lvTable.setAdapter(adapter);
        //=====================================================================
    }
}
