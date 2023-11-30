/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import crypto.DigitalSignature;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.UserCard;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class teste {
    public static void main(String[] args) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException {
        
            /*FileManager fileManager=new FileManager();
            String email="teste@email.com";
            String appName="teste";
            String version="-1";
            String licenceRep="LicenseRep";
            String tempWorkingDir=licenceRep+"/TempWorkingDir";
            String tempDataFolder=tempWorkingDir+"/data";
            String tempDataJSON=tempDataFolder+"/data.json";
            Scanner sc=new Scanner(System.in);
            fileManager.licenseDataToJSONFile(new UserCard(email),new ComputerProperties(),new AppProperties(appName,version),tempDataJSON,sc);
            */
            
        
        try { 
            new DigitalSignature().addProvider("SunPKCS11","CartaoCidadao");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(Security.getProvider("SunPKCS11-CartaoCidadao").getName());
}
}
