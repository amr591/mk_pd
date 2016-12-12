package com.example.arm.data;

/**
 * Created by Alyona on 20.10.2016.
 */


public class TemplateData {
    private String name;
    private String level;
    private String templ;
    private String data;

    public TemplateData(String name, String level, String templ, String data) {
        this.name = name;
        this.level = level;
        this.templ = templ;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTempl() {
        return templ;
    }

    public void setTempl(String templ) {
        this.templ = templ;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
