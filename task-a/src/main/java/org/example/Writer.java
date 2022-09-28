package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Writer extends Thread {
    public Record[] records;

    public Writer(Record[] records) {
        this.records = records;
    }

    @Override
    public void run() {
        for(var record : records) {
            while(App.readLock.get() != 0) {}
            App.writeLock.set(true);
            try {
                Files.write(Paths.get(App.fileName), String.format("%s%n", record).getBytes(), StandardOpenOption.APPEND);
                System.out.printf("Appended: %s%n", record);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            App.writeLock.set(false);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
