/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Miguel
 */
public class MRLicensing {
    private FileManager fileManager;
    private String licenceRep;
    private String defaultLicenseFolder;

    public MRLicensing() {
        fileManager=new FileManager();
        licenceRep="/LicenseRep";
        defaultLicenseFolder=licenceRep+"/default";
    }
    
    public String askNewLicence(String userName){
        String src="/MRLic_"+userName+".zip";//pwd of licenseAsk file
        
        String tempFolder="/MRAskNewLicenseTemp";
        String tempDataFolder=tempFolder+"/data";
        fileManager.clearFolder(tempFolder);
        
        getDataJson(tempDataFolder);
        
        signAll(tempDataFolder);
        
        String dataTempZip="";
        try {
            dataTempZip=fileManager.zipToFile(tempDataFolder);
        } catch (IOException ex) {
            Logger.getLogger(MRLicensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fileManager.deleteFolder(tempDataFolder);
        
        String key=getGestorKey();
        secureComs(dataTempZip, key);
        
        fileManager.zipToFileWithDest(tempFolder,src);
        fileManager.deleteFolder(tempFolder);
        return src;
    }
    
    private boolean validateLicence(String file){
        openLicense(file);
        
        String lData=getLicenseData();
        
        fileManager.clearFolder(defaultLicenseFolder);
        
        String cData=getCurrentData();
        
        if (lData.equals(cData)){
            //++tolerancias
            return true;
        }
        
        
        return false;
    }
    
    public boolean verifyLicense(String userName){
        String licFile="/MRLic_"+userName+".zip";
        //search for lic username
        if (validateLicence(licFile)){
            return true;
        }
        
        return verifyNewLicense("");
    }
    private boolean verifyNewLicense(String pwd){
        if(validateLicence(pwd)){
            //change zip to normal pwd
            return true;
        }
        return false;
    }
    
    
    private String getData(){
        //get app detail
        //get user details
        //get pc details
        return "";
    }
    
    private String getDataJson(String folder){
        getData();
        
        //create assymetric key to license
        
        //data to json
        return "jsonObject";
    }
    
    private String getCurrentData(){
        //hash app
        getData();
        return "";
    }
    
    private String getLicenseData(){
        try {
            fileManager.readFileToBytes(defaultLicenseFolder+"/data/data.json");
        } catch (IOException ex) {
            Logger.getLogger(MRLicensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private void signAll(String folder){
        //sign app
        
        //sign data
        //cerificado de user
    }
    
    private void openLicense(String file){
        //get user private key
        
        fileManager.unzipToFileWithDest(file,defaultLicenseFolder);
        
        //decript symetric key with user private key
        
        //decript data(future .json)
        
        fileManager.unzipToFile(defaultLicenseFolder+"/data");
        
        //check sign form gestor(hash data==descript sign with public key (certificado))
    }
    
    private void secureComs(String file, String key){
        //symetric cypher
        
        //assimetric cypher of symmmetric key with gestor public key
    }
    
    private String getGestorKey() {
        return "";
    }
}
