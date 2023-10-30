/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogodogalo;

/**
 *
 * @author Miguel
 */
public class tabuleiro {
    private pecas[][] casas;

    public tabuleiro() {
        this.casas=new pecas[3][3];
    }
    public boolean play(Player player,int[] casa){
        if(casas[casa[0]][casa[1]]==null){
            casas[casa[0]][casa[1]]=player.getPecas();
            return true;
        }
        return false;
    }
    
    public pecas checkWinner(){
        for (int i = 0; i < 3; i++) {
            pecas vc=verticalChecker(i);
            if(vc!=null){
                return vc;
            }
        }
        for (int i = 0; i < 3; i++) {
            pecas hc=horizontalChecker(i);
            if(hc!=null){
                return hc;
            }
        }
        pecas dc=diagonalChecker();
        if(dc!=null){
            return dc;
        }
        pecas cc=crossDiagonalChecker();
        if(cc!=null){
            return cc;
        }
        return null;
    }
    public pecas verticalChecker(int i){
        pecas check =casas[0][i];
        if(check==null){
            return null;
        }
        for (int j = 1; j < 3; j++) {
            if(check!=casas[j][i]){
                return null;
            }
        }
        return check;
    }

    public pecas horizontalChecker(int i) {
        pecas check =casas[i][0];
        if(check==null){
            return null;
        }
        for (int j = 1; j < 3; j++) {
            if(check!=casas[i][j]){
                return null;
            }
        }
        return check;
    }
    public pecas diagonalChecker(){
        pecas check =casas[0][0];
        if(check==null){
            return null;
        }
        for (int i = 1; i < 3; i++) {
            if(check!=casas[i][i]){
                return null;
            }
        }
        return check;
    }
    public pecas crossDiagonalChecker(){
        pecas check =casas[2][0];
        if(check==null){
            return null;
        }
        for (int i = 1; i < 3; i++) {
            if(check!=casas[2-i][i]){
                return null;
            }
        }
        return check;
    }
    public boolean hasSpaces(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(casas[i][j]==null){
                    return true;
                }
            }
        }
        return false;
    }

    void printTab() {
        System.out.println("");
        System.out.println("--------------------");
        System.out.println("");
        for (int i = 0; i < 3; i++) {
            String linha=" ";
            for (int j = 0; j < 3; j++) {
                linha+="  ";
                pecas cur=casas[i][j];
                if(cur==null){
                    linha+=(i+1)+"-"+(j+1);
                }else{
                    linha+=cur;
                }
            }
            System.out.println(linha);
            System.out.println("");
        }
        System.out.println("--------------------");
        System.out.println("");
    }
}
