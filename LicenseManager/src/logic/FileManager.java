/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import dataCollecting.UserCard;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.LicenseData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class FileManager {

    private static String distFolder = (new File(LicenseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath().replace("\\", "/");
    private static String licenseRep = distFolder + "/LicenseRep";

    public FileManager() {
    }

    public void writeToFile(byte[] arrayBytes, String fileName) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(new File(fileName))) {
            outputStream.write(arrayBytes);
        }
    }

    public byte[] readFileToBytes(String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        byte[] arrayBytesWithContent = new byte[(int) file.length()];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(arrayBytesWithContent);
        inputStream.close();
        return arrayBytesWithContent;
    }

    public String zipToFile(String folder) throws IOException {
        String fileZip = folder;
        //write a zip 
        return fileZip;
    }

    public String unzipToFile(String filezip) {
        String folder = filezip;
        //write the folder from zip
        return folder;
    }

    public void clearFolder(String folderPath) {
        File folder = new File(folderPath);

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    clearFolder(file.getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    public void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recursively delete subfolders and files
                        deleteFolder(file.getPath());
                    } else {
                        file.delete();
                    }
                }
            }
            // Delete the main folder once its contents are deleted
            folder.delete();
        }
    }

    public void zipToFileWithDest(String folderToZip, String endFile) {
        byte[] buffer = new byte[1024];

        try (FileOutputStream fos = new FileOutputStream(endFile); ZipOutputStream zipOutputStream = new ZipOutputStream(fos)) {

            // create a new File object based on the folder path
            File folder = new File(folderToZip);

            // add the folder to the ZIP file
            addFolderToZip(folder, folder.getName(), zipOutputStream);

            //System.out.println("Folder successfully zipped!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Auxiliar method of zipToFileWithDest to add a folder to the ZIP file recursively
    private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zipOutputStream) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zipOutputStream);
            } else {
                // create a new ZipEntry for the file
                ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                // copy the file content to the ZIP file
                try (FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                }

                // close the ZipEntry
                zipOutputStream.closeEntry();
            }
        }
    }

    public void unzipFolder(String fileToUnzip) throws IOException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileToUnzip))) {

            File zipFile = new File(fileToUnzip);
            File parenDirectory = zipFile.getParentFile();

            // iterate through each entry in the zip file
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(parenDirectory, fileName);

                // create directories if needed
                new File(newFile.getParent()).mkdirs();

                // write the file
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public void unzipFolderWithDest(String fileToUnzip, String endFolder) throws IOException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileToUnzip))) {

            // create output directory if it doesn't exist
            File folder = new File(endFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // iterate through each entry in the zip file
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(endFolder + File.separator + fileName);

                if (zipEntry.isDirectory()) {
                    // create directories if it's a directory
                    newFile.mkdirs();
                } else {
                    // create directories if needed
                    new File(newFile.getParent()).mkdirs();

                    // write the file
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }

                    // check if the file is a zip file and unzip it
                    if (fileName.toLowerCase().endsWith(".zip")) {
                        String nestedEndFolder = endFolder + File.separator + fileName.substring(0, fileName.length() - 4);
                        unzipFolderWithDest(newFile.getAbsolutePath(), nestedEndFolder);
                    }
                }

                zipEntry = zipInputStream.getNextEntry();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void licenseDataToJSONFile(UserCard user, ComputerProperties pc, AppProperties app, String outputFile) throws IOException, NoSuchAlgorithmException, KeyStoreException {

        LicenseData ld = new LicenseData(user, pc, app);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        writeToFile(gson.toJson(ld).getBytes(), outputFile);

    }

    public JsonObject JSONFiletoJSONObj(String jsonDataFile) throws IOException {
        FileReader reader = new FileReader(jsonDataFile);

        return new Gson().fromJson(reader, JsonObject.class);
    }

    public LicenseData JSONFiletoLicenseData(String jsonDataFile) throws IOException {
        FileReader reader = new FileReader(jsonDataFile);

        return new Gson().fromJson(reader, LicenseData.class);
    }

    public JsonObject currentDataToJSON(String jarAppFile, String email, String appName, String version) throws PTEID_Exception, IOException, FileNotFoundException, NoSuchAlgorithmException {

        LicenseData ld = new LicenseData(new UserCard(email), new ComputerProperties(), new AppProperties(appName, version));
        Gson gson = new Gson();

        JsonObject licenseJsonObject = new JsonObject();

        licenseJsonObject.add("data", gson.toJsonTree(ld));
        return licenseJsonObject;
    }

    public void createFolder(String folder) {
        new File(folder).mkdir();
    }

    public String getJarFileName() {

        File[] files = new File(distFolder).listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                return file.getAbsolutePath();
            }
        }
        return "";
    }

    public void addNewKeyStore(String email, Scanner sc, String dataFolder) throws KeyStoreException, NoSuchAlgorithmException, Exception {
        String password;
        String aux;
        String alias = "ReplacementUserKeyPair";
        String keyStoreFileName = licenseRep + "/keyStore/KeyStore_" + email;
        String replacementUserCertificate = dataFolder + "/replacementUserCertificate";
        while (true) {
            System.out.println(email + " --> Introduza a password para a sua licença de utilização:");
            aux = sc.nextLine();
            if (aux.length() > 5) {
                password = aux;
                break;
            } else {
                System.out.println("!!Caracteres insuficientes!!");
            }
        }

        AssymetricCipher aCipher = new AssymetricCipher();

        createFolder(licenseRep + "/keyStore");

        aCipher.genKeyStore(aCipher.genKeyPair(), password, alias, keyStoreFileName);

        aCipher.exportCertificateFromKeyStore(keyStoreFileName, password, alias, replacementUserCertificate);

    }

    public void updateFile(String fileToUpdate, String appVersion, String hash) throws IOException, Exception {
        File versionInfo = new File(fileToUpdate);
        if (!versionInfo.exists()) {
            versionInfo.createNewFile();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileToUpdate))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(">")[0].equals(appVersion)) {
                    if (line.split(">")[1].equals(hash)) {
                        throw new Exception("App Version already registered!");
                    } else {
                        throw new Exception("App Version already used to diferent Application distribution (Check if the given version is correct)!");
                    }

                }
            }

        } catch (IOException e) {
            throw e;
        }

        FileWriter writer = new FileWriter(versionInfo, true);

        writer.write(appVersion + ">" + hash + "\n");
        writer.close();
    }

    public String listApps() {
        String appRep = licenseRep;
        String list = "";

        File folder = new File(appRep);
        File[] apps = folder.listFiles();
        int aux = 0;
        for (File app : apps) {
            if (app.isDirectory()) {
                if (new File(app.getAbsolutePath() + "/version_info.txt").exists()) {
                    aux++;
                    list += "  " + aux + " - " + app.getName() + "\n";
                }
            }
        }
        return list;
    }

    public String createPurchaseFileName(String dir) {
        String purchaseFinal = dir + "/purchase";
        File[] purchaseFiles = new File(dir).listFiles();
        String pattern = "^purchase\\d+\\.json$";
        int cont = 1;
        for (File purchaseFile : purchaseFiles) {
            if (purchaseFile.isFile()) {
                Pattern regex = Pattern.compile(pattern);
                if (regex.matcher(purchaseFile.getName()).matches()) {
                    cont++;
                }
            }
        }
        return purchaseFinal + cont + ".json";
    }

    public void purchaseDataToJSONFile(UserCard user, AppProperties app, String outputFile) throws IOException, NoSuchAlgorithmException, KeyStoreException {

        LicenseData ld = new LicenseData(user, app);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        writeToFile(gson.toJson(ld).getBytes(), outputFile);

    }

    public boolean checkExistingLine(String file, String searchLine) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contentEquals(searchLine)) {
                    return true;
                }
            }

        } catch (IOException e) {
            throw e;
        }
        return false;
    }

    public ArrayList<File> getAllJSONspurchases(String folder) throws IOException {
        ArrayList<File> lcArray = new ArrayList<>();
        for (File json : new File(folder).listFiles()) {
            String pattern = "^purchase\\d+\\.json$";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(json.getName());
            if (matcher.matches()) {
                lcArray.add(json);
            }
        }
        return lcArray;
    }

    void licenseDataToJSONFileWithValidity(UserCard user, ComputerProperties pc, AppProperties app, String validity, String outputFile) throws IOException {
        LicenseData ld = new LicenseData(user, pc, app);
        ld.setValidity(validity);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        
        writeToFile(gson.toJson(ld).getBytes(), outputFile);
    }
}
