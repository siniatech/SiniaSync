package com.siniatech.siniasync.change;

public interface IChangeContext {

    ChangeQueueingStrategy getChangeQueueingStrategy( IChange change);

    void reportError( String string );

    void reportSuccess( String string );

}
