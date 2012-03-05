package com.siniatech.siniasync.change;

import java.util.ArrayList;
import java.util.List;

import com.siniatech.siniautils.fn.IResponse1;

public class ChangeCollector implements IResponse1<IChange> {

    private List<IChange> changes = new ArrayList<>();

    @Override
    public void respond( IChange change ) {
        if ( ! ( change instanceof NoChange ) ) {
            changes.add( change );
        }
    }

    public List<IChange> getChanges() {
        return changes;
    }
}
