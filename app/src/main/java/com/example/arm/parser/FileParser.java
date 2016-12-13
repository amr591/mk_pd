package com.example.arm.parser;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.example.arm.ChooseObjectActivity;
import com.example.arm.ChooseWorkerActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;
import static com.example.arm.ChooseObjectActivity.isObjectChanged;
import static com.example.arm.Constants.*;
import com.example.arm.data.RailwayData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.jar.JarOutputStream;

//РЕФАЧЬ, ЕБАНЫЙ ШАКАЛ!!!
public class FileParser {
    public static String loadFileFromAsset(Activity activity, String name) {
        String json;
        try {
            InputStream is = activity.getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void saveFileToInternalStorage(Context activity, String fileName, String data) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    activity.openFileOutput(fileName, MODE_PRIVATE)));
            bw.write(data);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadFileFromInternalStorage(Context context, String fileName) {
        String json = new String();
        try {
            InputStream is = context.openFileInput(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return json;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveFileToSDCard(String filename, String content) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Отчеты");
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, filename);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(content);
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getStations(JsonObject mainObject) {
        JsonArray stations = mainObject.getAsJsonArray("stations");
        ArrayList<String> parsedStations = new ArrayList<String>();
        for (JsonElement station : stations) {
            JsonObject jsStation = station.getAsJsonObject();
            parsedStations.add(jsStation.get("name").getAsString());
        }
        return parsedStations;
    }

    public static ArrayList<String> getParks(JsonObject mainObject, String station) {
        ArrayList<String> parsedParks = new ArrayList<String>();
        if (station != null) {
            JsonArray stations = mainObject.getAsJsonArray("stations");
            JsonObject reqStation = null;
            for (JsonElement st : stations) {
                JsonObject jsStation = st.getAsJsonObject();
                if (jsStation.get("name").getAsString().equals(station)) {
                    reqStation = jsStation;
                    break;
                }
            }

            JsonArray parks = reqStation.getAsJsonArray("parks");

            for (JsonElement park : parks) {
                JsonObject jsPark = park.getAsJsonObject();
                parsedParks.add(jsPark.get("name").getAsString());
            }
        }
        return parsedParks;
    }

    public static ArrayList<String> getSwitches(JsonObject mainObject, String station, String park) {
        ArrayList<String> parsedSwitches = new ArrayList<String>();
        JsonArray stations = mainObject.getAsJsonArray("stations");
        JsonObject reqStation = null;
        for (JsonElement st : stations) {
            JsonObject jsStation = st.getAsJsonObject();
            if (jsStation.get("name").getAsString().equals(station)) {
                reqStation = jsStation;
                break;
            }
        }

        JsonArray parks = reqStation.getAsJsonArray("parks");
        JsonObject reqPark = null;
        for (JsonElement p : parks) {
            JsonObject jsPark = p.getAsJsonObject();
            if (jsPark.get("name").getAsString().equals(park)) {
                reqPark = jsPark;
                break;
            }
        }

        JsonArray switches = reqPark.getAsJsonArray("switches");
        for (JsonElement sw : switches) {
            JsonObject jsSwitch = sw.getAsJsonObject();
            parsedSwitches.add(jsSwitch.get("name").getAsString());
        }
        return parsedSwitches;
    }


    public static ArrayList<String> getNames(JsonObject mainObject) {
        JsonArray names = mainObject.getAsJsonArray("workers");
        ArrayList<String> parsedNames = new ArrayList<>();
        for (JsonElement worker: names) {
            JsonObject jsWorker = worker.getAsJsonObject();
            parsedNames.add(jsWorker.get("name").getAsString());
        }
        return parsedNames;
    }

    public static ArrayList<HashMap<String, String>> getTroubles(JsonObject mainObject) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        JsonArray troubles = mainObject.getAsJsonArray("troubles");

        for (JsonElement trouble: troubles) {
            JsonObject jsonTrouble = trouble.getAsJsonObject();
            if (jsonTrouble.has("sessionID")){
                if(jsonTrouble.has("sessionID") && jsonTrouble.get("sessionID").getAsString().equals(ChooseWorkerActivity.session )){
                    HashMap<String, String> map = new HashMap<>();
                    map.put(NUMBER, jsonTrouble.get(NUMBER).getAsString());
                    map.put(ELEMENT, jsonTrouble.get(ELEMENT).getAsString());
                    map.put(TROUBLE, jsonTrouble.get(TROUBLE).getAsString());
                    map.put(VALUE, jsonTrouble.get(VALUE).getAsString());

                    list.add(map);
                }
            }


        }

        return list;
    }

    public static ArrayList<String> getSwitchElements(JsonObject mainObject) {
        JsonArray stations = mainObject.getAsJsonArray("listOfTroubles");
        ArrayList<String> parsedStations = new ArrayList<String>();
        for (JsonElement station : stations) {
            JsonObject jsStation = station.getAsJsonObject();
            parsedStations.add(jsStation.get("switchElement").getAsString());
        }
        return parsedStations;
    }

    public static ArrayList<String> getListOfTroubles(JsonObject mainObject, String switchElement) {
        ArrayList<String> parsedParks = new ArrayList<String>();
        if (switchElement != null) {
            JsonArray stations = mainObject.getAsJsonArray("listOfTroubles");
            JsonObject reqStation = null;
            for (JsonElement st : stations) {
                JsonObject jsStation = st.getAsJsonObject();
                if (jsStation.get("switchElement").getAsString().equals(switchElement)) {
                    reqStation = jsStation;
                    break;
                }
            }

            JsonArray parks = reqStation.getAsJsonArray("troubles");

            for (JsonElement park : parks) {
                JsonObject jsPark = park.getAsJsonObject();
                parsedParks.add(jsPark.get("name").getAsString());
            }
        }
        return parsedParks;
    }

    public static ArrayList<String> getElements(JsonObject mainObject, String id) {
        ArrayList<String> elements = new ArrayList<String>();
        if (id != null) {
            JsonArray switchTypes = mainObject.getAsJsonArray("switchTypes");
            JsonObject reqSwitch = null;
            for (JsonElement st : switchTypes) {
                JsonObject jsSwitch = st.getAsJsonObject();
                if (jsSwitch.get("id").getAsString().equals(id)) {
                    reqSwitch = jsSwitch;
                    break;
                }
            }

            JsonArray points = reqSwitch.getAsJsonArray("points");

            for (JsonElement point : points) {
                JsonObject jsPoint = point.getAsJsonObject();
                elements.add(jsPoint.get("name").getAsString());
            }
        }
        return elements;
    }

    public static String getTypeOfSwitch(JsonObject mainObject, String station, String park, String switch_) {
        String id = "";
        JsonArray stations = mainObject.getAsJsonArray("stations");
        JsonObject reqStation = null;
        for (JsonElement st : stations) {
            JsonObject jsStation = st.getAsJsonObject();
            if (jsStation.get("name").getAsString().equals(station)) {
                reqStation = jsStation;
                break;
            }
        }

        JsonArray parks = reqStation.getAsJsonArray("parks");
        JsonObject reqPark = null;
        for (JsonElement p : parks) {
            JsonObject jsPark = p.getAsJsonObject();
            if (jsPark.get("name").getAsString().equals(park)) {
                reqPark = jsPark;
                break;
            }
        }

        JsonArray switches = reqPark.getAsJsonArray("switches");
        for (JsonElement sw : switches) {
            JsonObject jsSwitch = sw.getAsJsonObject();
            if (jsSwitch.get("name").getAsString().equals(switch_)) {
                id = jsSwitch.get("type").getAsString();
            }
        }
        return id;
    }

    public static RailwayData getObjectDataFirstLevel(JsonObject object, String array1Name, int position1) {
        JsonArray array1 = object.getAsJsonArray(array1Name);
        JsonObject jsonObject1 = array1.get(position1).getAsJsonObject();
        return new RailwayData(jsonObject1.get("name").getAsString(),
                jsonObject1.get("short_name").getAsString(),
                jsonObject1.get("id").getAsString());

    }

    public static RailwayData getObjectDataSecondLevel(JsonObject object, String array1Name, int position1, String array2Name, int position2) {
        JsonArray array1 = object.getAsJsonArray(array1Name);
        JsonObject jsonObject1 = array1.get(position1).getAsJsonObject();
        JsonArray array2 = jsonObject1.getAsJsonArray(array2Name);
        JsonObject jsonObject2 = array2.get(position2).getAsJsonObject();
        return new RailwayData(jsonObject2.get("name").getAsString(),
                               jsonObject2.get("short_name").getAsString(),
                               jsonObject2.get("id").getAsString());
    }

    public static RailwayData getObjectDataThirdLevel(JsonObject object, String array1Name, int position1,
                                                                         String array2Name, int position2,
                                                                         String array3Name, int position3) {
        JsonArray array1 = object.getAsJsonArray(array1Name);
        JsonObject jsonObject1 = array1.get(position1).getAsJsonObject();
        JsonArray array2 = jsonObject1.getAsJsonArray(array2Name);
        JsonObject jsonObject2 = array2.get(position2).getAsJsonObject();
        JsonArray array3 = jsonObject2.getAsJsonArray(array3Name);
        JsonObject jsonObject3 = array3.get(position3).getAsJsonObject();
        return new RailwayData(jsonObject3.get("name").getAsString(),
                jsonObject3.get("short_name").getAsString(),
                jsonObject3.get("id").getAsString());
    }



    public static JsonObject getLastObject(JsonObject mainObject, String arrayName, String station, String park, String switch_) {
        JsonArray array1 = mainObject.getAsJsonArray(arrayName);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        JsonObject retObject = new JsonObject();
        try {
            date = format.parse("01-01-1970 00:00:00");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("date")) {
                try{
                    Date newDate = format.parse(jo.get("date").getAsString());
                    if (newDate.compareTo(date) > 0 &&
                            jo.get("station").getAsString().equals(station) &&
                            jo.get("park").getAsString().equals(park) &&
                            jo.get("switch").getAsString().equals(switch_)) {
                        date = newDate;
                        retObject = jo;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return retObject;
    }

    public static JsonObject getScnLastObject(JsonObject mainObject, String arrayName, String station, String park, String switch_) {
        JsonArray array1 = mainObject.getAsJsonArray(arrayName);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date date1 = new Date();
        Date date2 = new Date();
        JsonObject retObject = new JsonObject();
        JsonObject ret2Object = new JsonObject();
        try {
            date1 = format.parse("01-01-1970 00:00:00");
            date2 = format.parse("01-01-1970 00:00:00");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("date")) {
                try{
                    Date newDate = format.parse(jo.get("date").getAsString());
                    if (newDate.compareTo(date1) > 0 &&
                            jo.get("station").getAsString().equals(station) &&
                            jo.get("park").getAsString().equals(park) &&
                            jo.get("switch").getAsString().equals(switch_)) {
                        date2 = date1;
                        date1 = newDate;
                        ret2Object = retObject;
                        retObject = jo;
                    }

                    else if (newDate.compareTo(date2) > 0 &&
                            jo.get("station").getAsString().equals(station) &&
                            jo.get("park").getAsString().equals(park) &&
                            jo.get("switch").getAsString().equals(switch_)) {
                        date2 = newDate;
                        ret2Object = jo;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret2Object;
    }

    public static ArrayList<String> getEmptyList(int length) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add("");
        }
        return list;
    }

    public static ArrayList<String> getArrayOfValues(JsonArray array, String key) {
        ArrayList<String> retArray = new ArrayList<>();

        for (JsonElement je: array) {
            retArray.add(je.getAsJsonObject().get(key).getAsString());
        }

        return retArray;
    }

    public static ArrayList<String> getIds(int length) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    public static String getSessionID(JsonObject mainObject, String station, String park, String switch_) {
        JsonArray array1 = mainObject.getAsJsonArray("troubles");
        String sessionID = "";

        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("sessionID")) {
                if (jo.get("sessionID").getAsString().compareTo(sessionID) > 0 &&
                        jo.get("station").getAsString().equals(station) &&
                        jo.get("park").getAsString().equals(park) &&
                        jo.get("switch").getAsString().equals(switch_)) {
                    sessionID = jo.get("sessionID").getAsString();
                }
            }
        }

        return sessionID;
    }

    public static JsonArray getListOfLastTroubles(JsonObject mainObject, String arrayName, String station, String park, String switch_) {
        JsonArray array1 = mainObject.getAsJsonArray(arrayName);
        String sessionID = "";
        JsonArray retArray = new JsonArray();

        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("sessionID")) {
                if (jo.get("sessionID").getAsString().compareTo(sessionID) > 0  &&
                        jo.get("station").getAsString().equals(station) &&
                        jo.get("park").getAsString().equals(park) &&
                        jo.get("switch").getAsString().equals(switch_)) {
                    sessionID = jo.get("sessionID").getAsString();
                }
            }
        }

        for (JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("sessionID")) {
                if (jo.get("sessionID").getAsString().equals(sessionID)) {
                    retArray.add(jo);
                }
            }
        }

        return retArray;
    }

    public static JsonArray getListOfScnLastTroubles(JsonObject mainObject, String arrayName, String station, String park, String switch_) {
        JsonArray array1 = mainObject.getAsJsonArray(arrayName);
        String sessionID = "";
        String session2ID = "";
        JsonArray retArray = new JsonArray();

        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("sessionID")) {
                if (jo.get("sessionID").getAsString().compareTo(sessionID) > 0  &&
                        jo.get("station").getAsString().equals(station) &&
                        jo.get("park").getAsString().equals(park) &&
                        jo.get("switch").getAsString().equals(switch_)) {
                    session2ID = sessionID;
                    sessionID = jo.get("sessionID").getAsString();
                }
                else if (jo.get("sessionID").getAsString().compareTo(session2ID) > 0  &&
                        jo.get("station").getAsString().equals(station) &&
                        jo.get("park").getAsString().equals(park) &&
                        jo.get("switch").getAsString().equals(switch_)) {
                    session2ID = jo.get("sessionID").getAsString();
                }
            }
        }

        for (JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.has("sessionID")) {
                if (jo.get("sessionID").getAsString().equals(session2ID)) {
                    retArray.add(jo);
                }
            }
        }

        return retArray;
    }

    public static ArrayList<String> getListOfTroubles(JsonArray array, ArrayList<String> elements) {

        ArrayList<String> retArray = new ArrayList<>();

        for (String elem: elements) {
            String trouble = "";

            for (JsonElement je: array) {
                JsonObject jo = je.getAsJsonObject();
                if (jo.has("element")) {
                    if (jo.get("element").getAsString().equals(elem)) {
                        trouble = jo.get("trouble").getAsString();
                    }
                }
            }

            retArray.add(trouble);
        }

        return retArray;
    }

    public static ArrayList<HashMap<String, String>> getUnsolvedTroubles(JsonArray troubles, String station, String park, String switch_) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (JsonElement je: troubles) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.get("station").getAsString().equals(station) &&
                    jo.get("park").getAsString().equals(park) &&
                    jo.get("switch").getAsString().equals(switch_) &&
                    !jo.get("solved").getAsBoolean()) {
                HashMap<String, String> map = new HashMap<>();
                map.put(ELEMENT, jo.get(ELEMENT).getAsString());
                map.put(TROUBLE, jo.get(TROUBLE).getAsString());
                map.put(DATE, jo.get(DATE).getAsString());
                map.put(VALUE, jo.get(VALUE).getAsString());
                map.put(SOLVING_DATE, "");
                map.put(RANG, "");
                map.put(SOLVING_USER, "");

                list.add(map);
            }
        }

        return list;
    }

    public static String getDate() {
        String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return date;
    }

    public static ArrayList<HashMap<String, String>> getListForReport(JsonObject jMeasurements, JsonArray troubles,
                                                                      String station, String park,
                                                                      ArrayList<String> switches) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (String switch_: switches) {
            JsonObject obj1 = FileParser.getLastObject(jMeasurements, "measurements", station, park, switch_);
            JsonObject obj2 = FileParser.getScnLastObject(jMeasurements, "measurements", station, park, switch_);
            list.addAll(getDataForReport(obj1,
                    obj2,
                    troubles,
                    station, park, switch_));
        }
        return list;
    }

    public static ArrayList<HashMap<String, String>> getDataForReport(JsonObject lastObject,
                                                                      JsonObject preLastObject,
                                                                      JsonArray troubles,
                                                                      String station, String park, String switch_) {
        JsonArray array1 = lastObject.has("result") ? lastObject.getAsJsonArray("result") : new JsonArray();
        JsonArray array2 = preLastObject.has("result") ? preLastObject.getAsJsonArray("result") : new JsonArray();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "Стрелка #"+switch_);
            map.put("template", lastObject.has(DATE) ? lastObject.get(DATE).getAsString().substring(0, 10) : "-");
            map.put("level", preLastObject.has(DATE) ? preLastObject.get(DATE).getAsString().substring(0, 10) : "-");
            map.put("date", "");

            list.add(map);
        }
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "");
            map.put("template", "");
            map.put("level", "");
            map.put("date", "");

            list.add(map);
        }

        for(JsonElement je: array1) {
            JsonObject jo = je.getAsJsonObject();
            HashMap<String, String> map = new HashMap<>();
            map.put("element", jo.get("object").getAsString());
            if (jo.get("template").getAsString().equals("")) {
                map.put("template", "-");
            }
            else {
                map.put("template", jo.get("template").getAsString() + "    " + jo.get("level").getAsString());
            }
            map.put("level", "");
            map.put("date", "");
            list.add(map);
        }

        int i = 2;
        for(JsonElement je: array2) {
            JsonObject jo = je.getAsJsonObject();
            HashMap<String, String> map = list.get(i);
            if (jo.get("template").getAsString().equals("")) {
                map.remove("level");
                map.put("level", "-");
            }
            else {
                map.remove("level");
                map.put("level", jo.get("template").getAsString() + "    " + jo.get("level").getAsString());
            }
            i++;
        }

        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "");
            map.put("template", "");
            map.put("level", "");
            map.put("date", "");

            list.add(map);
        }

        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "Неисправности");
            map.put("template", "");
            map.put("level", "");
            map.put("date", "");

            list.add(map);
        }

        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "");
            map.put("template", "");
            map.put("level", "");
            map.put("date", "");

            list.add(map);
        }

        for (JsonElement je: troubles) {
            JsonObject jo = je.getAsJsonObject();
            if (jo.get("station").getAsString().equals(station) &&
                    jo.get("park").getAsString().equals(park) &&
                    jo.get("switch").getAsString().equals(switch_) &&
                    !jo.get("solved").getAsBoolean()) {
                HashMap<String, String> map = new HashMap<>();
                map.put(ELEMENT, jo.get(TROUBLE).getAsString());
                map.put(TEMPLATE, jo.get(VALUE).getAsString());
                map.put(LEVEL, "");
                map.put(DATE, "");

                list.add(map);
            }
        }
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("element", "");
            map.put("template", "");
            map.put("level", "");
            map.put("date", "");

            list.add(map);
        }

        return list;
    }
}
