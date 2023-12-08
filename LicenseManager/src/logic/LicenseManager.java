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
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Miguel
 */
public class LicenseManager {

    static String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    static Pattern pattern = Pattern.compile(EMAIL_REGEX);

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

                //criar pasta com name ou ir para ja criada
                fileManager.createFolder(appName);
                //getHash of pwdJar
                AppProperties appProp = new AppProperties(appName, appVersion);
                appProp.setHash(pathJar);

                //criar ou atualizar version_info.txt
                fileManager.updateFile("version_info.txt", appVersion, appProp.getHash());

                break;

            case "purchase":
                if (args.length > 1) {
                    printDefaultHelp();
                    break;
                }
                Scanner sc = new Scanner(System.in);
                String appChosen = "";
                String appDir = "LicenseRep";
                String userRep = "";
                do {
                    //print app list
                    System.out.println("Introduza o nome uma das seguintes Aplicacoes:");
                    System.out.println(fileManager.listApps());
                    //chose app
                    appChosen = sc.nextLine();
                    appDir += "/" + appChosen;
                } while (!(new File(appChosen).exists() && new File(appChosen).isDirectory()));

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

                UserCard userCard = new UserCard(email);

                fileManager.createFolder(email);
                userRep = appDir + "/" + email;

                //create purchase#.json
                //put it in pasta(jogo)/pasta(email) correspondente
                String purchaseFileDir = fileManager.createPurchaseFileName(userRep);
                fileManager.purchaseDataToJSONFile(userCard, new AppProperties(appName), purchaseFileDir);

                break;

            case "generate-license":
                if (args.length > 2) {
                    printDefaultHelp();
                    break;
                }
                String licenseRep = "./LicenseRep";
                String licenseRequest = licenseRep;
                if (args.length == 2) {
                    licenseRequest += "/" + args[1];
                } else {
                    File[] files = new File(licenseRequest).listFiles();
                    for (File file : files) {
                        if (file.getName().split("_")[0].equals("MRLicReq")) {
                            licenseRequest += "/" + file.getName();
                            break;
                        }
                    }
                }
                //ler dados pedido de licenca
                //unzip
                fileManager.unzipFolder(licenseRequest);
                //open "unsecureComs"
                String dataZip = openLicenseRequest(licenseRep + "/TempWorkingDir");
                fileManager.unzipFolder(dataZip);
                //check signature
                String dataDir = licenseRep + "/TempWorkingDir/data";
                String certificate = dataDir + "/certificate.crt";
                String signature = dataDir + "/signature.txt";
                String govTrustAnchor = "PTCardCertificates/govTrustCertificate/ECRaizEstado002.crt";
                String intermediateCertificatesFolder = "PTCardCertificates/intermediateCertificates";

                CertificateValidity certitificateValidity = new CertificateValidity(certificate);
                if (!certitificateValidity.isCertificateValid(govTrustAnchor, intermediateCertificatesFolder)) {
                    throw new Exception("Certificado de Utilizador do Cartao de Cidadao invalido");
                }

                DigitalSignature digitalSignature = new DigitalSignature();
                if (!digitalSignature.checkSignature(dataDir + "/data.json", certificate, signature)) {
                    throw new Exception("Assinatura do Utilizador do Cartao de Cidadao e invalida");
                }

                //read jsonFile  to json
                JsonObject licenseReqData= fileManager.JSONFiletoJSONObj(dataDir + "/data.json");
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
                File purchaseFound=null;
                for (File purchase : purchases) {
                    JsonObject jsonPur=fileManager.JSONFiletoJSONObj(purchase.getAbsolutePath());
                    if (jsonPur.getAsJsonObject("user").getAsString().equals(licenseReqData.getAsJsonObject("user").getAsString())) {
                        if(!jsonPur.getAsJsonObject("pc").getAsString().equals("")){
                            if (tolerable(jsonPur.getAsJsonObject("pc"),licenseReqData.getAsJsonObject("pc"),1)) {
                                purchaseFound=purchase;
                                break;
                            }
                        }
                    }
                }
                //if pc empty--- update with dados de pedido
                if(purchaseFound==null){
                    for (File purchase : purchases) {
                        LicenseData licenseData=fileManager.JSONFiletoLicenseData(purchase.getAbsolutePath());
                        if(licenseData.pc==null){
                            
                            fileManager.licenseDataToJSONFile(licenseData.user, new ComputerProperties(licenseReqData.getAsJsonObject("numberOfCPUs").getAsString(),licenseReqData.getAsJsonObject("macAddress").getAsString()), licenseData.app, purchase.getAbsolutePath());
                            
                            purchaseFound=new File(purchase.getAbsolutePath());
                            
                            break;
                        }
                    }
                }
                if(purchaseFound==null){
                    throw new Exception("Nao existe nunhuma compra para este pedido de licenca!");
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

    private static String openLicenseRequest(String tempWorkingDir) {
        String symmetricEncryptedKeyFile = tempWorkingDir + "/sKey.crypto";
        String symmetricKeyFile = tempWorkingDir + "/sKey.txt";
        String initialVectorFile = tempWorkingDir + "/iv.txt";
        String encryptedDataFile = tempWorkingDir + "/data.crypto";
        String dataFile = tempWorkingDir + "/data.zip";
        //ler chave privada do gestor
        String managerKeyStore = "keyStoreManager/licenseManagerKeystore.jks";
        //decifrar sKey.crypto com a privada    
        AssymetricCipher aCipher = new AssymetricCipher();
        aCipher.decipherFile(symmetricEncryptedKeyFile, symmetricKeyFile, managerKeyStore, "licenseManager", "licenseManager");
        //decifrar data.crypto com sKey.txt e iv.txt
        SymmetricCipher sCipher = new SymmetricCipher();
        sCipher.decipherFile(encryptedDataFile, dataFile, symmetricKeyFile, initialVectorFile);
        return dataFile;
    }

    private static boolean tolerable(JsonObject json1, JsonObject json2, int maxTolerance) {
        int tolerance=0;
        for (String key : json1.keySet()) {
            if(!json1.getAsJsonObject(key).getAsString().equals(json2.getAsJsonObject(key).getAsString())){
                tolerance++;
                if(tolerance>maxTolerance){
                    return false;
                }
            }
        }
        return true;
    }
}
