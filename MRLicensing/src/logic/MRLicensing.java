/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import crypto.DigitalSignature;
import crypto.SymmetricCipher;
import datacolecting.AppProperties;
import datacolecting.ComputerProperties;
import datacolecting.UserCard;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class MRLicensing {
    private FileManager fileManager;
    private String licenceRep;
    private String defaultLicenseFolder;
    private DigitalSignature digitalSignature;

    public MRLicensing() {
        fileManager=new FileManager();
        licenceRep="/LicenseRep";
        defaultLicenseFolder=licenceRep+"/default";
        digitalSignature=new DigitalSignature();
    }
    
    public String askNewLicence(String userName) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException{
        String src="/MRLic_"+userName+".zip";//pwd of licenseAsk file
        
        String tempFolder="/MRAskNewLicenseTemp";
        String tempDataJSON=tempFolder+"/data.json";
        fileManager.clearFolder(tempFolder);
        
        fileManager.licenseDataToJSONFile(new UserCard(),new ComputerProperties(),new AppProperties(""),tempDataJSON);
        
        signDataJSON(tempDataJSON,tempFolder);
        
        String dataTempZip="";
        try {
            dataTempZip=fileManager.zipToFile(tempDataJSON);
        } catch (IOException ex) {
            Logger.getLogger(MRLicensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fileManager.deleteFolder(tempDataJSON);
                
        secureComs(dataTempZip, "certificate_manager");
        
        fileManager.zipToFileWithDest(tempFolder,src);
        fileManager.deleteFolder(tempFolder);
        return src;
    }
    
    private boolean validateLicence(String file) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        openLicense(file);
        
        String lData=getLicenseData();
        
        fileManager.clearFolder(defaultLicenseFolder);
        
        JsonObject cData=fileManager.currentDataToJSON(file);
        
        if (lData.equals(cData)){
            //++tolerancias
            return true;
        }
        
        
        return false;
    }
    
    public boolean verifyLicense(String userName) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        String licFile="/MRLic_"+userName+".zip";
        //search for lic username
        if (validateLicence(licFile)){
            return true;
        }
        
        return verifyNewLicense("");
    }
    private boolean verifyNewLicense(String pwd) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        if(validateLicence(pwd)){
            //change zip to normal pwd
            return true;
        }
        return false;
    }

        
    private String getLicenseData(){
        try {
            fileManager.readFileToBytes(defaultLicenseFolder+"/data/data.json");
        } catch (IOException ex) {
            Logger.getLogger(MRLicensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private void signDataJSON(String jsonFile,String tempFolder) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
        //sign json
        digitalSignature.signFileWithID(jsonFile, tempFolder+"/certicate_userCard", tempFolder+"/signature_dataJSON_user");
        
        
    }
    
    private void openLicense(String file){
        //get user private key
        
        fileManager.unzipToFileWithDest(file,defaultLicenseFolder);
        
        //decript symetric key with user private key
        
        //decript data(future .json)
        
        fileManager.unzipToFile(defaultLicenseFolder+"/data");
        
        //check sign form gestor(hash data==descript sign with public key (certificado))
    }
    
    private void secureComs(String file, String certificateFile) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, InvalidKeySpecException{
        String symmetricKeyFile="symmetric_key";
        String initialVectorFile="ini_vec";
        String encryptedZipFile= "data.crypto";
        
        //symetric cypher
        SymmetricCipher sCipher=new SymmetricCipher();
        sCipher.genKey(symmetricKeyFile);
        sCipher.genVector(initialVectorFile);
        sCipher.cipherFile(file,encryptedZipFile, symmetricKeyFile, initialVectorFile);
        
        
        //assimetric cypher of symmmetric key with manager public key
        AssymetricCipher aCipher=new AssymetricCipher();
        aCipher.cipherFile(symmetricKeyFile+".txt", symmetricKeyFile+".crypto", certificateFile);
        fileManager.deleteFolder(symmetricKeyFile+".txt");
    }
}
