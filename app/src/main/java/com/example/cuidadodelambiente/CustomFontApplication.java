package com.example.cuidadodelambiente;
import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class CustomFontApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arvo-bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}