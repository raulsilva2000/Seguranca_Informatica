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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class FileManager {
    private static String distFolder = (new File(MRLicensing.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath().replace("\\", "/");
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

    public void unzipFolder(String fileToUnzip) {
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
            e.printStackTrace();
        }
    }

    public void licenseDataToJSONFile(UserCard user, ComputerProperties pc, AppProperties app, String outputFile) throws IOException, NoSuchAlgorithmException, KeyStoreException {

        LicenseData ld = new LicenseData(user, pc, app);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        writeToFile(gson.toJson(ld).getBytes(), outputFile);

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
        String alias="ReplacementUserKeyPair";
        String keyStoreFileName=licenseRep+"/keyStore/KeyStore_" + email;
        String replacementUserCertificate=dataFolder+"/replacementUserCertificate";
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

        createFolder(licenseRep+"/keyStore");

        aCipher.genKeyStore(aCipher.genKeyPair(), password, alias, keyStoreFileName);
        
        aCipher.exportCertificateFromKeyStore(keyStoreFileName, password, alias, replacementUserCertificate);

    }
}
