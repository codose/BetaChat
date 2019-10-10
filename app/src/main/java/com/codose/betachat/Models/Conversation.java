package com.codose.betachat.Models;

public class Conversation {
    public String seen;
    public long timestamp;

    public Conversation() {
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Conversation(String seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }
}
