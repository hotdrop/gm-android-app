package jp.hotdrop.gmapp.model;

import javax.inject.Inject;

public class MainContentStateBrokerProvider {

    private static final MainContentStateBroker BROKER = new MainContentStateBroker();

    @Inject
    public MainContentStateBrokerProvider() {
    }

    public MainContentStateBroker get() {
        return BROKER;
    }
}
