package com.example.arm.data;

/**
 * Created by Андрей on 01.11.2016.
 */

public class RailwayData {
    private String mName;
    private String mShortName;
    private String mId;

    public RailwayData() {
        mName = "N/A";
        mShortName = "N/A";
        mId = "-1";
    }

    public RailwayData(String name, String shortName, String id) {
        mName = name;
        mShortName = shortName;
        mId = id;
    }

    public String getName() {return mName;}
    public void setName(String value) {mName = value;}

    public String getShortName() {return mShortName;}
    public void setShortName(String value) {mShortName = value;}

    public String getId() {return mId;}
    public void setId(String value) {mId = value;}

}
