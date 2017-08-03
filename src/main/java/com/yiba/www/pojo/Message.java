package com.yiba.www.pojo;

import java.io.Serializable;

public class Message implements Serializable {

    private String key;
    private Object message;

    public Message() {
    }

    public Message(String key, Object message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
