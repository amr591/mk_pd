package com.example.arm;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arm.parser.FileParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import static com.example.arm.ChooseWorkerActivity.user;
import static com.example.arm.Constants.LEVEL;
import static com.example.arm.Constants.RESULT;
import static com.example.arm.Constants.TEMPLATE;
import static com.example.arm.Constants.TROUBLES;

public class MainActivity extends AppCompatActivity {
    String fMeasurements = "data_object.json";
    String fElements = "data_switch_type.json";
    String fTableTroubles = "troubles.json";
    String fListOfTroublesBySwitchElements = "list_of_troubles.json";

    String station = ChooseObjectActivity.station;
    String park = ChooseObjectActivity.park;
    String switch_ = ChooseObjectActivity.switch_;
    String type = ChooseObjectActivity.type;

    ArrayList<String> elements;
    ArrayList<String> currentMeasureTemplate;
    ArrayList<String> currentMeasureLevel;
    ArrayList<String> prevMeasureTemplate;
    ArrayList<String> prevMeasureLevel;

    ArrayList<String> troubleElements;
    ArrayList<String> ids;
    ArrayList<String> currentTroubles;
    ArrayList<String> prevTroubles;

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String sMeasurements = FileParser.loadFileFromInternalStorage(this, fMeasurements);

        if (sMeasurements == null) {
            sMeasurements = Constants.MEASURE_FIRST_CREATION;
        }

        String sElements = FileParser.loadFileFromAsset(this, fElements);

        setTitle(getResources().getText(R.string.title) + " (режим просмотра результатов) - " + user);

        Button btnBack = (Button)findViewById(R.id.btnBackInspection);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        TextView tvParams = (TextView)findViewById(R.id.tvParamsInspection);
        tvParams.setText(station + " - " + park + " - " + switch_);

        JsonParser parser = new JsonParser();
        JsonObject jMeasurements = parser.parse(sMeasurements).getAsJsonObject();
        JsonObject jElements = parser.parse(sElements).getAsJsonObject();
        JsonObject obj1 = FileParser.getLastObject(jMeasurements, "measurements", station, park, switch_);
        JsonObject obj2 = FileParser.getScnLastObject(jMeasurements, "measurements", station, park, switch_);

        elements = FileParser.getElements(jElements, type);

        currentMeasureTemplate = (obj1.has("result")
                ? FileParser.getArrayOfValues(obj1.get("result").getAsJsonArray(), "template")
                : FileParser.getEmptyList(elements.size()));

        currentMeasureLevel = (obj1.has("result")
                ? FileParser.getArrayOfValues(obj1.get("result").getAsJsonArray(), "level")
                : FileParser.getEmptyList(elements.size()));

        prevMeasureTemplate = (obj2.has(RESULT)
                ? FileParser.getArrayOfValues(obj2.get(RESULT).getAsJsonArray(), TEMPLATE)
                : FileParser.getEmptyList(elements.size()));

        prevMeasureLevel = (obj2.has(RESULT)
                ? FileParser.getArrayOfValues(obj2.get(RESULT).getAsJsonArray(), LEVEL)
                : FileParser.getEmptyList(elements.size()));


        JsonObject jsonTableOfTroubles = parser.parse(FileParser.loadFileFromInternalStorage(this, fTableTroubles)).
                getAsJsonObject();

        JsonObject jsonListOfTroublesBySwitchElements = parser.parse(FileParser.
                loadFileFromAsset(this, fListOfTroublesBySwitchElements)).
                getAsJsonObject();

        troubleElements = FileParser.getSwitchElements(jsonListOfTroublesBySwitchElements);

        ids = FileParser.getIds(troubleElements.size());

        currentTroubles = FileParser.getListOfTroubles(FileParser.getListOfLastTroubles(jsonTableOfTroubles, TROUBLES, station, park, switch_), troubleElements);

        prevTroubles = FileParser.getListOfTroubles(FileParser.getListOfScnLastTroubles(jsonTableOfTroubles, TROUBLES, station, park, switch_), troubleElements);

        globalLength=troubleElements.size();
        globalLength2=elements.size();
        onCreateButton();
    }

    Button btn;
    int globalLength;
    int globalLength2;
    //для 1 таблицы
    //public String[] masName= {"Стык рамного рельса", "У острия остряков", "В корне остряков прямо","Стык рамного рельса", "У острия остряков", "В корне остряков прямо"};
    String[] masName = new String[globalLength2];

    //текущий осмотр
    //public String[] mas1TO1= {"1523",null,"1526","1529","1530","1522"};
    //public String[] mas1TO2= {"0","0","5","0","5","0"};

    String[] mas1TO1 = new String[globalLength2];
    String[] mas1TO2 = new String[globalLength2];

    //предыдущий осмотр
    //public String[] mas2TO1= {"1520","1520","1522","1526","1528","1520"};
    //public String[] mas2TO2= {"0","0","2","0","2","2"};

    String[] mas2TO1 = new String[globalLength2];
    String[] mas2TO2 = new String[globalLength2];

    //для 2 таблицы
    //public String[] masName2= {"Стык рамного рельса", "У острия остряков", "В корне остряков прямо","Стык рамного рельса", "У острия остряков", "В корне остряков прямо"};
    //public String[] masNumber= {"1","1","1","2","8","0"};
    String[] masName2 = new String[globalLength];
    String[] masNumber = new String[globalLength];
    //текущий осмотр
    //public String[] masNowOS= {"Стык рамного рельса", "У острия остряков", "","Стык рамного рельса", "", "В корне остряков прямо"};
    String[] masNowOS = new String[globalLength];
    //предыдущий осмотр
    //public String[] masLastOS= {"", "", "В корне остряков прямо","", "", ""};
    String[] masLastOS = new String[globalLength];
    //public String[] masSize= {"","","","","",null};
    String[] masSize = new String[globalLength];

    protected Button createButton(String text, int wigth, int flag){

        btn = new Button(this);
        btn.setBackgroundColor(Color.rgb(255, 255, 255));
        btn.setHeight(50);
        btn.setWidth(wigth);
        btn.setTextColor(Color.rgb(0, 0, 0));
        btn.setTextSize(25);
        btn.setGravity(Gravity.CENTER);
        if (text!=null){
            if (flag==1) {
                if (Integer.parseInt(text)>0) text="+"+text;
            }
        }
        else {
            text="";
        }
        btn.setText(text);
        return btn;

    }
    protected void onCreateButton(){
        masName = elements.toArray(masName);
        mas1TO1 = currentMeasureTemplate.toArray(mas1TO1);
        mas1TO2 = currentMeasureLevel.toArray(mas1TO2);
        mas2TO1 = prevMeasureTemplate.toArray(mas2TO1);
        mas2TO2 = prevMeasureLevel.toArray(mas2TO2);

        masName2 = troubleElements.toArray(masName2);
        masNumber = ids.toArray(masNumber);
        masNowOS = currentTroubles.toArray(masNowOS);
        masLastOS = prevTroubles.toArray(masLastOS);
        masSize = FileParser.getEmptyList(globalLength).toArray(masSize);

        //Toast.makeText(this, String.valueOf(currentTroubles), Toast.LENGTH_LONG).show();

        TableLayout tl = (TableLayout)findViewById(R.id.tableMainLayout);
        for (int k = 0; k < globalLength2; k++) {
            TableRow row = new TableRow(this);
            //  row.setId(k+1+6000);
            row.setBackgroundColor(Color.rgb(201, 205, 214));
            row.setMinimumHeight(68);
            //  row.setMinimumWidth(1920);

            row.addView(createButton(masName[k],920,0));
            row.addView(createButton(mas1TO1[k],200,0));
            row.addView(createButton(mas1TO2[k],200,0));
            row.addView(createButton(mas2TO1[k],200,0));
            row.addView(createButton(mas2TO2[k],200,0));
            if ((mas1TO1[k]!="")&&(mas2TO1[k]!="")&&(mas1TO1[k]!= null)&&(mas2TO1[k]!=null)){
                row.addView(createButton(String.valueOf(Integer.parseInt(mas1TO1[k])-Integer.parseInt(mas2TO1[k])),200 ,1));
            }else  {
                row.addView(createButton("0",200,1));
            }
            tl.addView(row);


        }


        TableLayout tl2 = (TableLayout)findViewById(R.id.tableMainLayout2);
        for (int k = 0; k < globalLength; k++) {
            TableRow row = new TableRow(this);
            //  row.setId(k+1+6000);
            row.setBackgroundColor(Color.rgb(201, 205, 214));
            row.setMinimumHeight(68);

            row.addView(createButton(masName2[k],920,0));
            row.addView(createButton(masNowOS[k],400,0));
            row.addView(createButton(masLastOS[k],400,0));
            row.addView(createButton(masSize[k],200,0));
            tl2.addView(row);

        }
    }
}
