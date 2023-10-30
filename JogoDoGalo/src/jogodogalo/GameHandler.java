/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogodogalo;

import java.util.Scanner;

/**
 *
 * @author Miguel
 */
public class GameHandler {
    private Player player1;
    private Player player2;
    private tabuleiro tabuleiro;

    public GameHandler(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        tabuleiro=new tabuleiro();
    }
    
    public void playGamesc(){
        Player activePlayer=player1;
        Scanner sc=new Scanner(System.in);
        do{
            boolean played;
            do{
                System.out.println("Player: "+(activePlayer.getId()+1)+"   pecas: "+activePlayer.getPecas());
                tabuleiro.printTab();
                System.out.println("coluna e linha (_ _)");
                String[] read=sc.nextLine().split("-");
                int[] array={Integer.parseInt(read[0])-1,Integer.parseInt(read[1])-1};
                played=tabuleiro.play(activePlayer, array);
            }while(!played);
            
            if(tabuleiro.checkWinner()!=null){
                tabuleiro.printTab();
                System.out.println("");
                System.out.println("O jogador "+(activePlayer.getId()+1)+" GANHOU!!!");
                break;
            }
  
            if(activePlayer==player1){
                activePlayer=player2;
            }
            else{
                activePlayer=player1;
            }
        }
        while(tabuleiro.hasSpaces());
        sc.close();
    }
}
