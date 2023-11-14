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
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class AssymetricCipher {
    final String algo = "RSA";
    final String cipherMode = "ECB";
    final String padding = "NoPadding";
    final int keySize = 4096;
    FileManager fileManager;
    
    public AssymetricCipher(){
        fileManager =new FileManager();
    }
    
    
    public void genKey(String publicKeyFileName, String privateKeyFileName) throws NoSuchAlgorithmException, IOException{
        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(algo);
        kpGenerator.initialize(keySize);
        KeyPair pair = kpGenerator.generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();
        
        byte[] arrayBytesPublicKey = publicKey.getEncoded();
        byte[] arrayBytesPrivateKey = privateKey.getEncoded();
        
        fileManager.writeToFile(arrayBytesPublicKey, publicKeyFileName);
        fileManager.writeToFile(arrayBytesPrivateKey, privateKeyFileName);
    }
    
    public void cipherFile(String fileToCipher, String encryptedFile, String publicKeyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        byte[] arrayBytesPublicKey = fileManager.readFileToBytes(publicKeyFile);
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(fileToCipher);
        
        KeyFactory keyFactory = KeyFactory.getInstance(algo);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(arrayBytesPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        
        Cipher encryptCipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encryptedData = encryptCipher.doFinal(arrayBytesWithContent);
        fileManager.writeToFile(encryptedData, encryptedFile);
    }
    
    public void decipherFile(String fileToDecipher, String decryptedFile, String privateKeyFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException{
        byte[] arrayBytesPrivateKey = fileManager.readFileToBytes(privateKeyFile);
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(fileToDecipher);
        
        KeyFactory keyFactory = KeyFactory.getInstance(algo);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(arrayBytesPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        
        Cipher decryptCipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] decryptedData = decryptCipher.doFinal(arrayBytesWithContent);
        fileManager.writeToFile(decryptedData, decryptedFile);
    }
}
