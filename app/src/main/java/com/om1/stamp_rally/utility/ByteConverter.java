package com.om1.stamp_rally.utility;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by yaboo on 2016/12/10.
 */

public final class ByteConverter {
    private ByteConverter(){}

    public static byte[] convert(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
