/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package logic;

import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import crypto.CertificateValidity;
import crypto.DigitalSignature;
import crypto.SymmetricCipher;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.LicenseData;
import dataCollecting.UserCard;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
public class LicenseManager {

    private static String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private static String distFolder = (new File(LicenseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath().replace("\\", "/");
    private static String licenseRep = distFolder + "/LicenseRep";
    private static FileManager fileManager = new FileManager();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length == 0) {
            System.out.println("Command without arguments!");
            printHelp();
            System.exit(0);
        }

        switch (args[0]) {
            case "add-app":
                //tem de ser tudo por argumentos
                if (args.length != 4) {
                    printDefaultHelp();
                    break;
                }
                //name version pwdJar (args)
                String appName = args[1];
                String appVersion = args[2];
                String pathJar = args[3];
                String appFolder = licenseRep + "/" + appName;
                try {
                    //criar pasta com name ou ir para ja criada
                    fileManager.createFolder(appFolder);
                    //getHash of pwdJar
                    AppProperties appProp = new AppProperties(appName, appVersion);
                    appProp.setHash(pathJar);
                    //criar ou atualizar version_info.txt
                    fileManager.updateFile(appFolder + "/version_info.txt", appVersion, appProp.getHash());
                    System.out.println("Applicacao : " + appName + " versao : " + appVersion + " adicionada com sucesso!!!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                break;

            case "purchase":
                if (args.length > 1) {
                    printDefaultHelp();
                    break;
                }
                Scanner sc = new Scanner(System.in);
                String appChosen = "";
                String appDir;
                String userRep = "";
                do {
                    //print app list
                    System.out.println("Introduza o nome uma das seguintes Aplicacoes:");
                    System.out.println(fileManager.listApps());
                    //chose app
                    appChosen = sc.nextLine();
                    appChosen = licenseRep + "/" + appChosen;
                } while (!(new File(appChosen).exists() && new File(appChosen).isDirectory() && new File(appChosen + "/version_info.txt").exists()));
                appDir = appChosen;

                //add user info (with email)
                String email;
                String aux;
                while (true) {
                    System.out.println("Introduza o email do cliente:");
                    aux = sc.nextLine();
                    if (isValidEmail(aux)) {
                        email = aux;
                        break;
                    } else {
                        System.out.println("!!Email invalido!!");
                    }
                }

                UserCard userCard = null;
                try {
                    userCard = new UserCard(email);
                } catch (PTEID_Exception ex) {
                    System.out.println(ex.GetMessage());
                    break;
                }

                userRep = appDir + "/" + email;

                fileManager.createFolder(userRep);

                //create purchase#.json
                //put it in pasta(jogo)/pasta(email) correspondente
                String purchaseFileDir = fileManager.createPurchaseFileName(userRep);
                 {
                    try {
                        fileManager.purchaseDataToJSONFile(userCard, new AppProperties(new File(appDir).getName()), purchaseFileDir);
                    } catch (Exception ex) {
                        fileManager.deleteFolder(purchaseFileDir);
                        System.out.println(ex.getMessage());
                        break;
                    }
                }
                System.out.println("Registo da compra adicionado com sucesso!");
                break;

            case "generate-license":
                if (args.length > 3 || args.length < 2) {
                    printDefaultHelp();
                    break;
                }
                String validity = args[1];
                DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                String userEmail = "";
                String userFolder = "";
                LicenseData licensePurchaseData = null;
                JsonObject licenseReqData = null;
                String userReplaCert = "";
                try {
                    LocalDate parsedDate = LocalDate.parse(validity, dateForm);
                    LocalDate currentDate = LocalDate.now();
                    if (!parsedDate.isAfter(currentDate)) {
                        System.out.println("Validade da licenca com data invalida. Tera de ser posterior a " + currentDate.format(dateForm));
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Data da validade da licenca tem de ter o formato dd/MM/yyyy");
                    break;
                }
                System.out.println("Aguarde um pouco!");
                String licenseRequest = licenseRep + "/MRLicReqFolder";
                String licenseRequestZip = licenseRequest;
                String lrTempWorkingDir = licenseRequest + "/TempWorkingDir";
                if (args.length == 3) {
                    licenseRequestZip = args[2];
                    if (!new File(licenseRequestZip).exists()) {
                        System.out.println("Nao existe o pedido de licenca no diretorio especificado!");
                        break;
                    } else {
                        licenseRequestZip = new File(licenseRequestZip).getAbsolutePath();
                    }
                } else {
                    File[] files = new File(licenseRequestZip).listFiles();
                    if (files == null) {
                        System.out.println("Nao existe o pedido de licenca no diretorio por defeito(copie o pedido para " + licenseRequest + ")!");
                        break;
                    }
                    for (File file : files) {
                        if (file.getName().split("_")[0].equals("MRLicReq")) {
                            licenseRequestZip += "/" + file.getName();
                            break;
                        }
                    }
                }

                fileManager.deleteFolder(lrTempWorkingDir);
                try {
                    //ler dados pedido de licenca
                    //unzip
                    fileManager.unzipFolderWithDest(licenseRequestZip, licenseRequest);
                    //open "unsecureComs"
                    String dataZip = openLicenseRequest(lrTempWorkingDir);
                    fileManager.unzipFolder(dataZip);

                    //check signature
                    String dataDir = lrTempWorkingDir + "/data";
                    String certificate = dataDir + "/certificate.crt";
                    String signature = dataDir + "/signature.txt";
                    String ptCardCertFolder = distFolder + "/PTCardCertificates";

                    CertificateValidity certitificateValidity = new CertificateValidity(certificate);
                    if (!certitificateValidity.isCertificateValid(ptCardCertFolder)) {
                        throw new Exception("Certificado de Utilizador do Cartao de Cidadao invalido");
                    }

                    DigitalSignature digitalSignature = new DigitalSignature();
                    if (!digitalSignature.checkSignature(dataDir + "/data.json", certificate, signature)) {
                        throw new Exception("Assinatura do Utilizador do Cartao de Cidadao e invalida");
                    }

                    //read jsonFile  to json
                    licenseReqData = fileManager.JSONFiletoJSONObj(dataDir + "/data.json");
                    //fileManager.deleteFolder(licenseRep + "/TempWorkingDir");
                    //ir para pasta do jogo correspondente
                    String currentGameFolder = licenseRep + "/" + licenseReqData.getAsJsonObject("app").get("name").getAsString();
                    //comparar com dados version_info.txt
                    if (!fileManager.checkExistingLine(currentGameFolder + "/version_info.txt", licenseReqData.getAsJsonObject("app").get("version").getAsString() + ">" + licenseReqData.getAsJsonObject("app").get("hash").getAsString())) {
                        throw new Exception("Integridade da aplicacao comprometida!");
                    }
                    //ir para pasta do email correspondente
                    userEmail = licenseReqData.getAsJsonObject("user").get("email").getAsString();
                    userFolder = currentGameFolder + "/" + userEmail;
                    //ler todos os prchase#.json ->array
                    ArrayList<File> purchases = fileManager.getAllJSONspurchases(userFolder);
                    //ver se algum match
                    File purchaseFound = null;
                    for (File purchase : purchases) {
                        JsonObject jsonPur = fileManager.JSONFiletoJSONObj(purchase.getAbsolutePath());
                        if (jsonPur.getAsJsonObject("user").toString().equals(licenseReqData.getAsJsonObject("user").toString())) {
                            if (jsonPur.has("pc")) {
                                if (tolerable(jsonPur.getAsJsonObject("pc"), licenseReqData.getAsJsonObject("pc"), 1)) {
                                    purchaseFound = purchase;
                                    userReplaCert = dataDir + "/userReplacementCertificate.crt";
                                    break;
                                }
                            }
                        }
                    }
                    //if pc empty--- update with dados de pedido
                    if (purchaseFound == null) {

                        for (File purchase : purchases) {
                            licensePurchaseData = fileManager.JSONFiletoLicenseData(purchase.getAbsolutePath());
                            if (licensePurchaseData.pc == null) {

                                try {
                                    licensePurchaseData.pc = new ComputerProperties(licenseReqData.getAsJsonObject("pc").get("numberOfCPUs").getAsInt(), licenseReqData.getAsJsonObject("pc").get("macAddress").getAsString());
                                    fileManager.licenseDataToJSONFile(licensePurchaseData.user, licensePurchaseData.pc, licensePurchaseData.app, purchase.getAbsolutePath());

                                    purchaseFound = new File(purchase.getAbsolutePath());
                                    userReplaCert = dataDir + "/replacementUserCertificate.crt";

                                } catch (Exception e) {
                                    fileManager.purchaseDataToJSONFile(licensePurchaseData.user, licensePurchaseData.app, purchase.getAbsolutePath());
                                    throw e;
                                }
                                break;
                            }
                        }

                    }
                    if (purchaseFound == null) {
                        throw new Exception("Nao existe nunhuma compra para este pedido de licenca!");
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    break;
                }

                //criar licenca e colocar na pasta do user
                //criar tempWorkingDir in userFolder
                String lTempWorkingDir = userFolder + "/TempWorkingDir";
                String lDataFolder = lTempWorkingDir + "/data";
                String lDataJsonFile = lDataFolder + "/data.json";
                String lSignature = lDataFolder + "/signature.txt";
                String managerKeyStore = distFolder + "/keyStoreManager/licenseManagerKeystore.jks";
                String lDataFolderZip = lTempWorkingDir + "/data.zip";
                String lDataEncrypted = lTempWorkingDir + "/data.crypto";
                String lSymmetricKey = lTempWorkingDir + "/sKey.txt";
                String lIV = lTempWorkingDir + "iv.txt";
                String lEncryptedSymmetricKey = lTempWorkingDir + "/sKey.crypto";
                String license = userFolder + "/MRLic_" + userEmail + ".zip";

                //criar data(Folder) in tempworkingDir
                fileManager.deleteFolder(lTempWorkingDir);
                fileManager.createFolder(lTempWorkingDir);
                fileManager.createFolder(lDataFolder);

                try {
                    //criar data.json com dados do LicReq de user e app e validity, mas com pc de purchase.json in data(Folder)

                    AppProperties appProp = new AppProperties(licenseReqData.getAsJsonObject("app").get("name").getAsString(), licenseReqData.getAsJsonObject("app").get("version").getAsString());
                    appProp.injectHash(licenseReqData.getAsJsonObject("app").get("hash").getAsString());
     
                    fileManager.licenseDataToJSONFileWithValidity(licensePurchaseData.user, licensePurchaseData.pc, appProp, validity, lDataJsonFile);
                    //sign data.json com managerPrivateKey
                    //write signature file in data(folder)
                    DigitalSignature dSignature = new DigitalSignature();
                    dSignature.signFile(lDataJsonFile, lSignature, new AssymetricCipher().getPrivateKeyFromKeyStore(managerKeyStore, "licenseManager", "licenseManager"));

                    //zip data(Folder) to data.zip
                    fileManager.zipToFileWithDest(lDataFolder, lDataFolderZip);
                    //delete data(folder)
                    fileManager.deleteFolder(lDataFolder);

                    //securecoms (data.zip,dataDir+"/replacementUserCertificate.crt)"
                    secureComs(lDataFolderZip, userReplaCert, lTempWorkingDir);
                    //zip tempWorkingDir to "MRLic_"+emai+".zip"
                    fileManager.zipToFileWithDest(lTempWorkingDir, license);

                    //print path (userFolder + MRLic...)
                    System.out.println("A licenca do utilizador " + userEmail + " encontra-se em:");
                    System.out.println(license);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    break;
                }
                break;

            case "help":
                if (args.length > 1) {
                    printDefaultHelp();
                } else {
                    printHelp();
                }
                break;
            default:
                printDefaultHelp();
        }
    }

    private static void printDefaultHelp() {
        System.out.println("Arguments not recognized!");
        printHelp();
    }

    private static void printHelp() {
        System.out.println("   ------   Printing Help   ------   ");
        System.out.println("[] -> required parameter\n<> -> optional parameter\n");
        
        System.out.println("\n\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\n");
        System.out.println("add-app: java -jar \"LicenseManager.jar\" add-app [App Name] [Version] \"[Path to App jar File]\"");
        System.out.println("\t-> Add new Application or update Version Application to your Repository of Applications");
        System.out.println("\t-> It is necessary to give correct Application Name and Version");
        System.out.println("\t-> It is necessary to give and have in this machine the correct jar file, properly integrated with the library MRLicensing");
        System.out.println("\n\t-> In a correct use, the LicenseRep Folder will be a repository of registered applications");
        System.out.println("\t-> The applications will be represented as folders");
        System.out.println("\t-> Only folders that have a file \"version_info.txt\" will be considerated as valid");
        System.out.println("\t-> The file \"version_info.txt\" will hold the different registered app versions and correspondent hash");
        
        System.out.println("\n\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\n");
        System.out.println("help: java -jar \"LicenseManager.jar\" help");
        System.out.println("\t-> print available commands, options and descriptions");
        
        System.out.println("\n\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\n");
        System.out.println("generate-license: java -jar \"LicenseManager.jar\" generate-license [validity] <Path to License Request>");
        System.out.println("\t-> Generate a new Use License");
        System.out.println("\t-> It is necessary to set a validity for the License, it needs to be after the current Date");
        System.out.println("\t-> validity date format --->  dd/MM/yyyy  |\texample --->  11/09/2000");
        System.out.println("\t-> It is necessary to have the correlated License Request in this machine (read <Path to License Request> option description to understand how to indicate the proper file)");
        System.out.println("\n    option <Path to License Request>");
        System.out.println("\t-> If this option is not used, the command will try to use the License Request in the default folder");
        System.out.println("\t-> If this option exists, the command will try to use the given License Request file");
        System.out.println("\t->\tDefault Folder --> "+licenseRep + "/MRLicReqFolder <--" );
        System.out.println("\t-> If you want to use the Default Method (without <Path to License Request> option), follow this steps:");
        System.out.println("\t\t1- Delete any zip file(that starts with \"MRLicReq_\") from the Default Folder("+licenseRep + "/MRLicReqFolder)");
        System.out.println("\t\t2- Copy the License Request (correlated with this License Generation Action), to the Default Folder("+licenseRep + "/MRLicReqFolder)");
        System.out.println("\t\t3- execute the command: java -jar \"LicenseManager.jar\" generate-license [validity]");
        
        System.out.println("\n\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\n");
        System.out.println("purchase: java -jar \"LicenseManager.jar\" purchase");
        System.out.println("\t-> Add a new user purchase regist");
        System.out.println("\t-> It is necessary to give a Application already registed (input when asked)");
        System.out.println("\t-> It is necessary to be connected to a smartCard reader with Portuguese Citizen Card (Authentication PIN not needed)");
        System.out.println("\t-> It is necessary to give the user email (matching the one that the user intends to use in final App)");
        System.out.println("\n\t-> In a correct use, each game folder will have the users that purchase the acording application");
        System.out.println("\t-> The Users will be represented as folders");
        System.out.println("\t-> Only folders that have one or more files \"purchase#.json\" will be considerated as valid");
        System.out.println("\t-> The file \"purchase#.json\" will hold the mainly the user infomation and the user computer properties");
        System.out.println("\t-> If there are no user computer properties in the file \"purchase#.json\" it means that the user has not consumed the purchase to a specific computer");
        System.out.println("\t-> The User can ask again for a license for the same computer, this will reuse the matching \"purchase#.json\"");
        System.out.println("\t-> If the User is asking for a license for a not recognized computer, this will use the first found \"purchase#.json\" without user computer properties (for the maching game and user)");
        
        System.out.println("\n\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\n");
        System.out.println("   -------   End of Help   -------   ");
    }

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static String openLicenseRequest(String tempWorkingDir) throws Exception {
        String symmetricEncryptedKeyFile = tempWorkingDir + "/sKey.crypto";
        String symmetricKeyFile = tempWorkingDir + "/sKey.txt";
        String initialVectorFile = tempWorkingDir + "/iv.txt";
        String encryptedDataFile = tempWorkingDir + "/data.crypto";
        String dataFile = tempWorkingDir + "/data.zip";
        //ler chave privada do gestor
        String managerKeyStore = distFolder + "/keyStoreManager/licenseManagerKeystore.jks";
        //decifrar sKey.crypto com a privada    
        AssymetricCipher aCipher = new AssymetricCipher();
        aCipher.decipherFile(symmetricEncryptedKeyFile, symmetricKeyFile, managerKeyStore, "licenseManager", "licenseManager");
        //decifrar data.crypto com sKey.txt e iv.txt
        SymmetricCipher sCipher = new SymmetricCipher();
        sCipher.decipherFile(encryptedDataFile, dataFile, symmetricKeyFile, initialVectorFile);
        return dataFile;
    }

    private static boolean tolerable(JsonObject json1, JsonObject json2, int maxTolerance) {
        int tolerance = 0;
        for (String key : json1.keySet()) {
            if (!json1.getAsJsonObject(key).getAsString().equals(json2.getAsJsonObject(key).getAsString())) {
                tolerance++;
                if (tolerance > maxTolerance) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void secureComs(String zipFile, String managerCertificateFile, String workingDir) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, InvalidKeySpecException, KeyStoreException {
        String symmetricKeyFile = workingDir + "/sKey.txt";
        String symmetricEncryptedKeyFile = workingDir + "/sKey.crypto";
        String initialVectorFile = workingDir + "/iv.txt";
        String encryptedZipFile = workingDir + "/data.crypto";

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
}
