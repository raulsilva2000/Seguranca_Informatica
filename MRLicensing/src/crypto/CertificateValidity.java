/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author Utilizador
 */
public class CertificateValidity {
    private CertPath cp;
    private KeyStore trustAnchorKeyStore;
    private X509Certificate userCertificate;
    private final CertificateFactory certificateFactory;
    
    public CertificateValidity(String userCertificate) throws CertificateException, IOException{
        cp = null;
        trustAnchorKeyStore = null;
        certificateFactory = CertificateFactory.getInstance("X.509");
        setUserCertificate(userCertificate);
    }

    private void setUserCertificate(String userCertificate) throws FileNotFoundException, CertificateException, IOException {
        // Read User Certificate
        FileInputStream fis = new FileInputStream(userCertificate);
        this.userCertificate = (X509Certificate) certificateFactory.generateCertificate(fis);
        fis.close();
    }
    
    private boolean isCertificateTimeValid() {
        try {
            userCertificate.checkValidity();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void buildCertificationPath(String govTrustAnchor, String intermediateCertificates) throws FileNotFoundException, CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException, CertPathBuilderException{
        //defines the end-user certificate as a selector
        X509CertSelector cs = new X509CertSelector();
        cs.setCertificate(this.userCertificate);
        //Create an object to build the certification path
        CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
        
        // Create a KeyStore with the Gov Trust Anchor
        trustAnchorKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustAnchorKeyStore.load(null, null); // Initialize the keystore
        FileInputStream fis = new FileInputStream(govTrustAnchor); // Read the Gov Trust Anchor
        X509Certificate trustAnchorCertificate = (X509Certificate) certificateFactory.generateCertificate(fis);
        fis.close();
        trustAnchorKeyStore.setCertificateEntry(trustAnchorCertificate.getSubjectX500Principal().getName(), trustAnchorCertificate);

        //Define the parameters to build the certification path and provide the Trust anchor
        //certificates (trustAnchors) and the end user certificate (cs)
        PKIXBuilderParameters pkixBParams = new PKIXBuilderParameters(trustAnchorKeyStore, cs);
        pkixBParams.setRevocationEnabled(false); //No revocation check
        
        // Get Intermediate Certificates
        File intermediateCertFolder = new File(intermediateCertificates);
        File[] listOfIntermediateCertFiles = intermediateCertFolder.listFiles();
        
        List<X509Certificate> iCerts = new ArrayList<>();
        
        for(File file : listOfIntermediateCertFiles){
            fis = new FileInputStream(file);
            X509Certificate intermediateCertificate = (X509Certificate) certificateFactory.generateCertificate(fis);
            iCerts.add(intermediateCertificate);
            fis.close();
        }
        
        //Provide the intermediate certificates (iCerts)
        CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(iCerts);
        CertStore store = CertStore.getInstance("Collection", ccsp);
        pkixBParams.addCertStore(store);
        
        //Build the certification path
        CertPathBuilderResult cpbr = cpb.build(pkixBParams);
        cp = cpbr.getCertPath();
    }
    
    private void validateCertificationPath() throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException, CertPathValidatorException {
        PKIXParameters pkixParams = new PKIXParameters(trustAnchorKeyStore);
        //Class that performs the certification path validation
        CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
        //Disables the previous mechanism for revocation check (pre Java8)
        pkixParams.setRevocationEnabled(false);
        //Enable OCSP verification
        Security.setProperty("ocsp.enable", "true");
        //Instantiate a PKIXRevocationChecker class
        PKIXRevocationChecker rc = (PKIXRevocationChecker) cpv.getRevocationChecker();
        //Configure to validate all certificates in chain using only OCSP
        rc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.SOFT_FAIL,PKIXRevocationChecker.Option.NO_FALLBACK));
        //Do the validation
        cpv.validate(cp, pkixParams);
    }
    
    public boolean isCertificateValid(String govTrustAnchor, String intermediateCertificates){
        try{
            // First See if the Certificate Time is Valid
            isCertificateTimeValid();

            // Build the Certification Path
            buildCertificationPath(govTrustAnchor, intermediateCertificates);

            // Validate the Certification Path
            validateCertificationPath();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
