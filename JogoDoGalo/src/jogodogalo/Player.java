/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogodogalo;

/**
 *
 * @author Miguel
 */
public class Player {
    private int id;
    private pecas pecas;

    public Player(int id,pecas pecas) {
        this.id=id;
        this.pecas=pecas;
    }
    
    public int getId(){
        return id;
    }

    public pecas getPecas() {
        return pecas;
    }
    
}
