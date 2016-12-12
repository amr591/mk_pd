package com.example.arm;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.arm.adapter.ExtendedArrayAdapter;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.arm.Constants.*;


///TODO: добавить константы

public class TroubleActivity extends AppCompatActivity {

    Dialog dialogAddNewTrouble;

    Activity currentActivity;

    TextView tvParams;
    Button btnNewTrouble, btnAddTroubleToList, btnObject, btnBack, btnUpdateTrouble, btnDialogBack;
    ListView lvTroublesTable, lvElement, lvTrouble;
    EditText etValue;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();

    ArrayList<String> listElements, listTroubles;

    //Возможно, придется расширить
    SimpleAdapter adapter;

    ExtendedArrayAdapter<String> adapterElements, adapterTroubles;

    JsonParser parser;
    JsonObject jsonTableOfTroubles, jsonListOfTroublesBySwitchElements;

    String station = ChooseObjectActivity.station;
    String park = ChooseObjectActivity.park;
    String switch_ = ChooseObjectActivity.switch_;
    String user = ChooseWorkerActivity.user;
    String session = ChooseWorkerActivity.session;
    String element = "N / A";
    String trouble = "N / A";

    String fTableTroubles = "troubles.json";
    String fListOfTroublesBySwitchElements = "list_of_troubles.json";

    String correctedNumber;

    boolean isActionAdding; //true - добавление новой неисправности, false - редактирование существующей

    HashMap<String, String> clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouble);
        currentActivity = this;

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

        jsonListOfTroublesBySwitchElements = parser.parse(FileParser.
                loadFileFromAsset(this, fListOfTroublesBySwitchElements)).
                    getAsJsonObject();
        //=====================================================================

        //TITLE
        //=====================================================================
        setTitle(getResources().getText(R.string.title) + " (режим ввода неисправностей) - " + user);
        //=====================================================================

        //TEXTVIEW - Строка с параметрами объекта
        //======================================================
        tvParams = (TextView)findViewById(R.id.tvParamsTroubleshooting);
        tvParams.setText(station + " - " + park + " - " + switch_);

        tvParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), fTableTroubles);
                file.delete();
                currentActivity.finish();
            }
        });
        //=======================================================

        //BUTTON - Вернуться к промерам
        //===============================================================
        btnObject = (Button)findViewById(R.id.btnUseless);
        btnObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileParser.saveFileToInternalStorage(currentActivity, fTableTroubles, jsonTableOfTroubles.toString());
                currentActivity.finish();
            }
        });
        //===============================================================

        //BUTTON - Кнопка "Назад"
        //===============================================================
        btnBack = (Button)findViewById(R.id.btnBackFromTroubleshooting);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileParser.saveFileToInternalStorage(currentActivity, fTableTroubles, jsonTableOfTroubles.toString());

                currentActivity.finish();
                //startActivity(new Intent(TroubleActivity.this, ChooseObjectActivity.class));
            }
        });
        //===============================================================

        //LISTVIEW - Таблица с неисправностями
        //===============================================================
        lvTroublesTable = (ListView)findViewById(R.id.lvTroubles);
        list = FileParser.getTroubles(jsonTableOfTroubles);
        adapter = new SimpleAdapter(this, list, R.layout.list_item_trouble,
                new String[] {NUMBER, ELEMENT, TROUBLE, VALUE},
                new int[] {R.id.tvNumber, R.id.tvElement, R.id.tvTrouble, R.id.tvValue});
        lvTroublesTable.setAdapter(adapter);

        lvTroublesTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnUpdateTrouble.setVisibility(View.VISIBLE);
                btnUpdateTrouble.setClickable(true);
                clicked = (HashMap<String, String>)lvTroublesTable.getItemAtPosition(position);
                correctedNumber = clicked.get(NUMBER);
             }
        });
        //===============================================================

        //BUTTON - Добавить новую неисправность
        //=================================================================
        btnNewTrouble = (Button)findViewById(R.id.btnAddTrouble);
        btnNewTrouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActionAdding = true;
                element = "N / A";
                trouble = "N / A";
                listElements = FileParser.getSwitchElements(jsonListOfTroublesBySwitchElements);
                adapterElements = new ExtendedArrayAdapter<>(currentActivity,
                        R.layout.list_item_dialog_add_trouble, listElements);
                lvElement.setAdapter(adapterElements);

                dialogAddNewTrouble.show();
            }
        });
        //==================================================================

        //BUTTON - Изменить неисправность
        //==================================================================
        btnUpdateTrouble = (Button)findViewById(R.id.btnUpdateTrouble);
        btnUpdateTrouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActionAdding = false;
                element = "N / A";
                trouble = "N / A";
                listElements = FileParser.getSwitchElements(jsonListOfTroublesBySwitchElements);
                adapterElements = new ExtendedArrayAdapter<>(currentActivity,
                        R.layout.list_item_dialog_add_trouble, listElements);
                lvElement.setAdapter(adapterElements);

                dialogAddNewTrouble.show();
            }
        });
        //==================================================================

        //DIALOG - Всплывающее окно для добавления новой неисправности
        //===============================================================
        dialogAddNewTrouble = new Dialog(TroubleActivity.this);
        dialogAddNewTrouble.setContentView(R.layout.dialog_add_trouble);
        dialogAddNewTrouble.setTitle("Добавление новой неисправности");
        dialogAddNewTrouble.setCancelable(true);
        //================================================================

        //BUTTON_IN_DIALOG - Кнопка "Назад"
        //================================================================
        btnDialogBack = (Button)dialogAddNewTrouble.findViewById(R.id.btnDialogBack);
        btnDialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddNewTrouble.cancel();
            }
        });
        //================================================================

        //LISTVIEW_IN_DIALOG - Список элементов стрелки
        //=================================================================
        lvElement = (ListView) dialogAddNewTrouble.findViewById(R.id.lvElementsDialog);
        lvElement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterElements.setSelection(position);
                element = lvElement.getItemAtPosition(position).toString();
                listTroubles = FileParser.
                        getListOfTroubles(jsonListOfTroublesBySwitchElements, element);
                adapterTroubles = new ExtendedArrayAdapter<>(currentActivity,
                                        R.layout.list_item_dialog_add_trouble,
                                        listTroubles);
                lvTrouble.setAdapter(adapterTroubles);
            }
        });
        //=================================================================

        //LISTVIEW_IN_DIALOG - Список неисправностей элемента
        //=================================================================
        lvTrouble = (ListView) dialogAddNewTrouble.findViewById(R.id.lvTroublesDialog);
        lvTrouble.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapterTroubles.setSelection(position);
                trouble = lvTrouble.getItemAtPosition(position).toString();
            }
        });
        //==================================================================

        //EDITTEXT_IN_DIALOG - Поле ввода величины
        //==================================================================
        etValue = (EditText) dialogAddNewTrouble.findViewById(R.id.etValue);
        //==================================================================

        //BUTTON_IN_DIALOG - Добавление неисправности в список
        //===================================================================
        btnAddTroubleToList = (Button)dialogAddNewTrouble.findViewById(R.id.btnDialogAdd);
        btnAddTroubleToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isActionAdding) {
                        //Создаем новый объект неисправности
                        JsonObject newTrouble = new JsonObject();
                        newTrouble.addProperty("number", list.size() + 1);
                        newTrouble.addProperty("element", element);
                        newTrouble.addProperty("trouble", trouble);
                        newTrouble.addProperty("value", etValue.getText().toString());
                        newTrouble.addProperty("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
                        newTrouble.addProperty("solved", false);
                        newTrouble.addProperty("station", station);
                        newTrouble.addProperty("park", park);
                        newTrouble.addProperty("switch", switch_);
                        newTrouble.addProperty("user", user);
                        newTrouble.addProperty("sessionID", session);
                        //Получаем текущий список неисправностей
                        JsonArray troubles = jsonTableOfTroubles.getAsJsonArray("troubles");
                        //Добавляем новую неисправность в список
                        troubles.add(newTrouble);
                        //Сохраняем изменения в объекте
                        jsonTableOfTroubles.add("troubles", troubles);
                    }

                    else {
                        btnUpdateTrouble.setClickable(false);
                        btnUpdateTrouble.setVisibility(View.INVISIBLE);

                        JsonArray troubles = jsonTableOfTroubles.getAsJsonArray("troubles");
                        JsonArray newTroubles = new JsonArray();
                        for (JsonElement elem: troubles) {
                            if (elem.getAsJsonObject().get(NUMBER).getAsString().equals(correctedNumber)) {
                                JsonObject newTrouble = new JsonObject();
                                newTrouble.addProperty("number", correctedNumber);
                                newTrouble.addProperty("element", element);
                                newTrouble.addProperty("trouble", trouble);
                                newTrouble.addProperty("value", etValue.getText().toString());
                                newTrouble.addProperty("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
                                newTrouble.addProperty("solved", false);
                                newTrouble.addProperty("station", station);
                                newTrouble.addProperty("park", park);
                                newTrouble.addProperty("switch", switch_);
                                newTrouble.addProperty("user", user);
                                newTrouble.addProperty("sessionID", session);

                                newTroubles.add(newTrouble);
                            }

                            else newTroubles.add(elem);
                        }

                        jsonTableOfTroubles.add("troubles", newTroubles);
                    }
                    //Обновляем таблицу на экране
                    list = FileParser.getTroubles(jsonTableOfTroubles);
                    adapter = new SimpleAdapter(currentActivity, list, R.layout.list_item_trouble,
                            new String[]{NUMBER, ELEMENT, TROUBLE, VALUE},
                            new int[]{R.id.tvNumber, R.id.tvElement, R.id.tvTrouble, R.id.tvValue});
                    lvTroublesTable.setAdapter(adapter);

                    //Сохраняем изменения
                    FileParser.saveFileToInternalStorage(currentActivity, fTableTroubles, jsonTableOfTroubles.toString());

                    //Очищаем поле ввода
                    etValue.setText("");
                    etValue.setHint("Величина");

                    //Очищаем ListView-ы в диалоговом окне
                    adapterTroubles.clear();
                    adapterTroubles.notifyDataSetChanged();

                    //Закрываем диалоговое окно
                    dialogAddNewTrouble.cancel();

                    lvTroublesTable.setSelection(adapter.getCount() - 1);
                }
                catch (Exception e) {
                    dialogAddNewTrouble.cancel();
                }
            }
        });
        //===================================================================
    }
}
