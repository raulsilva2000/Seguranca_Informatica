/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import crypto.AssymetricCipher;
import crypto.DigitalSignature;
import crypto.SymmetricCipher;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.LicenseData;
import dataCollecting.UserCard;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class MRLicensing {

    private FileManager fileManager;
    private String distFolder;
    private String licenseRep;
    private String tempWorkingDir;
    private DigitalSignature digitalSignature;
    private String appName;
    private String version;
    private LicenseData currentLicense = null;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public MRLicensing() {
        fileManager = new FileManager();
        distFolder = (new File(MRLicensing.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getParentFile().getAbsolutePath().replace("\\", "/");
        licenseRep = distFolder + "/LicenseRep";
        tempWorkingDir = licenseRep + "/TempWorkingDir";
        fileManager.createFolder(licenseRep);
        fileManager.createFolder(tempWorkingDir);
    }

    public void init(String nomeDaApp, String versao) {
        digitalSignature = new DigitalSignature();
        this.appName = nomeDaApp;
        this.version = versao;
    }

    /**
     * Check if the application has a valid License.<br><br>
     *
     * Asks for User <b>email</b>. Finds the license absolute path in
     * LicenseRep. if license doesn't exist in <b>LicenseRep</b> asks the user
     * for a path to the License Tries to valilidate the License and returns
     * true if the choosen License is valid
     *
     * @return Returns false if can't find the user given License file, and
     * returns a boolean indicating Choosen License validity (using method
     * {@link #validateLicense})
     * @see logic.MRLicensing#validateLicense(java.lang.String,
     * java.lang.String)
     */
    public boolean isRegistered() {
        String licensePath = null;
        Scanner sc = new Scanner(System.in);
        String email;
        String aux;
        while (true) {
            System.out.println("Introduza o seu email:");
            aux = sc.nextLine();
            if (isValidEmail(aux)) {
                email = aux;
                break;
            } else {
                System.out.println("!!Email invalido!!");
            }
        }
        for (File file : new File(licenseRep).listFiles()) {
            if (file.getName().equals("MRLic_" + email + ".zip")) {
                licensePath = file.getAbsolutePath();
                break;
            }
        }
        String aux2 = "";
        while (licensePath == null) {
            System.out.println("Indique a localizacao da nova licenca de utilizacao!(para cancelar escreva \"exit\")");
            aux2 = sc.nextLine();
            if (aux2.equals("exit")) {
                System.out.println("License Validation Cancelled");
                System.exit(0);
            }
            File filetemp = new File(aux2);
            if (filetemp.exists() && filetemp.getName().equals("MRLic_" + email + ".zip")) {
                try {
                    for (File file : new File(licenseRep).listFiles()) {
                        if (file.getName().split("_")[0].equals("MRLic")) {
                            fileManager.deleteFolder(file.getAbsolutePath());
                        }
                    }
                    Files.copy(Paths.get(filetemp.getAbsolutePath()), Paths.get(licenseRep + "/MRLic_" + email + ".zip"));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
                licensePath = licenseRep + "/MRLic_" + email + ".zip";
                break;
            }
        }
        boolean valid = false;
        try {
            valid = validateLicense(licensePath, email);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        fileManager.deleteFolder(tempWorkingDir);
        return valid;
    }

    /**
     * Generate a new <b>License Request</b><br><br>
     *
     * Asks for User <b>email</b>. Print path to the License Request after
     * generating it, through {@link #askNewLicense} method
     *
     * @return true if the License Request is properly generated
     * @see logic.MRLicensing#askNewLicense(java.lang.String, java.util.Scanner)
     */
    public boolean startRegistration() {
        Scanner sc = new Scanner(System.in);
        String email;
        String aux;
        while (true) {
            System.out.println("Introduza o seu email:");
            aux = sc.nextLine();
            if (isValidEmail(aux)) {
                email = aux;
                break;
            } else {
                System.out.println("!!Email invalido!!");
            }
        }
        System.out.println("Aguarde um pouco.");
        try {
            System.out.println("O pedido de licença está no diretório:\n" + askNewLicense(email, sc));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            fileManager.clearFolder(tempWorkingDir);
        }
        sc.close();
        return false;
    }

    /**
     * Show information about the valid Linsence in use<br><br>
     *
     * If there is no License in use it prints a warning message. If exists it
     * prints the License information
     *
     * @see logic.MRLicensing#currentLicense
     */
    public void showLicenseInfo() {
        if (currentLicense == null) {
            System.out.println("********************************************************");
            System.out.println("A aplicacao ainda nao tem nenhuma licenca em utilizacao!");
            System.out.println("********************************************************");
        } else {
            try {
                System.out.println("======================================");
                System.out.println("      ##    License Info     ##");
                System.out.println("======================================");
                System.out.println("\nUser Info:");
                System.out.println(showObjInfo(currentLicense.user));
                System.out.println("Computer Properties Info:");
                System.out.println(showObjInfo(currentLicense.pc));
                System.out.println("App Properties Info:");
                System.out.println(showObjInfo(currentLicense.app));
                System.out.println("======================================");
                System.out.println("     ##   End License Info     ##");
                System.out.println("======================================");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    /**
     * Generate the <b>License File</b><br><br>
     * It clears the Temporary Working Directory, and creates a json file with
     * the current data from User({@link dataCollecting.UserCard}),
     * Computer({@link dataCollecting.ComputerProperties}) and
     * App({@link dataCollecting.AppProperties}), in
     * <b>LicenseRep/TempWorkingDir/data/data.json</b><br>
     * Then signs that data through {@link crypto.DigitalSignature#signFileWithID(java.lang.String, java.lang.String, java.lang.String) } method.<br>
     * Create a KeyStore file that saves a Assymetric Key pair in app folders,
     * and writes a certificate in the license request folder.<br>
     * Use hibrid encripting to create a secure communication to the License
     * Manager through the
     * {@link #secureComs(java.lang.String, java.lang.String)} method
     *
     * @param email
     * @param sc
     * @return Returns the location of License Request, ready to be sent to the
     * License Manager
     * @throws PTEID_Exception
     * @throws IOException
     * @throws FileNotFoundException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     * @throws Exception
     */
    private String askNewLicense(String email, Scanner sc) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, Exception {
        String src = licenseRep + "/MRLicReq_" + email + ".zip";//pwd of licenseAsk file

        fileManager.clearFolder(tempWorkingDir);

        String tempDataFolder = tempWorkingDir + "/data";
        fileManager.createFolder(tempDataFolder);

        String tempDataJSON = tempDataFolder + "/data.json";
        String dataTempZip = tempWorkingDir + "/data.zip";

        fileManager.licenseDataToJSONFile(new UserCard(email), new ComputerProperties(), new AppProperties(appName, version), tempDataJSON);

        digitalSignature.signFileWithID(tempDataJSON, tempDataFolder + "/certificate.crt", tempDataFolder + "/signature.txt");

        fileManager.addNewKeyStore(email, sc, tempDataFolder);

        fileManager.zipToFileWithDest(tempDataFolder, dataTempZip);
        fileManager.deleteFolder(tempDataFolder);

        secureComs(dataTempZip, licenseRep + "/managerCertificate.crt");

        fileManager.zipToFileWithDest(tempWorkingDir, src);
        fileManager.deleteFolder(tempWorkingDir);
        return src;
    }

    /**
     * Check if the given License is valid<br><br>
     * Starts by clearing Temporary Working Directory.<br>
     * Opens License through hybrid decripting using the replacement User
     * assymetric private key ({@link #openLicense(java.lang.String, java.lang.String)
     * }(generated while creating License Request).<br>
     * Checks Manager signature of the License data with Manager assymetric
     * public key (already in application folders)<br>
     * Compares data from License with data from current execution (tolerating 1
     * computer hardware change).
     *
     * @param licensePath
     * @param email
     * @return Returns boolean representing validity of the given License
     * @throws Exception
     */
    private boolean validateLicense(String licensePath, String email) throws Exception {
        fileManager.deleteFolder(tempWorkingDir);
        fileManager.unzipFolder(licensePath);
        String dataZip = openLicense(tempWorkingDir, email);
        fileManager.unzipFolder(dataZip);
        String dataFolder = tempWorkingDir + "/data";
        String dataJson = dataFolder + "/data.json";
        String signature = dataFolder + "/signature.txt";
        String managerCert = licenseRep + "/managerCertificate.crt";

        DigitalSignature dgSignature = new DigitalSignature();
        if (!digitalSignature.checkSignature(dataJson, managerCert, signature)) {
            throw new Exception("Licenca Invalida!");
        }

        LicenseData licenseData = fileManager.JSONFiletoLicenseData(dataJson);
        boolean tolerab = false;
        LicenseData currentData = new LicenseData(new UserCard(email), new ComputerProperties(), new AppProperties(appName, version));
        if (licenseData.user.getFullName().equals(currentData.user.getFullName()) && licenseData.user.getCivilNumber().equals(currentData.user.getCivilNumber()) && licenseData.user.getEmail().equals(currentData.user.getEmail())) {
            if (licenseData.app.getName().equals(currentData.app.getName()) && licenseData.app.getVersion().equals(currentData.app.getVersion()) && licenseData.app.getHash().equals(currentData.app.getHash())) {
                tolerab = tolerable(licenseData.pc, currentData.pc, 1);
            }
        }
        if (tolerab) {
            currentLicense = licenseData;
        }
        return tolerab;
    }

    /**
     * Hybrid Decrypting<br><br>
     * 
     * Firstly asks for password to get replacement assymetric user private key (created while generating License Request).<br>
     * Then Decypher Symmetric Key (used to Encrypt License data by Manager), using replacement assymetric user private key.<br>
     * Finaly Decypher License Data with the same symmetric key and initial vector.
     * @param tempWorkingDir
     * @param email
     * @return Returns zip file location with License data (in plain text) 
     * @throws Exception 
     */
    private String openLicense(String tempWorkingDir, String email) throws Exception {
        String symmetricEncryptedKeyFile = tempWorkingDir + "/sKey.crypto";
        String symmetricKeyFile = tempWorkingDir + "/sKey.txt";
        String initialVectorFile = tempWorkingDir + "/iv.txt";
        String encryptedDataFile = tempWorkingDir + "/data.crypto";
        String dataFile = tempWorkingDir + "/data.zip";
        //ler chave privada do gestor
        String userKeyStore = licenseRep + "/keyStore/KeyStore_" + email + ".jks";
        //decifrar sKey.crypto com a privada    
        AssymetricCipher aCipher = new AssymetricCipher();
        Scanner sc = new Scanner(System.in);
        System.out.println(email + " --> Introduza a password para a sua licença de utilização:");

        aCipher.decipherFile(symmetricEncryptedKeyFile, symmetricKeyFile, userKeyStore, sc.nextLine(), "ReplacementUserKeyPair");

        //decifrar data.crypto com sKey.txt e iv.txt
        SymmetricCipher sCipher = new SymmetricCipher();
        sCipher.decipherFile(encryptedDataFile, dataFile, symmetricKeyFile, initialVectorFile);
        return dataFile;
    }

    /**
     * Hybrid Encrypting<br><br>
     * 
     * Firstly Encrypts the file given by paramater with a new symmetric key and initial vector.<br>
     * Then Encrypts the same symmetric key with the public key in the certificate file given by parameter.
     * 
     * 
     * asks for password to get replacement assymetric user private key (created while generating License Request).<br>
     * Then Decypher Symmetric Key (used to Encrypt License data by Manager), using replacement assymetric user private key.<br>
     * Finaly Decypher License Data with the same symmetric key and initial vector.
     * @param zipFile
     * @param managerCertificateFile
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws CertificateException
     * @throws InvalidKeySpecException
     * @throws KeyStoreException 
     */
    private void secureComs(String zipFile, String managerCertificateFile) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, InvalidKeySpecException, KeyStoreException {
        String symmetricKeyFile = tempWorkingDir + "/sKey.txt";
        String symmetricEncryptedKeyFile = tempWorkingDir + "/sKey.crypto";
        String initialVectorFile = tempWorkingDir + "/iv.txt";
        String encryptedZipFile = tempWorkingDir + "/data.crypto";

        //symetric cypher
        SymmetricCipher sCipher = new SymmetricCipher();
        byte[] keyBytes = sCipher.genKey(symmetricKeyFile);
        byte[] ivBytes = sCipher.genVector(initialVectorFile);
        sCipher.cipherFile(zipFile, encryptedZipFile, keyBytes, ivBytes);
        fileManager.deleteFolder(zipFile);

        //assimetric cypher of symmmetric key with manager public key
        AssymetricCipher aCipher = new AssymetricCipher();
        aCipher.cipherFile(symmetricKeyFile, symmetricEncryptedKeyFile, managerCertificateFile);

        fileManager.deleteFolder(symmetricKeyFile);
    }

    private static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Compares main attributes from two ComputerProperties and cheks if they are significantly different
     * 
     * 
     * @param pc1
     * @param pc2
     * @param maxTolerance
     * @return Returns boolean false if the differences are bigger than parameter maxTolerance
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException 
     */
    public static boolean tolerable(ComputerProperties pc1, ComputerProperties pc2, int maxTolerance) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        int tolerance = 0;

        for (String attribute : pc1.getMainAttributes()) {
            try {
                Method getterMethod = pc1.getClass().getMethod("get" + capitalize(attribute));
                Object value1 = getterMethod.invoke(pc1);
                Object value2 = getterMethod.invoke(pc2);

                if (value1 != null && value2 != null && !value1.equals(value2)) {
                    tolerance++;
                    if (tolerance > maxTolerance) {
                        return false;
                    }
                }
            } catch (NoSuchMethodException | SecurityException e) {
                // Handle exception as needed
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Get all values of relevante attributes from parameter object.<br>
     * Must be instanceof {@link dataCollecting.AppProperties} or {@link dataCollecting.ComputerProperties} or {@link dataCollecting.UserCard}
     * @param obj
     * @return String with the found information
     * @throws Exception 
     */
    public static String showObjInfo(Object obj) throws Exception {

        System.out.println(obj.getClass().toString());
        String returnString = "";
        String[] attributes = {};
        if (obj instanceof ComputerProperties) {
            attributes = ((ComputerProperties) obj).getMainAttributes();
        } else if (obj instanceof AppProperties) {
            attributes = ((AppProperties) obj).getMainAttributes();
        } else if (obj instanceof UserCard) {
            attributes = ((UserCard) obj).getMainAttributes();
        } else {
            throw new Exception("Not a valid Object to show info");
        }

        try {
            for (String attribute : attributes) {
                Method getterMethod = obj.getClass().getMethod("get" + capitalize(attribute));
                Object value1 = getterMethod.invoke(obj);
                returnString += "\t-> " + attribute + ": " + getterMethod.invoke(obj) + "\n";
            }

        } catch (Exception e) {
            return "bad license reading";
        }
        return returnString;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
