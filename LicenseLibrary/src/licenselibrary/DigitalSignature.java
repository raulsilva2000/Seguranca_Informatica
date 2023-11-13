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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 *
 * @author Utilizador
 */
public class DigitalSignature {
    
    public void writeToFile(byte[] arrayBytes, String fileName) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(arrayBytes);
        }
    }
    
    public void writeCertificate(String fileName, Certificate cert) throws FileNotFoundException, IOException, CertificateEncodingException{
        File file = new File(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file); Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write(Base64.getEncoder().encodeToString(cert.getEncoded()));
            writer.write("\n-----END CERTIFICATE-----\n");
        }
    }
    
    public byte[] readFileToBytes(String fileName) throws FileNotFoundException, IOException{
        File file = new File(fileName);
        byte[] arrayBytesWithContent = new byte[(int)file.length()];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(arrayBytesWithContent);
        
        return arrayBytesWithContent;
    }
    
    public void signFileWithID(String fileToSign, String outputCertificateFile, String outputSignatureFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
        java.security.Provider sunPKCS11 = Security.getProvider("SunPKCS11-CartaoCidadao");
        KeyStore ks = null;
        ks = KeyStore.getInstance( "PKCS11", sunPKCS11 );
        ks.load( null, null );
        
        //Read fileToSign
        byte[] fileToSignBytes = readFileToBytes(fileToSign);
        
        Key key = ks.getKey("CITIZEN AUTHENTICATION CERTIFICATE", null);
        
        // Get certificate of public key
        Certificate cert = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        
        // Get public key
        PublicKey publicKey = cert.getPublicKey();

        // Key pair
        KeyPair keyPair = new KeyPair(publicKey, (PrivateKey) key);
        
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initSign(keyPair.getPrivate());
        signature.update(fileToSignBytes);

        byte[] signatureBytes = signature.sign();
        
        // Generate Certificate and Signature File
        writeCertificate(outputCertificateFile, cert);
        writeToFile(signatureBytes, outputSignatureFile);
    }
    
    public boolean checkSignature(String originalFile, String certificateFile, String signatureFile) throws NoSuchAlgorithmException, FileNotFoundException, CertificateException, IOException, InvalidKeyException, SignatureException{
        // Read Certificate
        FileInputStream fis = new FileInputStream(certificateFile);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fis);
        fis.close();
        
        // Get Public Key
        PublicKey publicKey = certificate.getPublicKey();
        
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(publicKey);
        
        byte[] signatureBytes = readFileToBytes(signatureFile);
        
        byte[] messageBytes = readFileToBytes(originalFile);

        signature.update(messageBytes);
        
        boolean isCorrect = signature.verify(signatureBytes);
        
        return isCorrect;
    }
}
