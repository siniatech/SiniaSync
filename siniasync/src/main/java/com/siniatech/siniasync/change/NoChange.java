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

import java.nio.file.Path;

public class NoChange implements IChange {

    private final Path p1;
    private final Path p2;

    public NoChange( Path p1, Path p2 ) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( p1 == null ) ? 0 : p1.hashCode() );
        result = prime * result + ( ( p2 == null ) ? 0 : p2.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        NoChange other = (NoChange) obj;
        if ( p1 == null ) {
            if ( other.p1 != null )
                return false;
        } else if ( !p1.equals( other.p1 ) )
            return false;
        if ( p2 == null ) {
            if ( other.p2 != null )
                return false;
        } else if ( !p2.equals( other.p2 ) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + "," + p1 + "," + p2 + "}";
    }

    @Override
    public void respond( IChangeContext changeContext ) {
    }

}
