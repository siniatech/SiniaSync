package com.siniatech.siniasync.monitor;

public class CountingProgressMonitor implements IProgressMonitor {

    private long count = 0;

    @Override
    public void report( IProgressReport msg ) {
        count++;
    }

    public long getCount() {
        return count;
    }

}
