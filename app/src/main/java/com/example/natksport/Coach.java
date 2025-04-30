package com.example.natksport;

public class Coach {
    private String FIO;
    private int IDTrener;
    private String Image;
    private String Opisanie;

    public Coach() {

    }

    public Coach(String FIO, int IDTrener, String Image, String Opisanie) {
        this.FIO = FIO;
        this.IDTrener = IDTrener;
        this.Image = Image;
        this.Opisanie = Opisanie;
    }


    public String getFIO() { return FIO; }
    public int getIDTrene() { return IDTrener; }
    public String getImage() { return Image; }
    public String getOpisanie() { return Opisanie; }
}
