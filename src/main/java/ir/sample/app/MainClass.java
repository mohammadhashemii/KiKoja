package ir.sample.app;

import ir.appsan.sdk.APSConfig;
import ir.appsan.sdk.AppsanApplication;
import ir.sample.app.kikoja.KikojaChannel;

public class MainClass {

    public static void main(String[] args) {
        APSConfig config = new APSConfig("sbu.appsan.ir", 443, "4283BF786996E133D9F9A0B37BC067C3");
        AppsanApplication.setDebug(true);
        AppsanApplication.run(MainClass.class, args, config);
        AppsanApplication.registerChannel(new KikojaChannel());
    }
}
