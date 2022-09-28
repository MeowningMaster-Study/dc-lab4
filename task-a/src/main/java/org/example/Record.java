package org.example;

public class Record {
    String userName;
    String phoneNumber;

    public Record(String row) {
        var parts = row.split(":");
        userName = parts[0];
        phoneNumber = parts[1];
    }

    public String toString() {
        return String.format("%s:%s", userName, phoneNumber);
    }
}
