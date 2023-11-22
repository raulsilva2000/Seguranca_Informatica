/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import dataCollecting.UserCard;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class FileManager {

    public FileManager() {
    }
    
    public void writeToFile(byte[] arrayBytes, String fileName) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(arrayBytes);
        outputStream.close();
    }
    
    public byte[] readFileToBytes(String fileName) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        byte[] arrayBytesWithContent = new byte[(int)file.length()];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(arrayBytesWithContent);
        
        return arrayBytesWithContent;
    }
    
    public byte[] zip(String folder) throws IOException{
        byte[] inputBytes=readFileToBytes(folder);
        //zip
        byte[] outputBytes=inputBytes;//change this
        return outputBytes;
    }
    
    public byte[] unzip(String file) throws IOException{
        byte[] inputBytes=readFileToBytes(file);
        //unzip
        byte[] outputBytes=inputBytes;//change this
        return outputBytes;
    }
    
    public String zipToFile(String folder) throws IOException{
        String fileZip=folder;
        //write a zip 
        return fileZip;
    }
    
    public String unzipToFile(String filezip){
        String folder=filezip;
        //write the folder from zip
        return folder;
    }
    public void clearFolder(String folder){
        
    }
    
    public void deleteFolder(String folder){
        
    }

    public void zipToFileWithDest(String folder, String endFile) {
        
    }

    public void unzipToFileWithDest(String file, String endFolder) {
        
    }
    
    public void licenseDataToJSONFile(UserCard user, ComputerProperties pc, AppProperties app, String outputFile) throws IOException, NoSuchAlgorithmException{
        Gson gson = new Gson();
        
        JsonObject licenseJsonObject = new JsonObject();
        
        licenseDataToJSON(gson, licenseJsonObject, user, pc, app);
        
        //gerar par de chaves e adicionar ao json a chave publica
        AssymetricCipher assymCip=new AssymetricCipher();
        KeyPair keyPair=assymCip.genKeyPair();
        
        //tratar da chave privada (password ou ...)
        
        licenseJsonObject.add("key", gson.toJsonTree(keyPair.getPublic().getEncoded()));

        FileWriter writer = new FileWriter(outputFile);
        gson.toJson(licenseJsonObject, writer);
    }
    
    public void licenseDataToJSON(Gson gson,JsonObject licenseJsonObject,UserCard user, ComputerProperties pc, AppProperties app){
        
        licenseJsonObject.add("user", gson.toJsonTree(user));
        licenseJsonObject.add("pc", gson.toJsonTree(pc));
        licenseJsonObject.add("app", gson.toJsonTree(app));
    }
    
    public JsonObject currentDataToJSON(String jarAppFile) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException{
        Gson gson = new Gson();
        
        JsonObject licenseJsonObject = new JsonObject();
        licenseDataToJSON(gson, licenseJsonObject, new UserCard(), new ComputerProperties(), new AppProperties(jarAppFile));
        
        return licenseJsonObject;
    }
}
