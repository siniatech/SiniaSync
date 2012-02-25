package com.siniatech.siniasync.change;

import java.nio.file.Path;

import com.siniatech.siniasync.monitor.IProgressMonitor;


public class FileContentsChange implements IChange {

    private final Path p1;
    private final Path p2;

    public FileContentsChange( Path p1, Path p2 ) {
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
        FileContentsChange other = (FileContentsChange) obj;
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

    public void apply( IProgressMonitor... monitors ) {
        // TODO Auto-generated method stub
        
    }

}
