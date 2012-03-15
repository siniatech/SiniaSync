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
