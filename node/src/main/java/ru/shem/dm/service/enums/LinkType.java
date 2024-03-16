package ru.shem.dm.service.enums;

public enum LinkType {
    GET_DOC("file/getDoc"),
    GET_PHOTO("file/getPhoto");
    private final String link;

    LinkType(String link) {
        this.link = link;
    }

    @Override
    public String toString(){
        return link;
    }
}
