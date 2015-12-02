package com.parse.starter.interfaces;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by User on 01/12/2015.
 */
public interface GetMorePhotos {
    void getMorePhotos(List<ParseObject> parseObjects, int size);
}
