package org.example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class App
{
    public static final boolean[][] garden = new boolean[3][3];
    public static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public static final Lock writeLock = readWriteLock.writeLock();
    public static final Lock readLock = readWriteLock.readLock();

    public static void main( String[] args )
    {
        var gardener = new Gardener();
        var nature = new Nature();
        var monitorToFile = new MonitorToFile();
        var monitorToConsole = new MonitorToConsole();
        gardener.start();
        nature.start();
        monitorToFile.start();
        monitorToConsole.start();
    }
}
