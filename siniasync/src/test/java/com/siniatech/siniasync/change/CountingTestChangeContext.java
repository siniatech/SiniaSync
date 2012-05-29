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

public class CountingTestChangeContext implements IChangeContext {

    private int errors = 0;
    private int successes = 0;

    @Override
    public void reportError( String string ) {
        System.err.println( string );
        errors++;
    }

    @Override
    public void reportSuccess( String string ) {
        successes++;
    }

    @Override
    public ChangeQueueingStrategy getChangeQueueingStrategy( IChange change ) {
        return change instanceof NoChange ? ChangeQueueingStrategy.doNothing : ChangeQueueingStrategy.apply;
    }

    public int getErrorCount() {
        return errors;
    }

    public int getSuccessCount() {
        return successes;
    }

}
