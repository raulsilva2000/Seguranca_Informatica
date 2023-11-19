/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package licenselibrary;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utilizador
 */
public class LicenseLibrary {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        SymmetricCipher cipher = new SymmetricCipher();
        
        try {
            cipher.genKey("key.txt");
            cipher.genVector("vector.txt");
            cipher.cipherFile("content.txt", "encrypted.txt", "key.txt", "vector.txt");
            cipher.decipherFile("encrypted.txt", "decrypted.txt", "key.txt", "vector.txt");
        } catch (Exception e) {
            System.out.println(e);
        }
        
        AssymetricCipher cipher2 = new AssymetricCipher();

        try {
            cipher2.genKey("publicKey.txt", "privateKey.txt");
            cipher2.cipherFile("content.txt", "encrypted2.txt", "publicKey.txt");
            cipher2.decipherFile("encrypted2.txt", "decrypted2.txt", "privateKey.txt");
        } catch (Exception e) {
            System.out.println(e);
        }
        
        */
        try {
            ComputerProperties pc = new ComputerProperties();
            System.out.println(pc.getNumberOfCPUs() + " " + pc.getMacAddress());
        } catch (Exception e) {
            System.out.println(e);
        }
        
        /*
        try {
            AppProperties app = new AppProperties("LicenseLibrary.jar");
            System.out.println(app.getName() + " " + app.getVersion() + " " + app.getManufacturer());
            System.out.println("Hash: " + app.getHash());
        } catch (Exception e) {
            System.out.println(e);
        }
        */
    }
    
}
