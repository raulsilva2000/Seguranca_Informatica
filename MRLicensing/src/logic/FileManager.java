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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import crypto.AssymetricCipher;
import dataCollecting.UserCard;
import dataCollecting.AppProperties;
import dataCollecting.ComputerProperties;
import dataCollecting.LicenseData;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import pt.gov.cartaodecidadao.PTEID_Exception;

/**
 *
 * @author Miguel
 */
public class FileManager {

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

    public byte[] zip(String folder) throws IOException {
        byte[] inputBytes = readFileToBytes(folder);
        //zip
        byte[] outputBytes = inputBytes;//change this
        return outputBytes;
    }

    public byte[] unzip(String file) throws IOException {
        byte[] inputBytes = readFileToBytes(file);
        //unzip
        byte[] outputBytes = inputBytes;//change this
        return outputBytes;
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

    public void unzipFileWithDest(String fileToUnzip, String endFolder) {
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
                        unzipFileWithDest(newFile.getAbsolutePath(), nestedEndFolder);
                    }
                }

                zipEntry = zipInputStream.getNextEntry();
            }

            System.out.println("File successfully unzipped!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void licenseDataToJSONFile(UserCard user, ComputerProperties pc, AppProperties app, String outputFile, Scanner sc) throws IOException, NoSuchAlgorithmException {

        /////
        /*
        Gson gson = new Gson();
        String jsonInfoApp = gson.toJson(infoAppUtilizador);
        String jsonSistema = gson.toJson(sistema);
        Gson gsonAux = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonUtilizador = gsonAux.toJson(utilizador);
        String jsonAllData = "{\n"
                + "\"AppInfoUtilizador\": " + jsonInfoApp + ",\n \"Sistema\":" + jsonSistema + ",\n \"Utilizador\":" + jsonUtilizador + "\n}";
        doFile(pathJson, jsonAllData.getBytes());
         */
        /////
        LicenseData ld = new LicenseData(user, pc, app);

        //gerar par de chaves e adicionar ao json a chave publica
        AssymetricCipher assymCip = new AssymetricCipher();
        KeyPair keyPair = assymCip.genKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //JsonObject licenseJsonObject = new JsonObject();
        ld.setUserPublicKey(publicKey);

        writeToFile(gson.toJson(ld).getBytes(), outputFile);
        /*
        FileWriter writer = new FileWriter(outputFile);
        gson.toJson(licenseJsonObject, writer);
         */
        addNewPrivateKey(keyPair.getPrivate(), user.getEmail(), sc);
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
        File folderDir = new File("dist");
        File[] files = folderDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                return file.getAbsolutePath();
            }
        }
        return "";
    }

    private void addNewPrivateKey(PrivateKey privateKey, String email, Scanner sc) {
        String password;
        String aux;
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

        //PBE
        AssymetricCipher aCipher = new AssymetricCipher();
        byte[] salt = aCipher.generateRandomSalt();

        createFolder("LicenseRep/privateKey");

        try {
            writeToFile(salt, "LicenseRep/privateKey/salt_" + email + ".txt");
            writeToFile(aCipher.protectPrivateKey(privateKey, password, salt), "LicenseRep/privateKey/PBE_PK_" + email + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
