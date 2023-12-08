/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import crypto.DigitalSignature;
import crypto.SymmetricCipher;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.UserCard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private String distFolder;
    private String licenseRep;
    private String tempWorkingDir;
    private DigitalSignature digitalSignature;
    private String appName;
    private String version;
    private static final String EMAIL_REGEX ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    

    public MRLicensing() {
        fileManager=new FileManager();
        distFolder = (new File(MRLicensing.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath().replace("\\", "/");
        licenseRep = distFolder + "/LicenseRep";
        tempWorkingDir=licenseRep+"/TempWorkingDir";
        fileManager.createFolder(licenseRep);
        fileManager.createFolder(tempWorkingDir);
    }
    public void init(String nomeDaApp, String versao){
        digitalSignature=new DigitalSignature();
        this.appName=nomeDaApp;
        this.version=versao;
    }
    
    public boolean isRegistered(){
        return false;
    }
    
    public boolean startRegistration(){
        Scanner sc=new Scanner(System.in);
        String email;
        String aux;
        while (true){
            System.out.println("Introduza o seu email:");
            aux=sc.nextLine();
            if(isValidEmail(aux)){
                email=aux;
                break;
            }
            else{
                System.out.println("!!Email invalido!!");
            }
        }
        System.out.println("Aguarde um pouco.");
        try {
            System.out.println("O pedido de licença está no diretório:\n"+askNewLicense(email, sc));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            fileManager.clearFolder(tempWorkingDir);
        }
        sc.close();
        return false;
    }
     
    public void showLicenseInfo(){
        
    }
    
    private String askNewLicense(String email,Scanner sc) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, Exception{
        String src=licenseRep+"/MRLicReq_"+email+".zip";//pwd of licenseAsk file
        
        fileManager.clearFolder(tempWorkingDir);
        
        String tempDataFolder=tempWorkingDir+"/data";
        fileManager.createFolder(tempDataFolder);
        
        String tempDataJSON=tempDataFolder+"/data.json";
        String dataTempZip=tempWorkingDir+"/data.zip";
        
        fileManager.licenseDataToJSONFile(new UserCard(email),new ComputerProperties(),new AppProperties(appName,version),tempDataJSON);
        
        signDataJSON(tempDataJSON,tempDataFolder);
        
        fileManager.addNewKeyStore(email, sc,tempDataFolder);
        
        fileManager.zipToFileWithDest(tempDataFolder,dataTempZip);
        fileManager.deleteFolder(tempDataFolder);
                
        secureComs(dataTempZip, licenseRep+"/managerCertificate.crt");
        
        fileManager.zipToFileWithDest(tempWorkingDir,src);
        fileManager.deleteFolder(tempWorkingDir);
        return src;
    }
    
    private boolean validateLicense(String file, String email) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        openLicense(file);
        
        String lData=getLicenseData();
        
        fileManager.clearFolder(tempWorkingDir);
        
        JsonObject cData=fileManager.currentDataToJSON(file,email,appName,version);
        
        if (lData.equals(cData)){
            //++tolerancias
            return true;
        }
        
        
        return false;
    }
    
    public boolean verifyLicense(String email) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        String licFile=licenseRep+"/MRLic_"+email+".zip";
        //search for lic username
        if (validateLicense(licFile,email)){
            return true;
        }
        
        return verifyNewLicense("",email);
    }
    private boolean verifyNewLicense(String pwd,String email) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        if(validateLicense(pwd,email)){
            //change zip to normal pwd
            return true;
        }
        return false;
    }

        
    private String getLicenseData(){
        try {
            fileManager.readFileToBytes(tempWorkingDir+"/data/data.json");
        } catch (IOException ex) {
            Logger.getLogger(MRLicensing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private void signDataJSON(String jsonFile,String tempFolder) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, Exception{
        //sign json
        digitalSignature.signFileWithID(jsonFile, tempFolder+"/certicate.crt", tempFolder+"/signature.txt");
        
        
    }
    
    private void openLicense(String file){
        //get user private key
        
        //fileManager.unzipFileWithDest(file,tempWorkingDir);
        
        //decript symetric key with user private key
        
        //decript data(future .json)
        
        fileManager.unzipToFile(tempWorkingDir+"/data");
        
        //check sign form gestor(hash data==descript sign with public key (certificado))
    }
    
    private void secureComs(String zipFile, String managerCertificateFile) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, InvalidKeySpecException, KeyStoreException{
        String symmetricKeyFile=tempWorkingDir+"/sKey.txt";
        String symmetricEncryptedKeyFile=tempWorkingDir+"/sKey.crypto";
        String initialVectorFile=tempWorkingDir+"/iv.txt";
        String encryptedZipFile= tempWorkingDir+"/data.crypto";
        
        //symetric cypher
        SymmetricCipher sCipher=new SymmetricCipher();
        byte[] keyBytes=sCipher.genKey(symmetricKeyFile);
        byte[] ivBytes=sCipher.genVector(initialVectorFile);
        sCipher.cipherFile(zipFile,encryptedZipFile, keyBytes, ivBytes);
        fileManager.deleteFolder(zipFile);

        //assimetric cypher of symmmetric key with manager public key
        AssymetricCipher aCipher=new AssymetricCipher();
        aCipher.cipherFile(symmetricKeyFile, symmetricEncryptedKeyFile, managerCertificateFile);
        
        fileManager.deleteFolder(symmetricKeyFile);
    }
    
    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
