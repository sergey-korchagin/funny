package com.nafunny.app.utils;

import android.database.Cursor;

/**
 * Created by User on 13/12/2015.
 */
public class CloseHelper {
    public static void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
