package com.psifour.escapefromtools;

public class ItemEntry {

    private final String displayText;
    private final String key;

    public ItemEntry(String key) {
        this.key = key.split(":")[0];
        this.displayText = String.format(Constants.ITEM_DISPLAY_TEXT, key.split(":")[1]);
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
