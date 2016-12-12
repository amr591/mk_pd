package com.example.arm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arm.adapter.ExtendedArrayAdapter;
import com.example.arm.data.RailwayData;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.arm.ChooseWorkerActivity.session;
import static com.example.arm.Constants.*;


public class ChooseObjectActivity extends AppCompatActivity {
    Activity activity = this;

    String user = ChooseWorkerActivity.user;

    public static String station = null;
    public static String park = null;
    public static String switch_ = null;
    public static String type = null;

    int actionMode = MenuActivity.actionMode;

    ArrayList<String> datasetStations = new ArrayList<>();
    ArrayList<String> datasetParks = new ArrayList<>();
    ArrayList<String> datasetSwitches = new ArrayList<>();

    ExtendedArrayAdapter<String> adapterStations;
    ExtendedArrayAdapter<String> adapterParks;
    ExtendedArrayAdapter<String> adapterSwitches;

    JsonParser parser;
    JsonObject mainObject;

    Button btnBack;
    TextView tvParams;

    ListView lvStations;
    ListView lvParks;
    ListView lvSwitches;

    String fDataObject = "data_object.json";

    Intent iGoToObject;

    public static RailwayData dataStation;
    public static RailwayData dataPark;
    public static RailwayData dataSwitch;

    public static boolean isSame;
    public static boolean isObjectChanged;

    int positionStation;
    int positionPark;
    int positionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_object);
        isSame = true;

        //TITLE
        //========================================================================
        switch (actionMode) {
            case ACTION_MODE_INPUT_MEASUREMENT:
                setTitle(getResources().getText(R.string.title) + " (режим осмотра) - " + user);
                break;
            case ACTION_MODE_VIEW_PREVIOUS_INSPECTION:
                setTitle(getResources().getText(R.string.title) + " (просмотр данных предыдущего осмотра) - " + user);
                break;
            case ACTION_MODE_TROUBLESHOOTING:
                setTitle(getResources().getText(R.string.title) + " (режим устранения неисправностей) - " + user);
                break;
            case ACTION_MODE_REPORT:
                setTitle(getResources().getText(R.string.title) + " (режим формирования отчетов) - " + user);
                break;
        }
        //========================================================================

        //JSON-Объекты
        //========================================================================
        parser = new JsonParser();
        mainObject = parser.parse(FileParser.loadFileFromAsset(this, fDataObject)).
                getAsJsonObject();
        //========================================================================

        //BUTTON - Назад
        //========================================================================
        btnBack = (Button)findViewById(R.id.btnBackChooseObject);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        //========================================================================

        //TEXTVIEW - Параметры
        //========================================================================
        tvParams = (TextView)findViewById(R.id.tvParamsChooseObject);
        //========================================================================

        //LISTVIEW - Список станций
        //========================================================================
        lvStations = (ListView)findViewById(R.id.lvStations);
        datasetStations = FileParser.getStations(mainObject);
        adapterStations = new ExtendedArrayAdapter<>
                (this, R.layout.list_item_objects, datasetStations);
        lvStations.setAdapter(adapterStations);
        lvStations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterStations.setSelection(position);
                station = lvStations.getItemAtPosition(position).toString();
                dataStation = FileParser.getObjectDataFirstLevel(mainObject, "stations", position);
                tvParams.setText(station);
                positionStation = position;

                adapterParks.clear();
                adapterParks.clearSelection();
                datasetParks.clear();
                datasetParks = FileParser.getParks(mainObject, station);
                adapterParks.addAll(datasetParks);
                adapterParks.notifyDataSetChanged();

                adapterSwitches.clearSelection();
                adapterSwitches.clear();
                adapterSwitches.notifyDataSetChanged();
            }
        });
        //========================================================================

        //LISTVIEW - Список парков
        //========================================================================
        lvParks = (ListView)findViewById(R.id.lvParks);
        adapterParks = new ExtendedArrayAdapter<>
                (this, R.layout.list_item_objects, datasetParks);
        lvParks.setAdapter(adapterParks);
        lvParks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterParks.setSelection(position);
                park = lvParks.getItemAtPosition(position).toString();
                dataPark = FileParser.getObjectDataSecondLevel(mainObject, "stations", positionStation, "parks", position);
                tvParams.setText(station + " - " + park);
                positionPark = position;

                if (actionMode == ACTION_MODE_REPORT) {
                    startActivity(new Intent(ChooseObjectActivity.this, ReportLastActivity.class));
                }
                else {
                    adapterSwitches.clear();
                    datasetSwitches.clear();
                    datasetSwitches = FileParser.getSwitches(mainObject, station, park);
                    adapterSwitches.addAll(datasetSwitches);
                    adapterSwitches.notifyDataSetChanged();
                }

            }
        });
        //========================================================================

        //LISTVIEW - Список объектов
        //========================================================================
        iGoToObject = new Intent(ChooseObjectActivity.this, MeasureActivity.class);
        lvSwitches = (ListView)findViewById(R.id.lvSwitches);
        adapterSwitches = new ExtendedArrayAdapter<>(this, R.layout.list_item_objects, datasetSwitches);
        lvSwitches.setAdapter(adapterSwitches);
        lvSwitches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterSwitches.setSelection(position);
                switch_ = lvSwitches.getItemAtPosition(position).toString();
                type = FileParser.getTypeOfSwitch(mainObject, station, park, switch_);
                dataSwitch = FileParser.getObjectDataThirdLevel(mainObject, "stations", positionStation,
                                                                             "parks", positionPark,
                                                                             "switches", position);
                tvParams.setText(station + " - " + park + " - " + switch_);
                positionSwitch = position;
                switch (actionMode){
                    case ACTION_MODE_INPUT_MEASUREMENT:
                        if(station == MenuActivity.prevStation && park == MenuActivity.prevPark && switch_ == MenuActivity.prevSwitch_) isSame = true;
                        else isSame = false;
                        session = String.valueOf(Calendar.getInstance().getTimeInMillis());

                        startActivity(iGoToObject);
                        break;
                    case ACTION_MODE_VIEW_PREVIOUS_INSPECTION:
                        //Переход к активности предыдущих осмотров
                        startActivity(new Intent(ChooseObjectActivity.this, MainActivity.class));
                        //startActivity(new Intent(ChooseObjectActivity.this, OutputActivity.class));
                        break;
                    case ACTION_MODE_TROUBLESHOOTING:
                        //Переход к активности устранения неисправностей
                        startActivity(new Intent(ChooseObjectActivity.this, TroubleshootingActivity.class));
                        break;
                    case ACTION_MODE_REPORT:
                        startActivity(new Intent(ChooseObjectActivity.this, MenuReportActivity.class));
                        break;
                    default: break;
                }
            }
        });
        //========================================================================
    }
}
