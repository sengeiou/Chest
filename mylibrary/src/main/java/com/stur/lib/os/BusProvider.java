package com.stur.lib.os;

import com.squareup.otto.Bus;

/**
 * Created by Administrator on 2016/3/4.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}