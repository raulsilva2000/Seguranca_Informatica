/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package licenselibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Utilizador
 */
public class SymmetricCipher {
    final String algo = "AES";
    final String cipherMode = "CBC";
    final String padding = "PKCS5Padding";
    
    public SymmetricCipher(){}
    
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
    
    public void genKey(String keyFileName) throws NoSuchAlgorithmException, IOException{
        KeyGenerator keyGen = KeyGenerator.getInstance(algo);
        keyGen.init(256);
        SecretKey originalKey = keyGen.generateKey();
        byte[] arrayBytes = originalKey.getEncoded();
        writeToFile(arrayBytes, keyFileName);
    }
    
    public void genVector(String vectorFileName) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException{
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[Cipher.getInstance(algo).getBlockSize()];
        random.nextBytes(iv);
        writeToFile(iv, vectorFileName);
    }
    
    public void cipherFile(String fileToCipher, String encryptedFile, String keyFile, String vectorFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{   
        byte[] arrayBytesWithContent = readFileToBytes(fileToCipher);
        byte[] arrayBytesKey = readFileToBytes(keyFile);
        byte[] arrayBytesVector = readFileToBytes(vectorFile);
        
        SecretKey key = new SecretKeySpec(arrayBytesKey, 0 , arrayBytesKey.length, algo);
        Cipher cipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        IvParameterSpec iv = new IvParameterSpec(arrayBytesVector);

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = cipher.doFinal(arrayBytesWithContent);
        writeToFile(encryptedData, encryptedFile);
    }
    
    public void decipherFile(String fileToDecipher, String decryptedFile, String keyFile, String vectorFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        byte[] arrayBytesEncrypted = readFileToBytes(fileToDecipher);
        byte[] arrayBytesKey = readFileToBytes(keyFile);
        byte[] arrayBytesVector = readFileToBytes(vectorFile);
        
        SecretKey key = new SecretKeySpec(arrayBytesKey, 0 ,arrayBytesKey.length, algo);
        Cipher cipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        IvParameterSpec iv = new IvParameterSpec(arrayBytesVector);

        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = cipher.doFinal(arrayBytesEncrypted);
        writeToFile(decryptedData, decryptedFile);
    }
}
