package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App
{
    public static final String fileName = "db.txt";
    public static final AtomicInteger readLock = new AtomicInteger(0);
    public static final AtomicBoolean writeLock = new AtomicBoolean(false);

    public static void main( String[] args ) throws IOException {
        // clear file
        new FileWriter(fileName, false).close();

        var writer = new Writer(
            new Record[]{
                new Record("Andrew:4356"),
                new Record("David:5431"),
                new Record("Tomas:1524")
            }
        );
        var userSearcher = new UserSearcher("David");
        var phoneNumberSearcher = new PhoneNumberSearcher("1524");
        writer.start();
        userSearcher.start();
        phoneNumberSearcher.start();
    }
}
