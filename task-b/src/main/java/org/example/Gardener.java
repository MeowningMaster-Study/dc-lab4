package org.example;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Gardener extends Thread {
    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            var toWater = new ArrayList<int[]>();
            App.readLock.lock();
            try {
                for (int i = 0; i < App.garden.length; i += 1) {
                    var row = App.garden[i];
                    for (int k = 0; k < row.length; k += 1) {
                        var plant = row[k];
                        if (plant) {
                            toWater.add(new int[]{i, k});
                        }
                    }
                }
            } finally {
                App.readLock.unlock();
            }
            if (!toWater.isEmpty()) {
                App.writeLock.lock();
                try {
                    for (var p : toWater) {
                        App.garden[p[0]][p[1]] = false;
                    }
                } finally {
                    App.writeLock.unlock();
                }
            }
        }
    }
}
