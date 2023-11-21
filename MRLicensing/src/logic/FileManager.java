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
}
