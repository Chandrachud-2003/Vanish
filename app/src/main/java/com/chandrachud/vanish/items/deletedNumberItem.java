package com.chandrachud.vanish.items;

import java.util.Date;

public class deletedNumberItem {

    private int profilePic;
    private String number;
    private long timestamp;

    public deletedNumberItem()
    {

    }

    public deletedNumberItem(int profilePic, String number, long timestamp) {
        this.profilePic = profilePic;
        this.number = number;
        this.timestamp = timestamp;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
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
