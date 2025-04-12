package com.example.natksport;

public class VidSport {
    private int idvidaSporta;
    private String naimenovanie;

    public VidSport() {
    }

    public VidSport(int idvidaSporta, String naimenovanie) {
        this.idvidaSporta = idvidaSporta;
        this.naimenovanie = naimenovanie;
    }


    public int getIDVidaSporta() {
        return idvidaSporta;
    }

    public void setIDVidaSporta(int idvidaSporta) {
        this.idvidaSporta = idvidaSporta;
    }

    public String getNaimenovanie() {
        return naimenovanie;
    }

    public void setNaimenovanie(String naimenovanie) {
        this.naimenovanie = naimenovanie;
    }
}
