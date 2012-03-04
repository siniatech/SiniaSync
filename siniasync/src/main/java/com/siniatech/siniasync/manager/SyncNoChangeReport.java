package com.siniatech.siniasync.manager;

import java.nio.file.Path;

import com.siniatech.siniasync.monitor.IProgressReport;

public class SyncNoChangeReport implements IProgressReport {

    private final Path p1;
    private final Path p2;

    public SyncNoChangeReport( Path p1, Path p2 ) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String getMessage() {
        return "Compared " + p1 + " and " + p2 + ": no change.";
    }

}
