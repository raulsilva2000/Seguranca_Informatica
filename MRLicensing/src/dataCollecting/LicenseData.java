/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataCollecting;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Miguel
 */
public class LicenseData {
    @Expose
    public UserCard user;
    @Expose
    public ComputerProperties pc;
    @Expose
    public AppProperties app;
    @Expose
    public String validity;

    public LicenseData(UserCard user, ComputerProperties pc, AppProperties app) {
        this.user = user;
        this.pc = pc;
        this.app = app;
    }

    public LicenseData(UserCard user, AppProperties app) {
        this.user = user;
        this.app = app;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
