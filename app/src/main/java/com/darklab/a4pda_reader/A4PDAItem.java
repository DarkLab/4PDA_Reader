package com.darklab.a4pda_reader;

/**
 * Created by aleksandrlihovidov on 11.01.17.
 */

public class A4PDAItem {
    public final String title;
    public final String link;
    public final String description;

    public A4PDAItem(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", title, link, description);
    }
}
