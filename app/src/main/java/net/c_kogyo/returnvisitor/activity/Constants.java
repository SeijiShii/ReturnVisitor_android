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

    public static final String DATE_LONG = "date_data";

    public static final String REQUEST_CODE = "Request Code";

    public class PersonCode {

        public static final int ADD_PERSON_REQUEST_CODE = 1000;
        public static final int EDIT_PERSON_REQUEST_CODE = 1001;

        public static final int PERSON_ADDED_RESULT_CODE = 1002;
        public static final int PERSON_EDITED_RESULT_CODE = 1003;
        public static final int PERSON_CANCELED_RESULT_CODE = 1004;

    }

    public class PlacementCode {

        public static final int PLACEMENT_REQUEST_CODE = 2000;
        public static final int PLACEMENT_ADDED_RESULT_CODE = 2010;
        public static final int PLACEMENT_CANCELED_RESULT_CODE = 2020;
    }

    public class LogInCode {

        public static final int GOOGLE_SIGN_IN_RC = 3000;
//        public static final int FACEBOOK_LOG_IN_RC = 3001;


    }

    public class RecordVisitActions {

        public static final String NEW_PLACE_ACTION = "new_place_action";
        public static final String NEW_VISIT_ACTION = "new_visit_action";
        public static final String EDIT_VISIT_ACTION = "edit_visit_action";

        public static final int EDIT_VISIT_REQUEST_CODE = 4000;
        public static final int DELETE_VISIT_RESULT_CODE = 4010;
        public static final int VISIT_CHANGED_RESULT_CODE = 4020;

    }

    public class WorkActivityActions {

        public static final String SHOW_GENERAL_WORKS = "show_general_works";
        public static final String POST_DELETE_VISIT = "post_delete_visit";

    }

}
