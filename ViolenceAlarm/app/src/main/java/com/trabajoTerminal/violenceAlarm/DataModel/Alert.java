package com.trabajoTerminal.violenceAlarm.DataModel;

public class Alert {
    String message;
    boolean alertFlag;

    public Alert() {
    }

    public Alert(String message, boolean alertFlag) {
        this.message = message;
        this.alertFlag = alertFlag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAlertFlag() {
        return alertFlag;
    }

    public void setAlertFlag(boolean alertFlag) {
        this.alertFlag = alertFlag;
    }
}
