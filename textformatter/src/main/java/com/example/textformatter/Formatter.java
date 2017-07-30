package com.example.textformatter;

/**
 * Created by ntankasala on 7/26/17.
 */

public class Formatter {
    private String rawString;

    public Formatter(String rawString) {
        this.rawString = rawString;
    }

    public String getFormated(){
        return this.rawString.toUpperCase() + " is formatted";
    }
}
