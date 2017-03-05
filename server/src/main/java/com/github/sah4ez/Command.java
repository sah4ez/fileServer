package com.github.sah4ez;

/**
 * Created by aleksandr on 05.03.17.
 */
public enum Command {
    UPLOAD("UPLOAD"),
    FIND("FIND"),
    DOWNLOAD("DOWNLOAD"),
    DELETE("DELETED");

    private String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
