package emse.ismin.demineur;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Serveur extends JFrame implements Runnable {

    GUIServeur guiServeur;
    int compteurJoueur = 0;
    private HashSet<DataInputStream> listInputs = new HashSet<DataInputStream>();
    private HashSet<DataOutputStream> listOutputs = new HashSet<DataOutputStream>();

    private int port = 10000;

    private Champ champJeu;

    public boolean partieTerminee = false;
    public boolean partieCommencee = false;

    private int[] etatJoueurs;
    final private int PERDU = 0;
    final private int ENCOURS = 1;
    final private int GAGNE = 2;

    private ServerSocket gestSocket;

    public Serveur() {
        System.out.println("Démarrage du serveur.");

        guiServeur = new GUIServeur(this);

        setContentPane(guiServeur);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        setVisible(true);

        startServeur();
    }

    public static void main(String[] args) {

        new Serveur();

    }

    public void startServeur() {
        guiServeur.addMsg("Attente des joueurs");

        try {
            gestSocket = new ServerSocket(port);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            //attente
            Socket socket = gestSocket.accept();
            guiServeur.addMsg("Nouveau joueur");

            new Thread(this).start();

            //ouverture des streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //lecture donnée client
            String nomJoueur = in.readUTF();

            if (!partieCommencee) {

                guiServeur.addMsg(nomJoueur + " est connecté.");

                compteurJoueur++;
                int numJoueur = compteurJoueur;

                //envoi d'une donnée
                out.writeInt(numJoueur);

                listInputs.add(in);
                listOutputs.add(out);

                while (!partieTerminee) {
                    try {
                        if (partieCommencee && aGagne(numJoueur)) {
                            partieTerminee = true;
                            etatJoueurs[numJoueur - 1] = GAGNE;
                            guiServeur.addMsg("Joueur " + numJoueur + " a gagné !");
                        } else {
                            String[] caseCliquee = in.readUTF().split("\\s+");
                            broadcastCaseCliquee(caseCliquee, numJoueur);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                broadcastGagnant();

            } else {
                out.writeInt(Demineur.REFUSE);
                guiServeur.addMsg(nomJoueur + " a tenté de rejoindre, trop tard!");
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    private void broadcastGagnant(){
        for (DataOutputStream sortie : listOutputs){
            try {
                String msg = Demineur.FINISH + " " + getGagnant();
                guiServeur.addMsg("Message broadcasté : "+msg);
                sortie.writeUTF(msg);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    synchronized private void broadcastCaseCliquee(String[] caseCliquee, int numJoueur){
        for (DataOutputStream sortie : listOutputs){
            try {
                String msg = codeCase(caseCliquee, numJoueur);
                guiServeur.addMsg("Message broadcasté : "+msg);
                sortie.writeUTF(msg);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String codeCase(String[] caseCliquee, int numJoueur){
        int x = Integer.parseInt(caseCliquee[0]);
        int y = Integer.parseInt(caseCliquee[1]);
        boolean isMine = champJeu.isMine(x, y);
        int nb_mines = 9;
        if (!isMine){
            nb_mines = champJeu.minesAutour(x, y);
        } else {
            etatJoueurs[numJoueur-1] = PERDU;
        }
        String msg = (isMine ? 9 : nb_mines) + " " + x + " " + y + " " + numJoueur;
        return msg;
    }

    private boolean aGagne(int numJoueur){
        boolean victoire = true;

        int i=0;
        while(i<compteurJoueur){
            if (i != numJoueur-1 && etatJoueurs[i] != PERDU){
                victoire = false;
            }
            i++;
        }
        return victoire;
    }

    private int getGagnant(){
        int numGagnant = 0;
        int i = 0;
        while (numGagnant==0 && i<compteurJoueur){
            if (etatJoueurs[i] == GAGNE){
                numGagnant = i;
            }
            i++;
        }
        return numGagnant+1;
    }

    private void quit() {
        for (DataInputStream entree : listInputs) {
            try {
                entree.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (DataOutputStream sortie : listOutputs) {
            try {
                sortie.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            gestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newGame(){
        champJeu = new Champ();
        champJeu.newPartie(Level.EASY);
        champJeu.affText();
        partieCommencee = true;
        for (DataOutputStream sortie : listOutputs){
            try {
                sortie.writeUTF(String.valueOf(Demineur.START));
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        etatJoueurs = new int[compteurJoueur];
        for (int i=0; i<compteurJoueur; i++){
            etatJoueurs[i] = 1;
        }
    }
}

