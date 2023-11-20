package com.zeeyeh.multicurrency.entity;

public class Currency {
    private long id;
    private long cid;
    private String name;

    public Currency(long id, long cid, String name) {
        this.id = id;
        this.cid = cid;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Currency setId(long id) {
        this.id = id;
        return this;
    }

    public long getCid() {
        return cid;
    }

    public Currency setCid(long cid) {
        this.cid = cid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Currency setName(String name) {
        this.name = name;
        return this;
    }
}
