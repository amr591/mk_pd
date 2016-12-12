package com.example.arm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MenuReportActivity extends AppCompatActivity {
    Activity activity = this;

    Button btnBack;
    Button btnReportLast;
    Button btnReportLastTwo;
    Button btnReportTroubles;
    TextView tvParams;

    String user;
    String station;
    String park;
    String switch_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_report);
        user = ChooseWorkerActivity.user;
        station = ChooseObjectActivity.station;
        park = ChooseObjectActivity.park;
        switch_ = ChooseObjectActivity.switch_;

        //TITLE
        //================================================
        setTitle(getText(R.string.title).toString() + " (режим формирования отчетов) - " + user);
        //================================================

        //BUTTON - кнопка "Назад"
        //================================================
        btnBack = (Button)findViewById(R.id.btnBackFromReport);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        //================================================

        //TEXTVIEW - Параметры
        //================================================
        tvParams = (TextView)findViewById(R.id.tvParamsReport);
        tvParams.setText(station + " - " + park + " - " + switch_);
        //================================================

        //BUTTON - отчет по последним промерам
        //================================================
        btnReportLast = (Button)findViewById(R.id.btnReportLast);
        btnReportLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuReportActivity.this, ReportLastActivity.class));
            }
        });
        //================================================

        //BUTTON - отчет по предпоследним промерам
        //================================================
        btnReportLastTwo = (Button)findViewById(R.id.btnReportLastTwo);
        btnReportLastTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "ОТЧЕТ СОХРАНЕН", Toast.LENGTH_SHORT).show();
            }
        });
        //================================================

        //BUTTON - отчет по неисправностям
        //================================================
        btnReportTroubles = (Button)findViewById(R.id.btnReportTroubles);
        btnReportTroubles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuReportActivity.this, ReportTroubleActivity.class));
            }
        });
        //================================================
    }
}
