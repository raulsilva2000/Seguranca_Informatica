/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package jogodogalo;

import java.io.File;
import java.util.Scanner;
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
        MRLicensing mrlicensing = new MRLicensing();
        mrlicensing.init("Jogo do galo", "1.3.0");

        Scanner sc = new Scanner(System.in);
        String opcao = "";
        boolean licencaValida = false;
        do {
            System.out.println("==============================");
            System.out.println("       Menu de opcoes");
            System.out.println("==============================\n");
            System.out.println(" 1- Realizar Pedido de licenca");
            System.out.println(" 2- Validar Licenca");
            System.out.println(" 3- Mostrar Informacao da Licenca Atual");
            System.out.println(" 4- Continuar para o Jogo");
            System.out.println(" 5- Cancelar e sair\n");
            System.out.println("==============================");
            System.out.println("         Fim do menu");
            System.out.println("==============================");
            
            opcao = sc.nextLine();

            switch (opcao) {
                case "1":
                    mrlicensing.startRegistration();
                    break;
                case "2":
                    licencaValida=mrlicensing.isRegistered();
                    if (licencaValida){
                        System.out.println("A licenca e valida! :)");
                    }
                    else{
                        System.out.println("Nao foi encontrada uma licenca valida!");
                        System.out.println("Certifique-se que tem a licenca no diretorio LicenseRep");
                        System.out.println("E que e o unico ficheiro zip comecado por MRLic_");
                    }
                    break;
                case "3":
                    mrlicensing.showLicenseInfo();
                    break;
                case "4":
                    if(!licencaValida){
                        System.out.println("Ainda nao validou a licenca!");
                        System.out.println("Selecione a opcao 2 para validar a sua licenca!");
                        System.out.println("Se nao tiver nenhuma licenca selecione a opcao 1 para pedir uma nova \ne comunique com gestor de licencas para receber a sua licenca de utilizacao!");
                    }
                    continue;
                case "5":
                    System.exit(0);
                    break;
                default:
                    System.out.println("opcao nao reconhecida!");
            }
        } while (!(opcao.equals("4") && licencaValida));

        
        Player player1=new Player(0, pecas.CROSS);
        ComputerEasy player2=new ComputerEasy(1, pecas.BALL);
        GameHandler gameHandler=new GameHandler(player1, player2);
        gameHandler.playGamesc();
    }

}
