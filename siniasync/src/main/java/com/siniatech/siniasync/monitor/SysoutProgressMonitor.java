package com.siniatech.siniasync.monitor;

public class SysoutProgressMonitor implements IProgressMonitor {

    public void report( String msg ) {
        System.out.println( msg );
    }

}
