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
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class AssymetricCipher {
    final String algo = "RSA";
    final String cipherMode = "ECB";
    final String padding = "PKCS1Padding";
    final int keySize = 4096;
    FileManager fileManager;
    
    public AssymetricCipher(){
        fileManager =new FileManager();
    }
    
    public void genKey(String publicKeyFileName, String privateKeyFileName) throws NoSuchAlgorithmException, IOException{
        KeyPair pair=genKeyPair();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();
        
        byte[] arrayBytesPublicKey = publicKey.getEncoded();
        byte[] arrayBytesPrivateKey = privateKey.getEncoded();
        
        fileManager.writeToFile(arrayBytesPublicKey, publicKeyFileName);
        fileManager.writeToFile(arrayBytesPrivateKey, privateKeyFileName);
    }
    
    public KeyPair genKeyPair() throws NoSuchAlgorithmException{
        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(algo);
        kpGenerator.initialize(keySize);
        KeyPair pair = kpGenerator.generateKeyPair();
        return pair;
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
    
    public byte[] generateRandomSalt(){
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return salt;
    }
    
    public byte[] protectPrivateKey(PrivateKey privateKey, String password, byte[] salt) {
        try {
            // Convert PrivateKey to PKCS#8 format
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            byte[] privateKeyBytes = privateKeySpec.getEncoded();
            
            // Generate a secret key from the password and salt
            SecretKeySpec sks = generateSecretKey(password, salt);
            
            // Initialize the cipher with the secret key and encryption mode
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);

            // Encrypt the private key
            byte[] encryptedPrivateKeyBytes = cipher.doFinal(privateKeyBytes);
            
            return encryptedPrivateKeyBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public byte[] unprotectPrivateKey(byte[] encryptedPrivateKeyBytes, String password, byte[] salt) {
        try {
            // Generate a secret key from the password and salt
            SecretKeySpec secretKey = generateSecretKey(password, salt);

            // Initialize the cipher with the secret key and decryption mode
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decrypt the private key
            byte[] decryptedPrivateKeyBytes = cipher.doFinal(encryptedPrivateKeyBytes);
            
            return decryptedPrivateKeyBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static SecretKeySpec generateSecretKey(String password, byte[] salt) throws Exception {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(keySpec).getEncoded();

        return new SecretKeySpec(keyBytes, "AES");
    }
}
