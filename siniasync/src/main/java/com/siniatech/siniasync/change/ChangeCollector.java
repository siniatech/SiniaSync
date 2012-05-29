/*******************************************************************************
 * SiniaSync
 * Copyright (c) 2011-2 Siniatech Ltd  
 * http://www.siniatech.com/products/siniasync
 *
 * All rights reserved. This project and the accompanying materials are made 
 * available under the terms of the MIT License which can be found in the root  
 * of the project, and at http://www.opensource.org/licenses/mit-license.php
 *
 ******************************************************************************/
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
