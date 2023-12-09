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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        FileManager fileManager = new FileManager();
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
                    System.out.println("Applicacao : "+appName+" versao : "+appVersion+" adicionada com sucesso!!!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                break;

            case "purchase":
                if (args.length > 1) {
                    printDefaultHelp();
                    break;
                }
                Scanner sc = new Scanner(System.in);;
                String appChosen = "";
                String appDir;
                String userRep = "";
                do {
                    //print app list
                    System.out.println("Introduza o nome uma das seguintes Aplicacoes:\n"+fileManager.listApps());
                    //chose app
                    appChosen = sc.nextLine();
                    appDir = licenseRep + "/" + appChosen;
                } while (!(new File(appDir).exists() && new File(appDir).isDirectory() && new File(appDir + "/version_info.txt").exists()));

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
                        fileManager.purchaseDataToJSONFile(userCard, new AppProperties(appChosen), purchaseFileDir);
                    } catch (Exception ex) {
                        fileManager.deleteFolder(purchaseFileDir);
                        System.out.println(ex.getMessage());
                    }
                }

                break;

            case "generate-license":
                if (args.length > 2) {
                    printDefaultHelp();
                    break;
                }

                String licenseRequest = licenseRep + "/MRLicReqFolder";
                String licenseRequestZip = licenseRequest;
                String lrTempWorkingDir = licenseRequest + "/TempWorkingDir";
                if (args.length == 2) {
                    licenseRequestZip = args[1];
                    if (!new File(licenseRequestZip).exists()) {
                        System.out.println("Nao existe o pedido de licenca no diretorio especificado!");
                        break;
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
                    String dataDir = lrTempWorkingDir + "data";
                    String certificate = dataDir + "/certificate.crt";
                    String signature = dataDir + "/signature.txt";
                    String govTrustAnchor = distFolder+"/PTCardCertificates/govTrustCertificate/ECRaizEstado002.crt";
                    String intermediateCertificatesFolder = distFolder+"/PTCardCertificates/intermediateCertificates";

                    CertificateValidity certitificateValidity = new CertificateValidity(certificate);
                    if (!certitificateValidity.isCertificateValid(govTrustAnchor, intermediateCertificatesFolder)) {
                        throw new Exception("Certificado de Utilizador do Cartao de Cidadao invalido");
                    }

                    DigitalSignature digitalSignature = new DigitalSignature();
                    if (!digitalSignature.checkSignature(dataDir + "/data.json", certificate, signature)) {
                        throw new Exception("Assinatura do Utilizador do Cartao de Cidadao e invalida");
                    }

                    //read jsonFile  to json
                    JsonObject licenseReqData = fileManager.JSONFiletoJSONObj(dataDir + "/data.json");
                    //fileManager.deleteFolder(licenseRep + "/TempWorkingDir");
                    //ir para pasta do jogo correspondente
                    String currentGameFolder = licenseRep + "/" + licenseReqData.getAsJsonObject("app").get("name").getAsString();
                    //comparar com dados version_info.txt
                    if (!fileManager.checkExistingLine(currentGameFolder + "/version_info.txt", licenseReqData.getAsJsonObject("app").get("version").getAsString() + ">" + licenseReqData.getAsJsonObject("app").get("hash").getAsString())) {
                        throw new Exception("Integridade da aplicacao comprometida!");
                    }
                    //ir para pasta do email correspondente
                    String userFolder = currentGameFolder + "/" + licenseReqData.getAsJsonObject("user").get("email").getAsString();
                    //ler todos os prchase#.json ->array
                    ArrayList<File> purchases = fileManager.getAllJSONspurchases(userFolder);
                    //ver se algum match
                    File purchaseFound = null;
                    for (File purchase : purchases) {
                        JsonObject jsonPur = fileManager.JSONFiletoJSONObj(purchase.getAbsolutePath());
                        if (jsonPur.getAsJsonObject("user").getAsString().equals(licenseReqData.getAsJsonObject("user").getAsString())) {
                            if (jsonPur.has("pc")) {
                                if (tolerable(jsonPur.getAsJsonObject("pc"), licenseReqData.getAsJsonObject("pc"), 1)) {
                                    purchaseFound = purchase;
                                    break;
                                }
                            }
                        }
                    }
                    //if pc empty--- update with dados de pedido
                    if (purchaseFound == null) {

                        for (File purchase : purchases) {

                            LicenseData licenseData = fileManager.JSONFiletoLicenseData(purchase.getAbsolutePath());
                            if (licenseData.pc == null) {

                                try {
                                    fileManager.licenseDataToJSONFile(licenseData.user, new ComputerProperties(licenseReqData.getAsJsonObject("numberOfCPUs").getAsString(), licenseReqData.getAsJsonObject("macAddress").getAsString()), licenseData.app, purchase.getAbsolutePath());

                                    purchaseFound = new File(purchase.getAbsolutePath());

                                } catch (Exception e) {
                                    fileManager.purchaseDataToJSONFile(licenseData.user, licenseData.app, purchase.getAbsolutePath());
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
                //criar data(Folder) in tempworkingDir
                //criar data.json com dados do LicReq de user e app, mas com pc de purchase.json in data(Folder)
                //sign data.json com managerPrivateKey
                //write signature file in data(folder)
                //zip data(Folder) to data.zip
                //delete data(folder)
                //securecoms (data.zip,dataDir+"/replacementUserCertificate.crt)"
                //zip tempWorkingDir to "MRLic_"+emai+".zip"
                //delete tempWorkingDir (license)
                //delete tempWorkingDir (licenseReq)
                //print path (userFolder + MRLic...)
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
        System.out.println("help: java -jar \"LicenseManager.jar\" help");
        System.out.println("  ->print available commands, options and descriptions\n");
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
        String managerKeyStore = distFolder+"/keyStoreManager/licenseManagerKeystore.jks";
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
}
