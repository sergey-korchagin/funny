package com.parse.starter;

import android.widget.ImageView;

import com.parse.ParseObject;

/**
 * Created by serge_000 on 25/12/2015.
 */
public class CustomObject {
    ParseObject parseObject;
    ImageView imageView;
public CustomObject(ParseObject parseObject,ImageView imageView){
    this.parseObject = parseObject;
    this.imageView = imageView;
}
    public ParseObject getParseObject() {
        return parseObject;
    }

    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
