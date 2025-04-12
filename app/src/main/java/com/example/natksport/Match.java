package com.example.natksport;

public class Match {
    private String id;
    private String description;
    private int sportId;

    public Match() {

    }

    public Match(String id, String description, int sportId) {
        this.id = id;
        this.description = description;
        this.sportId = sportId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSportId() { // Геттер для sportId
        return sportId;
    }

    public void setSportId(int sportId) { // Сеттер для sportId
        this.sportId = sportId;
    }
}
