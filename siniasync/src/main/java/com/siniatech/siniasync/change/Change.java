package com.siniatech.siniasync.change;

import com.siniatech.siniasync.monitor.IProgressMonitor;

public abstract class Change implements IChange {

    public void report( String msg, IProgressMonitor... monitors ) {
        for ( IProgressMonitor progressMonitor : monitors ) {
            progressMonitor.report( new ChangeReport(msg) );
        }
    }
}
