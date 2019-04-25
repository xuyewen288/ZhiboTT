package com.xunye.zhibott.helper;

public class MessageEvent {
    private int what;
    private String message;
    private String message2;

    public MessageEvent(int what) {
        this.what = what;
    }

    public MessageEvent(int what, String message) {
        this.what = what;
        this.message = message;
    }

    public MessageEvent(int what, String message, String message2) {
        this.what = what;
        this.message = message;
        this.message2 = message2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }
}

