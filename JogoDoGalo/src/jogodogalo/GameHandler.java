/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogodogalo;

import player.Player;
import java.util.Scanner;
import player.ComputerEasy;
import player.ComputerHard;
import player.ComputerMedium;

/**
 *
 * @author Miguel
 */
public class GameHandler {

    private Player player1;
    private Player player2;
    private Tabuleiro tabuleiro;

    public GameHandler(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        tabuleiro = new Tabuleiro();
        sincComp();
    }

    public void playGamesc() {
        Player activePlayer = player1;
        Scanner sc = new Scanner(System.in);
        do {
            boolean played;
            if (activePlayer instanceof ComputerEasy) {
                ((ComputerEasy) activePlayer).play();
                
            } else {
                do {
                    System.out.println("Player: " + (activePlayer.getId() + 1) + "   pecas: " + activePlayer.getPecas());
                    tabuleiro.printTab();
                    System.out.println("coluna e linha (_ _)");
                    String[] read = sc.nextLine().split("-");
                    int[] array = {Integer.parseInt(read[0]) - 1, Integer.parseInt(read[1]) - 1};
                    played = tabuleiro.play(activePlayer, array);
                } while (!played);
            }

            if (tabuleiro.checkWinner() != null) {
                tabuleiro.printTab();
                System.out.println("");
                System.out.println("O jogador " + (activePlayer.getId() + 1) + " GANHOU!!!");
                break;
            }

            if (activePlayer == player1) {
                activePlayer = player2;
            } else {
                activePlayer = player1;
            }
        } while (tabuleiro.hasSpaces());
        sc.close();
    }

    private void sincComp() {
        if (player1 instanceof ComputerEasy) {
            ComputerEasy com = (ComputerEasy) player1;
            com.setTabuleiro(tabuleiro);
        } else if (player1 instanceof ComputerMedium) {
            ComputerMedium com = (ComputerMedium) player1;
            com.setTabuleiro(tabuleiro);
        } else if (player1 instanceof ComputerHard) {
            ComputerHard com = (ComputerHard) player1;
            com.setTabuleiro(tabuleiro);
        } else if (player2 instanceof ComputerEasy) {
            ComputerEasy com = (ComputerEasy) player2;
            com.setTabuleiro(tabuleiro);
        } else if (player2 instanceof ComputerMedium) {
            ComputerMedium com = (ComputerMedium) player2;
            com.setTabuleiro(tabuleiro);
        } else if (player2 instanceof ComputerHard) {
            ComputerHard com = (ComputerHard) player2;
            com.setTabuleiro(tabuleiro);
        }
    }
}
