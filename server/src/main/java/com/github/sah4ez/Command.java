package com.github.sah4ez;

/**
 * Created by aleksandr on 05.03.17.
 */
public enum Command {
    UPLOAD(0),
    FIND(1),
    DOWNLOAD(2),
    DELETE(3);

    private int id;

    Command(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
