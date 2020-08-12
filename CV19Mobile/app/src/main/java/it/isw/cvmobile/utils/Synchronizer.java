package it.isw.cvmobile.utils;

import it.isw.cvmobile.utils.annotations.Completed;


@Completed
public class Synchronizer {

    private final Object synchronizer;
    private boolean satisfied;



    public Synchronizer(boolean satisfied) {
        synchronizer = new Object();
        this.satisfied = satisfied;
    }

    public void registerToSynchronizer() throws InterruptedException {
        synchronizer.wait();
    }

    public void notifyAllSynchronized() {
        synchronizer.notifyAll();
    }

    public void notifySynchronized() {
        synchronizer.notify();
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public Object getSynchronizer() {
        return synchronizer;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

}
