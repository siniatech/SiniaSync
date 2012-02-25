package com.siniatech.siniasync.monitor;

public class CountingProgressMonitor implements IProgressMonitor {

    private long count = 0;
    
    public void report( String msg ) {
        count++;
    }
    
    public long getCount() {
        return count;
    }

}
