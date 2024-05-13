package com.sdk.cpcl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Activity_Image_Download extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_image_download);


    }

}
