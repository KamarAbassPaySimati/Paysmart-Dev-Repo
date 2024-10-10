package com.afrimax.paysimati;

import android.os.Bundle;
import org.junit.runner.RunWith;
import java.io.File;
import io.cucumber.android.runner.CucumberAndroidJUnitRunner;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "features", glue = "com.afrimax.paysimati", monochrome = true)
public class TestJunitRunner extends CucumberAndroidJUnitRunner {
    @Override
    public void onCreate(final Bundle bundle) {
        bundle.putString("plugin", getPluginConfigurationString()); // we programmatically create the plugin configuration
        //it crashes on Android R without it
        new File(getAbsoluteFilesPath()).mkdirs();

        super.onCreate(bundle);
    }

    private String getPluginConfigurationString() {
        String cucumber = "cucumber";
        String separator = "--";
        return "junit:" + getCucumberXml(cucumber) + separator + "html:" + getCucumberHtml(cucumber);
    }

    private String getCucumberHtml(String cucumber) {
        return getAbsoluteFilesPath() + "/" + cucumber + ".html";
    }

    private String getCucumberXml(String cucumber) {
        return getAbsoluteFilesPath() + "/" + cucumber + ".xml";
    }

    private String getAbsoluteFilesPath() {

        //sdcard/Android/data/
        File directory = getTargetContext().getExternalFilesDir(null);
        return new File(directory, "reports").getAbsolutePath();
    }

}
