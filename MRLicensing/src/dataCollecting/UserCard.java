/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataCollecting;
import com.google.gson.annotations.Expose;
import pt.gov.cartaodecidadao.*;

/**
 *
 * @author Miguel
 */
public class UserCard {
    @Expose
    private String fullName;
    @Expose
    private String civilNumber;
    @Expose
    private String email;
    
    /* NOTE: the following static block is strictly necessary once
    that you must explicitly load the JNI library that implements
    the features of the Java wrapper.*/
    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. \n" + e);
            System.exit(1);
        }
    }
    
    public UserCard(String email) throws PTEID_Exception{
        getDetails();
        setEmail(email);
    }
    
    private void getDetails() throws PTEID_Exception{
        PTEID_ReaderSet.initSDK();
            
        PTEID_ReaderSet readerSet = PTEID_ReaderSet.instance();
        PTEID_ReaderContext context = readerSet.getReader();

        PTEID_EIDCard card = context.getEIDCard();
        PTEID_EId eid = card.getID();

        fullName = eid.getGivenName() + " " + eid.getSurname();
        civilNumber = eid.getCivilianIdNumber();

        PTEID_ReaderSet.releaseSDK();
    }

    public String getFullName() {
        return fullName;
    }

    public String getCivilNumber() {
        return civilNumber;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String[] getMainAttributes(){
        String [] atributes={"fullName","civilNumber","email"};
        return atributes;
    }
}
