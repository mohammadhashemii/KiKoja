package main.java.ir.sample.app.kikoja;

import ir.appsan.sdk.APSChannel;
import ir.sample.app.highway.services.KikojaService;

public class KikojaChannel extends APSChannel {

    public String getChannelName() {
        return "miniApp";
    }

    public void init() {
        registerService(new KikojaService(getChannelName()));
    }

}
