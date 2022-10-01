package org.example;

import java.util.Arrays;

public class MonitorToConsole extends Thread {
    private void append(String s) {
        System.out.println(s);
    }

    @Override
    public void run(){
        while(!isInterrupted()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            App.readLock.lock();
            append(String.valueOf(System.currentTimeMillis()));
            try {
                for (var row : App.garden) {
                    append(Arrays.toString(row));
                }
            } finally {
                App.readLock.unlock();
            }
        }
    }
}
