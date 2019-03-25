package com.example.myapplication;

import android.widget.ImageButton;

public class ImageButtonClass {

    int id;
    int arrId;
    String color;
    ImageButton imageButton;

    ImageButtonClass(MainActivity mainActivity, int id, int arrId)
    {
        this.arrId = arrId;
        this.id = id;
        this.color = "White";
        imageButton = (ImageButton) mainActivity.findViewById(arrId);
    }

    public int getId() {
        return id;
    }

    public int getArrId() {
        return arrId;
    }
    public String getColor()
    {
        return color;
    }

}
