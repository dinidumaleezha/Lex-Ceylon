package com.dinidu.lexceylon.Model;

public class ChatMessage {
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private String message;
    private int type;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
