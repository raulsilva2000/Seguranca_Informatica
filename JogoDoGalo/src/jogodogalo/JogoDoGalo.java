/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package jogodogalo;

import java.io.File;
import player.ComputerEasy;
import player.Player;
import logic.MRLicensing;


/**
 *
 * @author Miguel
 */
public class JogoDoGalo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(System.getProperty("java.home"));
        MRLicensing mrlicensing=new MRLicensing();
        mrlicensing.init("Jogo do galo", "1.3.0");
        mrlicensing.startRegistration();
        
        /*
        Player player1=new Player(0, pecas.CROSS);
        ComputerEasy player2=new ComputerEasy(1, pecas.BALL);
        GameHandler gameHandler=new GameHandler(player1, player2);
        gameHandler.playGamesc();*/
    }
    
}
