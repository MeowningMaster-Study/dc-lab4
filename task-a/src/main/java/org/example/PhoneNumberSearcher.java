package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PhoneNumberSearcher extends Thread {
    public String searchedPhoneNumber;

    public PhoneNumberSearcher(String searchedPhoneNumber) {
        this.searchedPhoneNumber = searchedPhoneNumber;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            if (App.writeLock.get()) {
                continue;
            }
            App.readLock.incrementAndGet();
            var file = new File(App.fileName);
            try {
                var scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    var row = scanner.nextLine();
                    var record = new Record(row);
                    if (record.phoneNumber.equals(searchedPhoneNumber)) {
                        System.out.printf("Found phone number %s with username %s%n", record.phoneNumber, record.userName);
                        App.readLock.decrementAndGet();
                        return;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            App.readLock.decrementAndGet();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}