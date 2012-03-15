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
