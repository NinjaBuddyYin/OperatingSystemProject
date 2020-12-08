package com.intelligentSoftwares.Trichain.batterymanager;

import android.graphics.drawable.Drawable;

public class SimpleItem {
    private Drawable appIcon;
    private String appName, pkgname;

    public SimpleItem(String appName, String  pkgname, Drawable appIcon) {
        this.appName = appName;
        this.appIcon = appIcon;
        this. pkgname =  pkgname;
    }

    public String getPkgname() {
        return pkgname;
    }

    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
