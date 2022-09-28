package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserSearcher extends Thread {
    public String searchedUserName;

    public UserSearcher(String searchedUserName) {
        this.searchedUserName = searchedUserName;
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
                    if (record.userName.equals(searchedUserName)) {
                        System.out.printf("Found username %s with phone number %s%n", record.userName, record.phoneNumber);
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
