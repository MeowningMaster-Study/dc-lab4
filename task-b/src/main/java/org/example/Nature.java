package org.example;

import java.util.concurrent.ThreadLocalRandom;

public class Nature extends Thread {
    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            App.writeLock.lock();
            try {
                for (int i = 0; i < App.garden.length; i += 1) {
                    var row = App.garden[i];
                    for (int k = 0; k < row.length; k += 1) {
                        row[k] = ThreadLocalRandom.current().nextBoolean();
                    }
                }
            } finally {
                App.writeLock.unlock();
            }
        }
    }
}
