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

public class SimpleTestChangeContext implements IChangeContext {

    public static IChangeContext simpleTestChangeContext = new SimpleTestChangeContext();

    @Override
    public void reportError( String string ) {
        System.err.println( string );
    }

    @Override
    public void reportSuccess( String string ) {
    }

    @Override
    public ChangeQueueingStrategy getChangeQueueingStrategy( IChange change ) {
        return change instanceof NoChange ? ChangeQueueingStrategy.doNothing : ChangeQueueingStrategy.apply;
    }

}
