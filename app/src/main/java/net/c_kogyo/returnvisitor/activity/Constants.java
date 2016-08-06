package net.c_kogyo.returnvisitor.activity;

import net.c_kogyo.returnvisitor.R;

/**
 * Created by SeijiShii on 2016/07/24.
 */

public class Constants {

    public static final int[] buttonRes = {
            R.mipmap.button_gray,
            R.mipmap.button_red,
            R.mipmap.button_purple,
            R.mipmap.button_blue,
            R.mipmap.button_green,
            R.mipmap.button_yellow,
            R.mipmap.button_orange
    } ;

    public static final int[] markerRes = {
            R.mipmap.marker,
            R.mipmap.marker_red,
            R.mipmap.marker_purple,
            R.mipmap.marker_blue,
            R.mipmap.marker_green,
            R.mipmap.marker_yellow,
            R.mipmap.marker_orange
    };

    public class PersonCode {

        public static final int ADD_PERSON_REQUEST_CODE = 1000;
        public static final int PERSON_ADDED_RESULT_CODE = 1001;
        public static final int PERSON_CANCELED_RESULT_CODE = 1002;
    }

}
