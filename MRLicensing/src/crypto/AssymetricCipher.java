/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crypto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
import logic.FileManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

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
    private KeyStore keyStore;
    
    public AssymetricCipher() throws KeyStoreException{
        fileManager =new FileManager();
        keyStore = KeyStore.getInstance("JKS");
    }
    
    public void genKeyStore(KeyPair keyPair, String keyStorePassword, String alias, String keyStoreFileName) throws NoSuchAlgorithmException, IOException, Exception{
        // Generate a self-signed X.509 certificate
        X509Certificate selfSignedCertificate = generateSelfSignedCertificate(keyPair);

        // Create a keystore
        keyStore.load(null, null); // Initialize an empty keystore

        // Add the key pair to the keystore
        char[] password = keyStorePassword.toCharArray(); // Change this password
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{selfSignedCertificate});
        
        // Save the keystore to a file
        try (FileOutputStream fos = new FileOutputStream(keyStoreFileName + ".jks")) {
            keyStore.store(fos, password);
        }
    }
    
    public KeyPair genKeyPair() throws NoSuchAlgorithmException{
        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(algo);
        kpGenerator.initialize(keySize);
        KeyPair pair = kpGenerator.generateKeyPair();
        return pair;
    }
    
    public void cipherFile(String fileToCipher, String encryptedFile, String certificateFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, CertificateException{
        
        FileInputStream fis = new FileInputStream(certificateFile);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fis);
        fis.close();
        
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(fileToCipher);
        
        
        PublicKey publicKey = certificate.getPublicKey();
        
        Cipher encryptCipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encryptedData = encryptCipher.doFinal(arrayBytesWithContent);
        fileManager.writeToFile(encryptedData, encryptedFile);
    }
    
    public void decipherFile(String fileToDecipher, String decryptedFile, String keyStoreFile, String keyStorePassword, String alias) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        this.keyStore.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());
        byte[] arrayBytesWithContent = fileManager.readFileToBytes(fileToDecipher);
        
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
        
        Cipher decryptCipher = Cipher.getInstance(algo+"/"+cipherMode+"/"+padding);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] decryptedData = decryptCipher.doFinal(arrayBytesWithContent);
        fileManager.writeToFile(decryptedData, decryptedFile);
    }
    
    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair) throws Exception {
        // Use Bouncy Castle library for X.509 certificate generation
        Security.addProvider(new BouncyCastleProvider());
        
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal subjectName = new X500Principal("CN=SelfSignedCert");

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(subjectName);
        certGen.setIssuerDN(subjectName); // Self-signed
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10L * 365L * 24L * 60L * 60L * 1000L)); // Valid for 10 year
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        return certGen.generate(keyPair.getPrivate(), "BC");
    }
    public void exportCertificateFromKeyStore(String keyStoreFile, String keyStorePassword, String alias, String certificateFileName) throws KeyStoreException, FileNotFoundException, IOException, CertificateEncodingException, NoSuchAlgorithmException, CertificateException{
        FileInputStream fis = new FileInputStream(keyStoreFile + ".jks");
        keyStore.load(fis, keyStorePassword.toCharArray());

        // Get the certificate
        Certificate cert = keyStore.getCertificate(alias);

        // Save the certificate to a file
        fileManager.writeToFile(cert.getEncoded(), certificateFileName + ".crt");
    }
}
