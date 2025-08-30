package com.dinidu.lexceylon.Model;

public class History {
    private String userInput;
    private String aiGeneratedMessage;
    private long timestamp;

    public History() { }

    public History(String userInput, String aiGeneratedMessage, long timestamp) {
        this.userInput = userInput;
        this.aiGeneratedMessage = aiGeneratedMessage;
        this.timestamp = timestamp;
    }

    public String getUserInput() {
        return userInput;
    }

    public String getAiGeneratedMessage() {
        return aiGeneratedMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
