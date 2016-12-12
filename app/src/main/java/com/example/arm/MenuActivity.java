package com.example.arm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.example.arm.Constants.*;


public class MenuActivity extends AppCompatActivity {
    String user = ChooseWorkerActivity.user;

    Activity activity = this;

    public static int actionMode;
    public static String prevStation = null;
    public static String prevPark = null;
    public static String prevSwitch_ = null;
    Intent iGoToChooseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle(getResources().getText(R.string.title) + " - " + user);
        iGoToChooseObject = new Intent(MenuActivity.this, ChooseObjectActivity.class);

        //BUTTON - Назад
        //==========================================================================
        Button btnBack = (Button)findViewById(R.id.btnBackMenu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        //==========================================================================

        //BUTTON - Осмотр стрелочного хозяйства
        //==========================================================================
        Button btnViewObject = (Button)findViewById(R.id.btnViewObject);
        btnViewObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMode = ACTION_MODE_INPUT_MEASUREMENT;
                startActivity(iGoToChooseObject);
            }
        });
        //==========================================================================

        //BUTTON - Результаты предыдущего осмотра
        //==========================================================================
        Button btnViewPrev = (Button)findViewById(R.id.btnViewPrev);
        btnViewPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMode = ACTION_MODE_VIEW_PREVIOUS_INSPECTION;
                startActivity(iGoToChooseObject);
            }
        });
        //==========================================================================

        //BUTTON - Устранение неисправностей
        //==========================================================================
        Button btnTroubleShooting = (Button)findViewById(R.id.btnTroubleShooting);
        btnTroubleShooting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMode = ACTION_MODE_TROUBLESHOOTING;
                startActivity(iGoToChooseObject);
            }
        });
        //==========================================================================

        //BUTTON - Формирование отчета
        //==========================================================================
        Button btnPU29 = (Button)findViewById(R.id.btnReports);
        btnPU29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMode = ACTION_MODE_REPORT;
                startActivity(new Intent(MenuActivity.this, ChooseObjectActivity.class));
            }
        });
        //==========================================================================
    }
}
