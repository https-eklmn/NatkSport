package com.example.natksport;
public class Position {
    private String naimenovanie;
    private int id;
    private int idSporta;


    public Position() { }


    public Position(String naimenovanie, int id, int idSporta) {
        this.naimenovanie = naimenovanie;
        this.id = id;
        this.idSporta = idSporta;
    }

    public String getNaimenovanie() {
        return naimenovanie;
    }

    public void setNaimenovanie(String naimenovanie) {
        this.naimenovanie = naimenovanie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSporta() {
        return idSporta;
    }

    public void setIdSporta(int idSporta) {
        this.idSporta = idSporta;
    }
}

