package com.example.arm.parser;

import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Андрей on 01.11.2016.
 */

public class CommandParser {

    private final char COMMAND_DIVIDER = '%';
    private final char KEY_DIVIDER = '#';
    private final char VALUE_DIVIDER = '$';

    private String mCommand;
    private String mComment;
    private HashMap<String, String> mParams;

    public CommandParser() {
        mCommand = "0";
        mComment = null;
        mParams = new HashMap<>();
    }

    public CommandParser(String command) {
        command = trimCommand(command);
        mCommand = parseCommand(command);
        mComment = parseComment(command);
        mParams = parseParams(command);
    }

    public CommandParser(String command, String comment, HashMap<String, String> params) {
        mCommand = command;
        mComment = comment;
        mParams = params;
    }

    private String trimCommand(String command) {
        String com = command;
        while(com.charAt(com.length() - 1) != '$') {
            com = com.substring(0, com.length() - 1);
        }
        while (!Character.isDigit(com.charAt(0))) {
            com = com.substring(1);
        }
        return com;
    }

    public String getCommand() {return mCommand;}
    public String getComment() {return mComment;}
    public HashMap<String, String> getParams() {return mParams;}

    private String parseCommand(String command) {
        return ( command.indexOf(COMMAND_DIVIDER) < 0 ? command : command.substring(0, command.indexOf(COMMAND_DIVIDER)));
    }

    @Nullable
    private String parseComment(String command) {
        int firstIndex = command.indexOf(COMMAND_DIVIDER);
        int lastIndex = command.lastIndexOf(COMMAND_DIVIDER);
        return (firstIndex == lastIndex ? null : command.substring(firstIndex + 1, lastIndex));
    }

    @Nullable
    private HashMap<String, String> parseParams(String command) {
        HashMap<String, String> parsedParams = new HashMap<>();
        if (command.lastIndexOf(COMMAND_DIVIDER) + 1 >= command.length() || command.indexOf(COMMAND_DIVIDER) < 0) return null;

        String params = command.substring(command.lastIndexOf(COMMAND_DIVIDER) + 1);

        while (params.length() > 0) {
            String key = params.substring(0, params.indexOf(KEY_DIVIDER));
            String value = params.substring(params.indexOf(KEY_DIVIDER) + 1, params.indexOf(VALUE_DIVIDER));

            parsedParams.put(key, value);

            params = params.substring(params.indexOf(VALUE_DIVIDER) + 1);
        }

        return parsedParams;
    }

    public String setCommand() {
        StringBuilder command = new StringBuilder();
        command.append(mCommand);
        if(mComment != null || mParams != null) command.append(COMMAND_DIVIDER);
        if(mComment != null) command.append(mComment + COMMAND_DIVIDER);
        if(mParams != null) {
            for (String key: mParams.keySet()) {
                command.append(key + KEY_DIVIDER + mParams.get(key) + VALUE_DIVIDER);
            }
        }

        return command.toString();
    }
}
