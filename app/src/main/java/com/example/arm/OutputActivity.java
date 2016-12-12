package com.example.arm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.arm.data.TemplateData;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import static com.example.arm.Constants.LEVEL;
import static com.example.arm.Constants.RESULT;
import static com.example.arm.Constants.TEMPLATE;
import static com.example.arm.Constants.TROUBLE;
import static com.example.arm.Constants.TROUBLES;

public class OutputActivity extends AppCompatActivity {

    TextView tvOutput;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        tvOutput = (TextView)findViewById(R.id.tvOutput1);

        String sMeasurements = FileParser.loadFileFromInternalStorage(this, fMeasurements);
        String sElements = FileParser.loadFileFromAsset(this, fElements);

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

        tvOutput.setText(jsonTableOfTroubles.toString());
    }
}
