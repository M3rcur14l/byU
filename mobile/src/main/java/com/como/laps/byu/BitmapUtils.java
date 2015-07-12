package com.como.laps.byu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Antonello on 12/07/15.
 */

public class BitmapUtils {

    public static Bitmap getBitmap(String path, Context context, int w, int h) {
        InputStream file = null;
        Bitmap bitmap = null;
        try {
            file = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return Bitmap.createScaledBitmap(bitmap, w, h, false);
    }
}

