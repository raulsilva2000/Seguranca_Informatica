/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jogodogalo;

/**
 *
 * @author Miguel
 */
public class Step {
    private int [] position;
    private pecas peca;

    public Step(int[] position, pecas peca) {
        this.position = position;
        this.peca = peca;
    }

    public pecas getPeca() {
        return peca;
    }

    public int[] getPosition() {
        return position;
    }
    
    
}
