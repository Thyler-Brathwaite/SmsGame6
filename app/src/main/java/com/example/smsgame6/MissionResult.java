package com.example.smsgame6;

import java.io.Serializable;

public class MissionResult implements Serializable {
    public String mission;
    public String reply;
    public boolean success;

    // Setters
    public void setMission(String s){
        this.mission = s;
    }

    public void setReply(String s){
        this.reply = s;
    }

    public void setSuccess(boolean s){
        this.success = s;
    }

    // Getters
    public String getMission() {
        return mission;
    }

    public String getReply() {
        return reply;
    }

    public boolean isSuccess() {
        return success;
    }
}
