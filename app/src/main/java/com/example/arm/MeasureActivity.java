package com.example.arm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arm.bluetooth.BluetoothConnection;
import com.example.arm.parser.FileParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.arm.ChooseObjectActivity.isObjectChanged;
import static com.example.arm.ChooseObjectActivity.isSame;
import static com.example.arm.Constants.DATE_FORMAT;
import static com.example.arm.Constants.MEASURE_FIRST_CREATION;
import static com.example.arm.Constants.REQUEST_CONNECT_DEVICE;
import static com.example.arm.Constants.REQUEST_ENABLE_BT;
import static com.example.arm.MenuActivity.prevPark;
import static com.example.arm.MenuActivity.prevStation;
import static com.example.arm.MenuActivity.prevSwitch_;

//ОТРЕФАЧИТЬ!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


public class MeasureActivity extends AppCompatActivity {
    TextView textView1;
    public static int globalLength;
    ArrayList<String> elements ;

    String session = ChooseWorkerActivity.session;
    String user = ChooseWorkerActivity.user;
    String station = ChooseObjectActivity.station;
    String park = ChooseObjectActivity.park;
    String switch_ = ChooseObjectActivity.switch_;
    String id = ChooseObjectActivity.type;

    public static int nowButton =0;
    public static int now1Button=0;
    public static int now2Button=0;
    public static int now3Button=0;
    public static int lastButton=0;
    public static int last1Button=0;
    public static int last2Button=0;
    public static int last3Button=0;

    public static String elementNumber;


    Activity activity = MeasureActivity.this;

    String fMeasurements = "data_object.json";

    BluetoothConnection connection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        setTitle(getResources().getText(R.string.title) + " (режим ввода промеров) - " + user );
        ind=0;

        if(isSame) isObjectChanged = false;
        else if (prevSwitch_ == null) isObjectChanged = false;
        else isObjectChanged = true;

        textView1 = (TextView)findViewById(R.id.textView1);
        textView1.setText(station + " - " + park + " - " + switch_);

        JsonParser parser = new JsonParser();
        JsonObject jsSwitches = parser.parse(FileParser.loadFileFromAsset(this, "data_switch_type.json")).getAsJsonObject();
        elements = FileParser.getElements(jsSwitches,id);
        globalLength= elements.size();

        onCreateTropButton();

        final ScrollView myScroll = (ScrollView) findViewById(R.id.verticalScroll);
        //   myScroll.scrollTo( 0, (int)but.getCurr);

        myScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                myScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);

        onFirstClick();
        if(ChooseObjectActivity.isSame) setText();
    }

    public String getData(){
        String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return date;
    }

    public void setText(){
        String[][] matr = ChooseWorkerActivity.loadedData;
        for (int i=0;i<globalLength;i++){
            Button button = (Button) findViewById(i+1);
            Button button1 = (Button) findViewById(i+1001);
            Button button2 = (Button) findViewById(i+2001);
            Button button3 = (Button) findViewById(i+3001);
            button.setText(matr[i][0]);
            button1.setText(matr[i][1]);
            button2.setText(matr[i][2]);
            button3.setText(matr[i][3]);
        }
    }

    public void onFirstClick(){
        Button firstBut = (Button) findViewById(1+0);
        Button first1But = (Button) findViewById(1+1000);
        Button first2But = (Button) findViewById(1+2000);
        Button first3But = (Button) findViewById(1+3000);
        firstBut.callOnClick();
        nowButton = firstBut.getId();
        now1Button =first1But.getId();
        now2Button = first2But.getId();
        now3Button = first3But.getId();
    }

    public void onClickBack(View view){
        saveData();
        this.finish();
    }



    public void saveData() {
        String[][] matr = new String[globalLength][4];
        JsonArray result = new JsonArray();
        for (int i=0;i<globalLength;i++){

            Button button = (Button) findViewById(i+1);
            Button button1 = (Button) findViewById(i+1001);
            Button button2 = (Button) findViewById(i+2001);
            Button button3 = (Button) findViewById(i+3001);
            //Это в цикле по всем строкам таблицы
            JsonObject newResult = new JsonObject();
            newResult.addProperty("number", (String) button.getText());
            newResult.addProperty("object", (String) button1.getText());
            newResult.addProperty("template", (String) button2.getText());
            newResult.addProperty("level", (String) button3.getText());
            result.add(newResult);

                matr[i][0]= button.getText().toString();
                matr[i][1]= button1.getText().toString();
                matr[i][2]= button2.getText().toString();
                matr[i][3]= button3.getText().toString();

        }

        prevStation = station;
        prevPark = park;
        prevSwitch_ = switch_;

        ChooseWorkerActivity.loadedData = matr;

        String temp = FileParser.loadFileFromInternalStorage(this, fMeasurements);
        if (temp == null) {
            FileParser.saveFileToInternalStorage(this, fMeasurements, MEASURE_FIRST_CREATION);
            temp = FileParser.loadFileFromInternalStorage(this, fMeasurements);
        }

        JsonObject jsMeasure = new JsonParser().parse(temp).getAsJsonObject();

        JsonArray measure = jsMeasure.getAsJsonArray("measurements");

        //JsonArray measure = new JsonArray();
        JsonObject measurements = new JsonObject();

        /*AAAAAAAAAAAAAAAAAAAA!!!!!!!!!!!!!!!!!!!1111111111111111111111111ОДИНОДИНОДИНОДИН*/
        measurements .addProperty("sessionID", session);
        measurements .addProperty("date", getData());
        measurements .addProperty("worker", user);
        measurements .addProperty("station", station);
        measurements .addProperty("park", park);
        measurements .addProperty("switch", switch_);
        measurements.add("result", result);

        measure.add( measurements );

        jsMeasure.add("measurements", measure);

        FileParser.saveFileToInternalStorage(this, fMeasurements, jsMeasure.toString());
    }


    public void onMyPlusClick(View view)
    {
        int myId=2+20000;
        Button but = (Button) findViewById(myId);

        String param = ((String) but.getText());

        if (param.charAt(0)=='-'){
            for (int i = 160; i > 0; i--) {
                Button button = (Button) findViewById(i+20000);
                button.setText("+" + i );

            }
        }else {
            for (int i = 160; i > 0; i--) {
                Button button = (Button) findViewById(i+20000);
                button.setText("-"+i);

            }
        }
    }

    int flag=0;
    public void onClickBlock(View view)
    {
        if (flag==0) {
            Toast.makeText(this, "Ввод данных заблокирован", Toast.LENGTH_SHORT).show();
            flag=1;
            view.setBackgroundResource(R.drawable.stop);
            Button buttonBack = (Button) findViewById(R.id.button1);
            buttonBack.setEnabled(false);
            Button buttonNext = (Button) findViewById(R.id.button4);
            buttonNext.setEnabled(false);
            for (int i = 160; i >= 0; i--) {
                Button button = (Button) findViewById(i + 20000);
                button.setEnabled(false);
            }

            for (int i = 0; i < 40; i++) {
                int n = 1510 + i;
                Button button = (Button) findViewById(n + 10000);
                button.setEnabled(false);
            }
            for (int i=0;i<globalLength;i++)
            {
                Button button = (Button) findViewById(i+1);
                Button button1 = (Button) findViewById(i+1001);
                Button button2 = (Button) findViewById(i+2001);
                Button button3 = (Button) findViewById(i+3001);
                button.setEnabled(false);
                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setEnabled(false);
                button.setBackgroundColor(Color.rgb(203, 205, 209));
                button1.setBackgroundColor(Color.rgb(203, 205, 209));
                button2.setBackgroundColor(Color.rgb(203, 205, 209));
                button3.setBackgroundColor(Color.rgb(203, 205, 209));
            }

            //Bluetooth
            connection = new BluetoothConnection(activity);

        }else {
            Toast.makeText(this, "Ввод данных разрешён", Toast.LENGTH_SHORT).show();
            flag=0;
            view.setBackgroundResource(R.drawable.bluetooth);
            Button buttonBack = (Button) findViewById(R.id.button1);
            buttonBack.setEnabled(true);
            Button buttonNext = (Button) findViewById(R.id.button4);
            buttonNext.setEnabled(true);
            for (int i = 160; i >= 0; i--) {
                Button button = (Button) findViewById(i + 20000);
                button.setEnabled(true);
            }

            for (int i = 0; i < 40; i++) {
                int n = 1510 + i;
                Button button = (Button) findViewById(n + 10000);
                button.setEnabled(true);
            }
            for (int i=0;i<globalLength;i++)
            {
                Button button = (Button) findViewById(i+1);
                Button button1 = (Button) findViewById(i+1001);
                Button button2 = (Button) findViewById(i+2001);
                Button button3 = (Button) findViewById(i+3001);
                button.setEnabled(true);
                button1.setEnabled(true);
                button2.setEnabled(true);
                button3.setEnabled(true);
                button.setBackgroundColor(Color.rgb(255, 255, 255));
                button1.setBackgroundColor(Color.rgb(255, 255, 255));
                button2.setBackgroundColor(Color.rgb(255, 255, 255));
                button3.setBackgroundColor(Color.rgb(255, 255, 255));
            }

            connection.stopConnection();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    Intent serverIntent = new Intent(activity, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

                    break;
                case REQUEST_CONNECT_DEVICE:
                    String address = intent.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    connection.startConnection(address);
            }
        }
    }

    public int indicatorUr=0;
    public int indicatorSh=0;
    //public int flagNaprav=0;
    public static int ind=0;

    int idLastRow, idNowRow = 0;

    public void onCreateTropButton()
    {

        final TableLayout tl = (TableLayout)findViewById(R.id.tableMainLayout);
        final ScrollView scrTabl = (ScrollView)findViewById(R.id.tableScroll) ;
        Button btn, btn1,btn2,btn3;

        //обрабтка нажатий
        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {



                if ((v.getId()>=0)&&(v.getId()<=7000)) {
                    Button button,button1,button2,button3;
                    idLastRow=idNowRow;

                    if (v.getId() <= 1000) {
                        idNowRow = (v.getId());
                        System.out.println("_ID_" + idNowRow);
                        button = (Button) findViewById(v.getId());
                        button1 = (Button) findViewById(v.getId() + 1000);
                        button2 = (Button) findViewById(v.getId() + 2000);
                        button3 = (Button) findViewById(v.getId() + 3000);
                    } else {
                        if ((v.getId() > 1000) && (v.getId() <= 2000)) {
                            idNowRow = (v.getId()- 1000);
                            System.out.println("_ID_" + idNowRow);
                            button = (Button) findViewById(v.getId() - 1000);
                            button1 = (Button) findViewById(v.getId());
                            button2 = (Button) findViewById(v.getId() + 1000);
                            button3 = (Button) findViewById(v.getId() + 2000);

                        } else {
                            if ((v.getId() > 2000) && (v.getId() <= 3000)) {
                                idNowRow = (v.getId()- 2000);
                                System.out.println("_ID_" + idNowRow);
                                button = (Button) findViewById(v.getId() - 2000);
                                button1 = (Button) findViewById(v.getId() - 1000);
                                button2 = (Button) findViewById(v.getId());
                                button3 = (Button) findViewById(v.getId() + 1000);
                            } else {
                                if ((v.getId() > 3000) && (v.getId() <= 4000)) {
                                    idNowRow = (v.getId()- 3000);
                                    System.out.println("_ID_" + idNowRow);
                                    button = (Button) findViewById(v.getId() - 3000);
                                    button1 = (Button) findViewById(v.getId() - 2000);
                                    button2 = (Button) findViewById(v.getId() - 1000);
                                    button3 = (Button) findViewById(v.getId());
                                } else {
                                    idNowRow = (v.getId()- 6000);
                                    System.out.println("_ID_" + idNowRow);
                                    button = (Button) findViewById(v.getId() - 6000);
                                    button1 = (Button) findViewById(v.getId() - 5000);
                                    button2 = (Button) findViewById(v.getId() - 4000);
                                    button3 = (Button) findViewById(v.getId()- 3000);

                                }
                            }
                        }
                    }
                    //смена цвета
                    if (lastButton!=0) {
                        Button colorButton = (Button) findViewById(lastButton);
                        Button colorButton1 = (Button) findViewById(last1Button);
                        Button colorButton2 = (Button) findViewById(last2Button);
                        Button colorButton3 = (Button) findViewById(last3Button);
                        colorButton.setBackgroundColor(Color.WHITE);
                        colorButton1.setBackgroundColor(Color.WHITE);
                        colorButton2.setBackgroundColor(Color.WHITE);
                        colorButton3.setBackgroundColor(Color.WHITE);
                    }


                    //COLOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    button.setBackgroundColor(Color.rgb(211, 213, 242));
                    button1.setBackgroundColor(Color.rgb(211, 213, 242));
                    button2.setBackgroundColor(Color.rgb(211, 213, 242));
                    button3.setBackgroundColor(Color.rgb(211, 213, 242));

                    nowButton = button.getId();
                    now1Button = button1.getId();
                    now2Button = button2.getId();
                    now3Button = button3.getId();

                    lastButton = nowButton;
                    last1Button = now1Button;
                    last2Button = now2Button;
                    last3Button = now3Button;




                }
                else {
                    if (nowButton != 0) {
                        if ((v.getId() >= 10000) && (v.getId() < 20000)) {
                            Button textButton = (Button)findViewById(now2Button);
                            Button rezButton = (Button)findViewById(v.getId());
                            //   int rez=Integer.parseInt((String)rezButton.getText()) ;
                            textButton.setText(rezButton.getText()+"");
                        /*    Button textButton = (Button)findViewById(v.getId());
                            textButton.setText(v.getId()+"");*/
                        }
                        if ((v.getId() >= 20000) && (v.getId() < 30000)) {
                            Button textButton = (Button)findViewById(now3Button);
                            Button rezButton = (Button)findViewById(v.getId());
                            //           int rez=Integer.parseInt((String)rezButton.getText()) ;
                            textButton.setText(rezButton.getText()+"");
                        }
                    }
                }






                if ((v.getId()>=10000)&&(v.getId()<20000)) {
                    indicatorUr++;
                }
                if ((v.getId()>=20000)&&(v.getId()<30000)) {
                    indicatorSh ++;
                }
                if ((v.getId()>=0)&&(v.getId()<9000)) {
                    indicatorUr = 0;
                    indicatorSh = 0;
                }
                //в зависимости от общего кол-ва
                if ((indicatorSh!=0)&&(indicatorUr!=0)&&(nowButton<=globalLength)&&(nowButton>0))
                {
                    indicatorUr = 0;
                    indicatorSh = 0;



                    if ((nowButton != globalLength )&&(ind==0))
                    {
                        Button next;
                        next = (Button) findViewById(nowButton + 1);
                        ind =1;

                        TableRow tr = (TableRow) findViewById(idNowRow+6000);
                        scrTabl.scrollTo(0, 115*(idNowRow-2 ));
                        System.out.println( "id " + 115*(idNowRow-2 ));
                        System.out.println("ПОЗИЦИЯ "+ 115*(idNowRow-2 ) + "id " + idNowRow);
                        next.callOnClick();
                    }
                    else {
                        if ((nowButton == globalLength) && (ind == 0)) {
                            Button next;
                            next = (Button) findViewById(nowButton - 2);
                            ind = 2;
                            TableRow tr = (TableRow) findViewById(idNowRow+6000);
                            System.out.println( "id " + 115*(idNowRow-2 ));
                            scrTabl.scrollTo(0, 115*idNowRow);
                            System.out.println("ПОЗИЦИЯ "+ 115*(idNowRow-2 ) + "id " + idNowRow);
                            next.callOnClick();

                        } else {
                            if ((nowButton != globalLength) && (nowButton != 1)) {
                                Button next;
                                if (ind == 1) {
                                    next = (Button) findViewById(nowButton + 1);

                                    TableRow tr = (TableRow) findViewById(idNowRow+6000);
                                    scrTabl.scrollTo(0, 115*(idNowRow-2 ));
                                    System.out.println( "id " + 115*(idNowRow-2 ));
                                    System.out.println("ПОЗИЦИЯ "+ 115*(idNowRow-2 ) + "id " + idNowRow);
                                    next.callOnClick();

                                }
                                if (ind == 2) {
                                    next = (Button) findViewById(nowButton - 1);

                                    TableRow tr = (TableRow) findViewById(idNowRow+6000);
                                    scrTabl.scrollTo(0, 115*(idNowRow-2 ));
                                    System.out.println( "id " + 115*(idNowRow-2 ));
                                    System.out.println("ПОЗИЦИЯ "+ 115*(idNowRow-2 )+ "id " + idNowRow);
                                    next.callOnClick();


                                }
                            }
                        }
                    }
                }
            }
        };

        for (int k = 0; k < globalLength; k++) {
            TableRow row = new TableRow(this);
            row.setId(k+1+6000);
            row.setBackgroundColor(Color.rgb(201, 205, 214));
            row.setMinimumHeight(115);
            //row.setMinimumWidth(1728);

            btn = new Button(this);
            btn.setBackgroundColor(Color.rgb(255, 255, 255));
            btn.setHeight(110);
            btn.setWidth(192);
            btn.setTextColor(Color.rgb(0, 0, 0));
            btn.setTextSize(25);
            int n=k+1;
            btn.setId(n+0);
            btn.setGravity(Gravity.CENTER);
            btn.setText("" + btn.getId());
            row.addView(btn);

            btn1 = new Button(this);
            //    btn1.setLayoutParams(tr);
            btn1.setBackgroundColor(Color.rgb(255, 255, 255));
            btn1.setHeight(110);
            btn1.setWidth(1152);
            btn1.setTextSize(25);
            btn1.setTextColor(Color.rgb(0, 0, 0));
            btn1.setId(n+1000);

            // btn1.setGravity(Gravity.LEFT);
            btn1.setText(elements.get(k) );
            row.addView(btn1);

            btn2 = new Button(this);
            //   btn2.setLayoutParams(tr);
            btn2.setBackgroundColor(Color.rgb(255, 255, 255));
            btn2.setHeight(110);
            btn2.setWidth(192);
            btn2.setTextSize(25);
            btn2.setTextColor(Color.rgb(0, 0, 0));
            btn2.setId(n+2000);
            btn2.setText("" );
            btn2.setGravity(Gravity.CENTER);
            row.addView(btn2);

            btn3 = new Button(this);
            //   btn3.setLayoutParams(tr);
            btn3.setBackgroundColor(Color.rgb(255, 255, 255));
            btn3.setHeight(110);
            btn3.setWidth(192);
            btn3.setTextSize(25);
            btn3.setTextColor(Color.rgb(0, 0, 0));
            btn3.setId(n+3000);
            btn3.setGravity(Gravity.CENTER);
            btn3.setText("" );

            row.addView(btn3);
            tl.addView(row);

            btn.setOnClickListener(onClickListener);
            btn1.setOnClickListener(onClickListener);
            btn2.setOnClickListener(onClickListener);
            btn3.setOnClickListener(onClickListener);

        }

        LinearLayout linear1 = (LinearLayout)findViewById(R.id.linearLayout1);
        for (int i=160; i > 0;i--){
            btn = new Button(this);
            btn.setBackgroundColor(Color.rgb(255, 255, 255));
            btn.setHeight(110);
            btn.setWidth(192);
            btn.setBackgroundResource(R.drawable.reg);
            btn.setTextSize(25);
            btn.setTextColor(Color.rgb(0, 0, 0));
            btn.setText("+" + i);
            btn.setId(i+20000);
            linear1.addView(btn);
            btn.setOnClickListener(onClickListener);
        }
        btn = new Button(this);
        //     btn.setBackgroundColor(Color.rgb(255, 255, 255));
        btn.setHeight(110);
        btn.setWidth(192);
        btn.setTextSize(25);
        btn.setTextColor(Color.rgb(0, 0, 0));
        btn.setBackgroundResource(R.drawable.reg);
        btn.setText("0");
        btn.setId(0+20000);
        linear1.addView(btn);
        btn.setOnClickListener(onClickListener);
        LinearLayout linear2 = (LinearLayout)findViewById(R.id.linearLayout2);
        for (int i=0; i <= 40;i++){
            btn = new Button(this);
            //  btn.setBackgroundColor(Color.rgb(255, 255, 255));
            btn.setHeight(140);
            btn.setWidth(180);
            btn.setTextColor(Color.rgb(0, 0, 0));
            btn.setTextSize(25);

            btn.setBackgroundResource(R.drawable.reg);
            int n=1510+i;
            btn.setId(n+10000);
            btn.setText("" + n);
            linear2.addView(btn);
            btn.setOnClickListener(onClickListener);
        }

    }

    public void onClickClear(View view) {
        File file = new File(getFilesDir(), fMeasurements);
        file.delete();
        this.finish();
        //connection.insertDataFromBluetooth("1234", "12");
    }

    public void onClickTrouble(View view) {
        Intent iGoToTrouble = new Intent(MeasureActivity.this, TroubleActivity.class);
        startActivity(iGoToTrouble);
        //
    }
}
