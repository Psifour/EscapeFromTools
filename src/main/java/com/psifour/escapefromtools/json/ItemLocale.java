package com.psifour.escapefromtools.json;

public class ItemLocale {

    public String Name;
    public String ShortName;
    public String Description;
    public String templateID;

    public void setTemplateID(String templateID) {
        this.templateID = templateID.substring(0, templateID.length() - 5);
    }
}
