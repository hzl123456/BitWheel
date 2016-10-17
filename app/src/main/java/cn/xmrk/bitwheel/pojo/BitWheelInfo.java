package cn.xmrk.bitwheel.pojo;

import android.graphics.Bitmap;

/**
 * Created by Au61 on 2016/8/31.
 */

public class BitWheelInfo {

    public String text;
    public Bitmap bitmap;
    public ItemInfo info;

    public BitWheelInfo(String text, Bitmap bitmap) {
        this.text = text;
        this.bitmap = bitmap;
        this.info = new ItemInfo();
    }


    public static class ItemInfo {

        /**
         * 背景小图标文字合成的bitmap
         **/
        public Bitmap itemBitmap;

        /**
         * 三种状态
         * 0 表示未被选中
         * 1 表示选中
         **/
        public int bitmapType;


    }

}
