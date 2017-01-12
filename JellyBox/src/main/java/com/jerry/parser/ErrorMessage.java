package com.jerry.parser;

/**
 * Created by jerryDev on 2017. 1. 10..
 */
public class ErrorMessage {
    public final static String FILE_NOT_FOUND = "file not founde";
    public static final String ERR_MSG_THIS_MEHTODE_NOT_IMPLEMENTS = "this mehtode not implements!!!";

    public static String getErrorMessage(String errorMessgeHeader, String addedMessage){
        return  String.format( "%s [%s]\n",errorMessgeHeader,addedMessage );
    }
}
