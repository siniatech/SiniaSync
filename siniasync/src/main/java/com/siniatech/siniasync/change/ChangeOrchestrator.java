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

import java.util.concurrent.ExecutorService;

import com.siniatech.siniautils.fn.IResponse0;
import com.siniatech.siniautils.orchestration.BackgroundThreadActionQueue;

public class ChangeOrchestrator extends BackgroundThreadActionQueue<IChange> {

    private final IChangeContext changeContext;

    public ChangeOrchestrator( ExecutorService threadPool, IChangeContext changeContext ) {
        super( threadPool );
        this.changeContext = changeContext;
    }

    @Override
    protected void onInterruption( InterruptedException e ) {
        e.printStackTrace();
    }

    @Override
    public void respond( IChange change ) {
        switch ( changeContext.getChangeQueueingStrategy(change) ) {
            case apply :
                addActionToQueue( change );
                break;
            case doNothing :
                break;
            case queryUser :
                break;
        }
    }

    public void setSynchComplete( final IResponse0 atEnd ) {
        addActionToQueue( new IChange() {
            @Override
            public void respond( IChangeContext t ) {
                atEnd.respond();
            }
        } );
    }

    @Override
    protected void executeAction( IChange change ) {
        change.respond( changeContext );
    }

}
