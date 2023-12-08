/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataCollecting;

import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class ComputerProperties {
    @Expose
    private int numberOfCPUs;
    @Expose
    private String macAddress;
    private FileManager fileManager;
    
    public ComputerProperties() throws IOException{
        setNumberOfCPUs();
        setMacAddress();
        fileManager = new FileManager();
    }
    
    public ComputerProperties(String numberOfCPUs,String macAddress){
        this.numberOfCPUs=Integer.getInteger(numberOfCPUs);
        this.macAddress=macAddress;
    }
    private void setNumberOfCPUs() {
        numberOfCPUs = Runtime.getRuntime().availableProcessors();
    }
    
    public void setMacAddress() throws SocketException{
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface firstInterface = null;
        StringBuilder macAddressValue = new StringBuilder();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            byte[] mac = networkInterface.getHardwareAddress();

            if (mac != null) {
                firstInterface = networkInterface;
                break;  // Stop after finding the first non-null MAC address
            }
        }

        if (firstInterface != null) {
            byte[] mac = firstInterface.getHardwareAddress();
            for (int i = 0; i < mac.length; i++) {
                macAddressValue.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }
        }
        
        macAddress = macAddressValue.toString();
    }

    public int getNumberOfCPUs() {
        return numberOfCPUs;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
}
