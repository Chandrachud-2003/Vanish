package com.chandrachud.vanish.items;

public class modelContact {

    public static final int HEADING = 0;
    public static final int DATA = 1;

    private int type;
    private contactHeadingItem headingItem;
    private contactDataItem dataItem;

    public modelContact(int type, contactHeadingItem headingItem, contactDataItem dataItem) {
        this.type = type;
        this.headingItem = headingItem;
        this.dataItem = dataItem;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public contactHeadingItem getHeadingItem() {
        return headingItem;
    }

    public void setHeadingItem(contactHeadingItem headingItem) {
        this.headingItem = headingItem;
    }

    public contactDataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(contactDataItem dataItem) {
        this.dataItem = dataItem;
    }
}
