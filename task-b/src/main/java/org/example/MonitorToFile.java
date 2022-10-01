package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class MonitorToFile extends Thread {
    private void append(String s) {
        try {
            Files.write(Paths.get("logs.txt"), String.format("%s%n", s).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
