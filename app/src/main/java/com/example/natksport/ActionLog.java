package com.example.natksport;

public class ActionLog {
    private String playerId;
    private String matchId;
    private String action;
    private String opisanie;

    public ActionLog() {
    }

    public ActionLog(String playerId, String matchId, String action, String opisanie) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.action = action;
        this.opisanie = opisanie;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getAction() {
        return action;
    }

    public String getopisanie() {
        return opisanie;
    }
}