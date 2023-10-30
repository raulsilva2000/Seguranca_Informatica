/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package jogodogalo;


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
        Player player1=new Player(0, pecas.CROSS);
        Player player2=new Player(1, pecas.BALL);
        GameHandler gameHandler=new GameHandler(player1, player2);
        gameHandler.playGamesc();
    }
    
}
