package com.siniatech.siniasync.change;

import com.siniatech.siniasync.monitor.IProgressMonitor;

public interface IChange {

    void apply( IProgressMonitor... monitors );

}
