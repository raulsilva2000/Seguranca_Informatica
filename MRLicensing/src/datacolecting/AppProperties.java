/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datacolecting;

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
    private String name;
    private String version;
    private String manufacturer;
    final String algo = "SHA-256";
    private String hash;
    private FileManager fileManager;
    
    public AppProperties(String jarFileName) throws IOException, FileNotFoundException, NoSuchAlgorithmException{
        this.jarFileName = jarFileName;
        setManifestProperties();
        setHash();
        fileManager=new FileManager();
    }
    
    public void setManifestProperties() throws IOException {
        File jf = new File("dist/LicenseLibrary.jar");
        JarFile jarFile = new JarFile(jf);
        Manifest manifest = jarFile.getManifest();
        
        if (manifest != null) {
                Attributes attributes = manifest.getMainAttributes();
                name = attributes.getValue("Implementation-Title");
                version = attributes.getValue("Implementation-Version");
                manufacturer = attributes.getValue("Implementation-Manufacturer");
        }
    }
    
    public void setHash() throws FileNotFoundException, IOException, NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] arrayBytesWithContent = fileManager.readFileToBytes("dist/"+jarFileName);
        
        FileInputStream inputStream = new FileInputStream("dist/"+jarFileName);
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

    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getHash() {
        return hash;
    }
}
