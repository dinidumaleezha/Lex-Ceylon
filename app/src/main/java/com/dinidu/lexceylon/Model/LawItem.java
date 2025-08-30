package com.dinidu.lexceylon.Model;

public class LawItem {
    private String id;
    private String title;
    private String act;
    private String section;
    private Long year;      // Numeric type for Firebase
    private String content;

    public LawItem() { }

    public LawItem(String id, String title, String act, String section, Long year, String content) {
        this.id = id;
        this.title = title;
        this.act = act;
        this.section = section;
        this.year = year;
        this.content = content;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAct() { return act; }
    public void setAct(String act) { this.act = act; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Long getYear() { return year; }
    public void setYear(Long year) { this.year = year; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
