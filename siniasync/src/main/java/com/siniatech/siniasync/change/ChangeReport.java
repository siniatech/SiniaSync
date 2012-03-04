package com.siniatech.siniasync.change;

import com.siniatech.siniasync.monitor.IProgressReport;

public class ChangeReport implements IProgressReport {

    private final String msg;

    public ChangeReport( String msg ) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

}
