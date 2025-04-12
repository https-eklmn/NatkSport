package com.example.natksport;

public class Player {
    private String surname;
    private String name;
    private String patronymic;
    private String height;
    private String number;
    private String photoUrl;
    private String id;
    private String position;
    private int sportId;

    public Player() {

    }

    public Player(String id, String surname, String name, String patronymic, String height, String number, String photoUrl,String position, int sportId) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.height = height;
        this.number = number;
        this.photoUrl = photoUrl;
        this.position = position;
        this.sportId = sportId;
    }

    public String getId() { return id; }
    public String getSurname() { return surname; }
    public String getName() { return name; }
    public String getPatronymic() { return patronymic; }
    public String getHeight() { return height; }
    public String getNumber() { return number; }
    public String getPhotoUrl() { return photoUrl; }
    public String getPosition() { return position; }
    public int getSportId() {
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
    }
}