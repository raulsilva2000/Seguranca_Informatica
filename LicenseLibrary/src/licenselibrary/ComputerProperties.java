/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package licenselibrary;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

/**
 *
 * @author Utilizador
 */
public class ComputerProperties {
    private String operatingSystem;
    private String macAddress;
    
    public ComputerProperties() throws IOException{
        setOperatingSystem();
        setMacAddress();
    }
    
    public void setOperatingSystem(){
        operatingSystem = System.getProperty("os.name");
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

    public String getMacAddress() {
        return macAddress;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
    
}
