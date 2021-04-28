package ir.sample.app.kikoja;

import ir.appsan.sdk.APSChannel;
import ir.sample.app.kikoja.services.KikojaService;

public class KikojaChannel extends APSChannel {

    public String getChannelName() {
        return "miniApp";
    }

    public void init() {
        registerService(new KikojaService(getChannelName()));
    }

}
