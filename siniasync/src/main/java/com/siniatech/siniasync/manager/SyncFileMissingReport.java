package com.siniatech.siniasync.manager;

import java.nio.file.Path;

import com.siniatech.siniasync.monitor.IProgressReport;

public class SyncFileMissingReport implements IProgressReport {

    private final Path p1;
    private final Path p2;

    public SyncFileMissingReport( Path p1, Path p2 ) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String getMessage() {
        return "File " + p1 + " is not present in " + p2 + ".";
    }

}
