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
import crypto.UserCard;
import datacolecting.AppProperties;
import datacolecting.ComputerProperties;
import java.io.FileWriter;

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
    
    public void licenseDataToJSON(UserCard user, ComputerProperties pc, AppProperties app, String outputFile) throws IOException{
        Gson gson = new Gson();

        JsonObject licenseJsonObject = new JsonObject();
        licenseJsonObject.add("user", gson.toJsonTree(user));
        licenseJsonObject.add("pc", gson.toJsonTree(pc));
        licenseJsonObject.add("app", gson.toJsonTree(app));

        FileWriter writer = new FileWriter(outputFile);
        gson.toJson(licenseJsonObject, writer);
    }
}
