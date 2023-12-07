/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataCollecting;

import com.google.gson.annotations.Expose;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class AppProperties {
    private String jarFileName;
    @Expose
    private String name;
    private String version;
    final String algo = "SHA-256";
    private String hash;
    private FileManager fileManager;
    
    public AppProperties(String appName,String version) throws IOException, FileNotFoundException, NoSuchAlgorithmException{
        fileManager=new FileManager();
        this.name=appName;
        this.version=version;
    }
    
    public AppProperties(String appName) throws IOException, FileNotFoundException, NoSuchAlgorithmException{
        fileManager=new FileManager();
        this.name=appName;
    }
    
    public void setHash(String jarPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException{
        this.jarFileName=jarPath;
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(jarFileName);
        
        FileInputStream inputStream = new FileInputStream(jarFileName);
        inputStream.read(arrayBytesWithContent);
        
        byte[] encodedhash = md.digest(arrayBytesWithContent);
        hash = new BigInteger(1, encodedhash).toString(16);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
    
    public String getHash() {
        return hash;
    }
}
