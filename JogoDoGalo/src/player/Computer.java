/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package player;

import jogodogalo.Step;
import jogodogalo.pecas;
import jogodogalo.Tabuleiro;

/**
 *
 * @author Miguel
 */
abstract class Computer extends Player {

    Tabuleiro tabuleiro;

    public Computer(int id, pecas pecas) {
        super(id, pecas);
    }

    public void play() {
        tabuleiro.play(this, bestplay());
    }
    
    private int[] bestplay() {
        pecas[][] tab = tabuleiro.getCasas();
        int[] canEnd = checkcanEnd(tab);
        if (canEnd != null) {
            return canEnd;
        } 
        return smartplay(tab);
    }

    private int[] checkcanEnd(pecas[][] tab) {
        Step[] plays = {};
        //all posible
        checkposiblevertical(tab, plays);
        checkposiblehorizontal(tab, plays);
        checkposiblecrossed(tab, plays);
        //best posible
        pecas mypeca = super.getPecas();
        for (Step play : plays) {
            if (play.getPeca().equals(mypeca)) {
                return play.getPosition();
            }
        }
        if(plays.length!=0){
            return plays[(int) Math.random() * plays.length].getPosition();
        }
        return null;
    }

    abstract public int[] smartplay(pecas[][] tab);

    private void checkposiblevertical(pecas[][] tab, Step[] plays) {
        for (int i = 0; i < 3; i++) {
            pecas peca = null;
            int contp = 0;
            int conta = 0;
            int[] nullp = new int[2];
            for (int j = 0; j < 3; j++) {
                if (tab[j][i] == null) {
                    int[] aux = {j, i};
                    nullp = aux;
                }
                if (peca == null) {
                    if (!(tab[j][i] == null)) {
                        peca = tab[j][i];
                        contp++;
                    }
                } else {
                    if (tab[j][i] == peca) {
                        contp++;
                    } else {
                        conta++;
                    }
                }
            }
            if (conta == 0 && contp == 2) {
                plays[plays.length] = new Step(nullp, peca);
            }
        }
    }

    private void checkposiblehorizontal(pecas[][] tab, Step[] plays) {
        for (int j = 0; j < 3; j++) {
            pecas peca = null;
            int contp = 0;
            int conta = 0;
            int[] nullp = new int[2];
            for (int i = 0; i < 3; i++) {
                if (tab[j][i] == null) {
                    int[] aux = {j, i};
                    nullp = aux;
                }
                if (peca == null) {
                    if (!(tab[j][i] == null)) {
                        peca = tab[j][i];
                        contp++;
                    }
                } else {
                    if (tab[j][i] == peca) {
                        contp++;
                    } else {
                        conta++;
                    }
                }
            }
            if (conta == 0 && contp == 2) {
                plays[plays.length] = new Step(nullp, peca);
            }
        }
    }

    private void checkposiblecrossed(pecas[][] tab, Step[] plays) {
        pecas peca = null;
        int contp = 0;
        int conta = 0;
        int[] nullp = new int[2];
        for (int i = 0; i < 3; i++) {
            int j = i;
            if (tab[j][i] == null) {
                int[] aux = {j, i};
                nullp = aux;
            }
            if (peca == null) {
                if (!(tab[j][i] == null)) {
                    peca = tab[j][i];
                    contp++;
                }
            } else {
                if (tab[j][i] == peca) {
                    contp++;
                } else {
                    conta++;
                }
            }
        }
        if (conta == 0 && contp == 2) {
                plays[plays.length] = new Step(nullp, peca);
        }
        peca = null;
        contp = 0;
        conta = 0;
        nullp = new int[2];
        for (int i = 0; i < 3; i++) {
            int j = 2-i;
            if (tab[j][i] == null) {
                int[] aux = {j, i};
                nullp = aux;
            }
            if (peca == null) {
                if (!(tab[j][i] == null)) {
                    peca = tab[j][i];
                    contp++;
                }
            } else {
                if (tab[j][i] == peca) {
                    contp++;
                } else {
                    conta++;
                }
            }
        }
        if (conta == 0 && contp == 2) {
                plays[plays.length] = new Step(nullp, peca);
        }
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }
    
}
