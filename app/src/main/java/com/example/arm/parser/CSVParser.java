package com.example.arm.parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amr59 on 12.12.2016.
 */

public class CSVParser {
    public static String ListToCSV(ArrayList<HashMap<String, String>> list) {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> map: list) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(map.get("element"));
            sb1.append(',');
            sb1.append(map.get("template"));
            sb1.append(',');
            sb1.append(map.get("level"));
            sb1.append(',');
            sb1.append(map.get("date"));
            sb1.append('\n');
            sb.append(sb1.toString());
        }
        return sb.toString();
    }
}
