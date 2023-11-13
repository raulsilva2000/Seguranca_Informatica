/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package player;

import jogodogalo.Tabuleiro;
import jogodogalo.pecas;

/**
 *
 * @author Miguel
 */
public class ComputerEasy extends Computer {

    public ComputerEasy(int id, pecas pecas) {
        super(id, pecas);
    }

    @Override
    public int[] smartplay(pecas[][] tab) {
        int i;
        int j;
        do {
            i = (int) (Math.random() * 3);
            j = (int) (Math.random() * 3);
        } while (tab[i][j] != null);
        return new int[]{i, j};
    }

    @Override
    public void setTabuleiro(Tabuleiro tabuleiro) {
        super.setTabuleiro(tabuleiro);
    }
    
}
