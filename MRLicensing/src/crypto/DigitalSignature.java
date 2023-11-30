/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crypto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.security.Provider;
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
import logic.FileManager;

/**
 *
 * @author Utilizador
 */
public class DigitalSignature {

    FileManager fileManager;

    public DigitalSignature() {
        fileManager = new FileManager();
    }

    public void writeCertificate(String fileName, Certificate cert) throws FileNotFoundException, IOException, CertificateEncodingException {
        File file = new File(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file); Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write(Base64.getEncoder().encodeToString(cert.getEncoded()));
            writer.write("\n-----END CERTIFICATE-----\n");
        }
    }

    public void signFileWithID(String fileToSign, String outputCertificateFile, String outputSignatureFile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, Exception {
        addProvider("SunPKCS11","CartaoCidadao");
        java.security.Provider sunPKCS11 = Security.getProvider("SunPKCS11-CartaoCidadao");
        KeyStore ks = null;
        ks = KeyStore.getInstance("PKCS11", sunPKCS11);
        ks.load(null, null);

        //Read fileToSign
        byte[] fileToSignBytes = fileManager.readFileToBytes(fileToSign);

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
        fileManager.writeToFile(signatureBytes, outputSignatureFile);
    }

    public boolean checkSignature(String originalFile, String certificateFile, String signatureFile) throws NoSuchAlgorithmException, FileNotFoundException, CertificateException, IOException, InvalidKeyException, SignatureException {
        // Read Certificate
        FileInputStream fis = new FileInputStream(certificateFile);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fis);
        fis.close();

        // Get Public Key
        PublicKey publicKey = certificate.getPublicKey();

        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(publicKey);

        byte[] signatureBytes = fileManager.readFileToBytes(signatureFile);

        byte[] messageBytes = fileManager.readFileToBytes(originalFile);

        signature.update(messageBytes);

        boolean isCorrect = signature.verify(signatureBytes);

        return isCorrect;
    }

    public void addProvider(String providerType,String providerName) throws IOException, Exception {
        
        if (Security.getProvider(providerType+"-"+providerName) == null) {
            int qProv = Security.getProviders().length;
            System.out.println(qProv);
            String javaHome = System.getProperty("java.home").replace("\\", "/");
            String pathProvFolder = javaHome + "/conf/security";
            String javaSecurity = pathProvFolder + "/java.security";
            String conf = pathProvFolder + "/PKCS11-cartao.cfg";
            
            fileManager.writeToFile(("name="+providerName+"\nlibrary=C:\\Windows\\system32\\pteidpkcs11.dll").getBytes(), conf);

            String regexToSearch = "^security\\.provider\\."+qProv+".*";
            int newp = qProv + 1;
            String newLineToAdd = "security.provider." + newp + "=" + providerType + " " + conf;

            System.out.println(pathProvFolder);
            
            try {
                // Read the file
                BufferedReader reader = new BufferedReader(new FileReader(javaSecurity));
                StringBuilder content = new StringBuilder();
                String line;

                // Search for the line with the specific regex
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                    if (line.matches(regexToSearch)) {
                        // Add a new line below the matched line
                        content.append(newLineToAdd).append(System.lineSeparator());
                    }
                }
                reader.close();

                // Write the updated content back to the file
                BufferedWriter writer = new BufferedWriter(new FileWriter(javaSecurity));
                writer.write(content.toString());
                writer.close();

                throw new Exception("Provider Update: Volte a executar a Aplicacao!!!");

            } catch (IOException e) {
                e.printStackTrace();

            } 
        }
    }
}
