package com.siniatech.siniasync.monitor;

public class SysoutProgressMonitor implements IProgressMonitor {

    @Override
    public void report( IProgressReport msg ) {
        System.out.println( msg.getMessage() );
    }

}
