package com.chandrachud.vanish.items;

import java.util.Date;
import java.util.Map;

public class NotificationManagerItem {

    private String type;
    private String number;
    long timestamp;

    public NotificationManagerItem()
    {

    }

    public NotificationManagerItem(String type, String number, long timestamp) {
        this.type = type;
        this.number = number;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
