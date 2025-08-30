package com.dinidu.lexceylon.Model;

public class HistoryModel {
    private String aiGeneratedMessage;
    private long timestamp;
    private String userInput;

    public HistoryModel() {}

    public String getAiGeneratedMessage() {
        return aiGeneratedMessage;
    }

    public void setAiGeneratedMessage(String aiGeneratedMessage) {
        this.aiGeneratedMessage = aiGeneratedMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    // Add these helper methods for adapter usage:
    public String getTitle() {
        return userInput;  // or aiGeneratedMessage based on what you want to show as title
    }

    public String getDate() {
        // convert timestamp (long) to formatted date string
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
