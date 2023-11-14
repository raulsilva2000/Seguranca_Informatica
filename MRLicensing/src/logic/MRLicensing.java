/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

/**
 *
 * @author Miguel
 */
public class MRLicensing {

    public MRLicensing() {
    }
    
    public String askNewLicence(){
        String src="";//pwd of licenseAsk file
        //sign app
        //get app detail
        //get user details
        //get pc details
        //data to json
        
        //sign data
        //cerificado de user
        
        //all above zip folder
        
        //symetric cipher zip
        //assymetic cipher symetric key
        
        //zip folder with encripted data, encripted key(pub key ges), intial vector
        
        //return pwd of zip
        return src;
    }
    
    public boolean verifyLicense(){
        if(verifyCurrenteLicense()){
            return true;
        }
        return verifyNewLicense("");
    }
    private boolean verifyNewLicense(String pwd){
        //unzip
        if(validateLicence(pwd)){
            //unzip
            //change folder to normal pwd
            return true;
        }
        return false;
    }
    private boolean verifyCurrenteLicense(){
        //check acording with user
        
        
        return false;
    }
    
    
    private boolean validateLicence(String folder){
        //decript symetric key with user private key
        //unzip
        //decript data(future .json)
        //check sign form gestor(hash data==descript sign with public key (certificado))

        //hash app
        //get app detail
        //get user details
        //get pc details
        
        //check if data matches with first json
        
        
        return false;
    }
}
