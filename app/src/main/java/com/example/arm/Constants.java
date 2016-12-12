package com.example.arm;

public interface Constants {

    //Для адаптера в таблице неисправностей (Добавить!!!)
    //================================================================================
    String NUMBER = "number";
    String ELEMENT = "element";
    String TROUBLE = "trouble";
    String VALUE = "value";
    String TOAST = "toast";
    String TROUBLES = "troubles";
    String RESULT = "result";
    String LEVEL = "level";
    String TEMPLATE = "template";
    String DATE = "date";
    String SOLVING_DATE = "solving_date";
    String RANG = "rang";
    String SOLVING_USER = "solving_user";
    //================================================================================

    //Первоначальное заполнение файла с таблицей неисправностей
    //================================================================================
    String TROUBLE_FIRST_CREATION = "{\n" +
            "  \"troubles\" : [\n" +
            "\n" +
            "  ]\n" +
            "}";
    //================================================================================

    //Формат даты
    String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    //Первоначальное заполнение файла со списком промеров
    //================================================================================
    String MEASURE_FIRST_CREATION = "{\n" +
            "  \"measurements\" : [\n" +
            "  ]\n" +
            "}";
    //================================================================================

    //Действия, исполняемые по нажатию кнопок меню
    //================================================================================
    int ACTION_MODE_INPUT_MEASUREMENT = 1;
    int ACTION_MODE_VIEW_PREVIOUS_INSPECTION = 2;
    int ACTION_MODE_TROUBLESHOOTING = 3;
    int ACTION_MODE_REPORT = 4;
    //================================================================================

    //Коды запросов для BluetoothConnection
    //================================================================================
    int REQUEST_CONNECT_DEVICE = 1;
    int REQUEST_ENABLE_BT = 2;
    //================================================================================

    //Коды запросов для Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    //Состояния подключения
    int STATE_NONE = 0;       // we're doing nothing
    int STATE_CONNECTING = 1; // now initiating an outgoing connection
    int STATE_CONNECTED = 2;  // now connected to a remote device
}
