package com.example.natksport;

public class User {
    private int IDUser;
    private String Familia;
    private String Imia;
    private String Otchestvo;
    private String Login;
    private String Parol;
    private String NaimenovanieRoli;
    private String Pochta;

    public User() {

    }

    public int getIDUser() {
        return IDUser;
    }

    public void setIDUser(int IDUser) {
        this.IDUser = IDUser;
    }
    public String getFamilia() {
        return Familia;
    }

    public void setFamilia(String familia) {
        this.Familia = familia;
    }

    public String getImia() {
        return Imia;
    }

    public void setImia(String imia) {
        this.Imia = imia;
    }

    public String getOtchestvo() {
        return Otchestvo;
    }

    public void setOtchestvo(String otchestvo) {
        this.Otchestvo = otchestvo;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        this.Login = login;
    }

    public String getParol() {
        return Parol;
    }

    public void setParol(String parol) {
        this.Parol = parol;
    }

    public String getNaimenovanieRoli() {
        return NaimenovanieRoli;
    }

    public void setNaimenovanieRoli(String naimenovanieRoli) {
        this.NaimenovanieRoli = naimenovanieRoli;
    }
    public String getPochta() {
        return Pochta;
    }

    public void setPochta(String pochta) {
        this.Pochta = pochta;
    }
}
