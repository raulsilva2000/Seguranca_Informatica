/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crypto;

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
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class SymmetricCipher {
    private final String algo = "AES";
    private final String cipherMode = "CBC";
    private final String padding = "PKCS5Padding";
    private FileManager fileManager;
    
    public SymmetricCipher(){
        fileManager=new FileManager();
    }
   
    public byte[] genKey(String keyFileName) throws NoSuchAlgorithmException, IOException{
        KeyGenerator keyGen = KeyGenerator.getInstance(algo);
        keyGen.init(256);
        SecretKey originalKey = keyGen.generateKey();
        byte[] arrayBytes = originalKey.getEncoded();
        fileManager.writeToFile(arrayBytes, keyFileName);
        return arrayBytes;
    }
    
    public byte[] genVector(String vectorFileName) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException{
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[Cipher.getInstance(algo).getBlockSize()];
        random.nextBytes(iv);
        fileManager.writeToFile(iv, vectorFileName);
        return iv;
    }
    
    public void cipherFile(String fileToCipher, String encryptedFile, byte[] arrayBytesKey, byte[] arrayBytesVector) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{   
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(fileToCipher);
        
        SecretKey key = new SecretKeySpec(arrayBytesKey, 0 , arrayBytesKey.length, algo);
        Cipher cipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        IvParameterSpec iv = new IvParameterSpec(arrayBytesVector);

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedData = cipher.doFinal(arrayBytesWithContent);
        fileManager.writeToFile(encryptedData, encryptedFile);
    }
    
    public void decipherFile(String fileToDecipher, String decryptedFile, String keyFile, String vectorFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        byte[] arrayBytesEncrypted = fileManager.readFileToBytes(fileToDecipher);
        byte[] arrayBytesKey = fileManager.readFileToBytes(keyFile);
        byte[] arrayBytesVector = fileManager.readFileToBytes(vectorFile);
        
        SecretKey key = new SecretKeySpec(arrayBytesKey, 0 ,arrayBytesKey.length, algo);
        Cipher cipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        IvParameterSpec iv = new IvParameterSpec(arrayBytesVector);

        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedData = cipher.doFinal(arrayBytesEncrypted);
        fileManager.writeToFile(decryptedData, decryptedFile);
    }
}
