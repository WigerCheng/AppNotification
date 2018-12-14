package wiger.appnotification.model;

import java.io.Serializable;

/**
 * 提醒
 */
public class Remind implements Serializable {
    private int Id;
    private String appName;
    private String msg;
    private String appPackageName;

    public Remind() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMsg() {
        if (msg == null) return "NULL";
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }
}
