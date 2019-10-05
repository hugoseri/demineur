package emse.ismin.demineur;

import javax.naming.InsufficientResourcesException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Demineur extends JFrame implements Runnable{

    Thread threadOnline;
    DataInputStream entreeOnline;
    DataOutputStream sortieOnline;
    Socket sock;
    String pseudo;

    private int nbJoueursEnCours = 0;

    public static final int MSG = 0;
    public static final int REFUSE = 403;
    public static final int START = 999;
    public static final int FINISH = 888;
    public static final int QUIT = 777;
    public static final int PLAYED = 111;

    GUI gui;

    String[] args;

    public String defaultHost = "localhost";
    public int defaultPort = 10000;
    public String defaultPseudo = "Michel";

    public int numJoueur;

    public boolean connected = false;

    Level level = Level.EASY;

    private Champ champ = new Champ(level);

    private boolean started = false;
    public boolean isStarted(){
        return started;
    }
    public void setStarted(boolean started){
        this.started = started;
    }

    private boolean lost = false;
    public boolean isLost(){
        return lost;
    }
    public void setLost(boolean lost){
        this.lost = lost;
    }

    private boolean won = false;
    public boolean isWon(){
        return won;
    }
    public void setWon(boolean won){
        this.won = won;
    }

    public Champ getChamp() {
        return champ;
    }

    public GUI getGUI() { return gui;}

    public Demineur(String[] args) {


        super("Démineur");
        gui = new GUI(this);

        setContentPane(gui);

        this.args = args;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        setVisible(true);


        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (connected){
                    deconnexionServeur();
                }
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("Lancement de l'interface du démineur.");
        System.out.println("");

        new Demineur(args);
    }

    public void start(){
        setStarted(true);
        getGUI().startCompteur();
    }

    public void resetCompteur(){
        getGUI().stopCompteur();
        getGUI().resetCompteur();
        setStarted(false);
    }

    public void quit() {
        int rep = JOptionPane.showConfirmDialog(null,
                "Etes-vous sur de vouloir quitter le jeu ?",
                "Quitter le démineur",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (rep == JOptionPane.YES_OPTION) {
            System.out.println("Merci d'avoir jouer.");
            System.exit(0);
        }
    }

    public void perdu() {
        getGUI().stopCompteur();
        JOptionPane.showConfirmDialog(null,
                "BOUM ! T'as tout fait pété...",
                "Trop nul !",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void gagne() {
        getGUI().stopCompteur();
        JOptionPane.showConfirmDialog(null,
                "BRAVO ! T'as gagné... \n Score : "+getGUI().getValCompteur(),
                "Champion !",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void gagne(int score) {
        getGUI().stopCompteur();
        JOptionPane.showConfirmDialog(null,
                "BRAVO ! T'as gagné... \n Score : "+score,
                "Champion !",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void serveurDeconnecte(){
        quitCo();
        getGUI().stopCompteur();
        JOptionPane.showConfirmDialog(null,
                "Désolé, le serveur s'est arrêté :(.",
                "Serveur déconnecté.",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void relancer() {
        setLost(false);
        setWon(false);
        resetCompteur();
        getChamp().renouvelleChamp();
        newPartie();
    }

    public void relancer(Level level) {
        setLost(false);
        setWon(false);
        resetCompteur();
        getChamp().newPartie(level);
        newPartie(level);
    }

    /**
     * Demande à toutes les cases de se réinitialiser.
     */
    private void newPartie() {
        for (int i = 0; i < getChamp().getDimX(); i++) {
            for (int j = 0; j < getChamp().getDimY(); j++) {
                gui.getPanelMines().getTabCases()[i][j].newPartie();
            }
        }
    }

    /**
     * Demande à toutes les cases de se réinitialiser.
     */
    private void newPartie(Level level) {
        gui.getPanelMines().removeAll();
        gui.getPanelMines().placeCases(this);
        pack();
    }

    public void connexionServeur(String host, String port, String pseudo){
        try {
            sock = new Socket(host, Integer.parseInt(port));
            entreeOnline = new DataInputStream(sock.getInputStream());
            sortieOnline = new DataOutputStream(sock.getOutputStream());
            if (pseudo.length() > 0) {
                sortieOnline.writeUTF(pseudo);
                this.pseudo = pseudo;
            } else {
                sortieOnline.writeUTF("Trololol");
            }
            numJoueur = entreeOnline.readInt();

            if (numJoueur == 403) { // accès refusé
                popUpconnexionEchoue(host, port);
                gui.addMsg_online("Connexion à "+host+":"+port+" échouée.");
            } else {
                popUpconnexionReussie(host, port, numJoueur);
                gui.addMsg_online("Connexion à "+host+":"+port+" réussie. Vous êtes le joueur " + numJoueur);
                connected = true;
                threadOnline = new Thread(this);
                threadOnline.start();
            }


        } catch (UnknownHostException e){
            gui.addMsg_online("Connexion à "+host+":"+port+" échouée.");
            popUpconnexionEchoue(host, port);
        } catch (IOException e){
            gui.addMsg_online("Connexion à "+host+":"+port+" échouée.");
            popUpconnexionEchoue(host, port);
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(threadOnline != null){
            try {
                String input = entreeOnline.readUTF();
                String[] cmd = input.split("\\s+");
                if (Integer.parseInt(cmd[0]) < 10) { // info case à afficher
                    int x = Integer.parseInt(cmd[1]);
                    int y = Integer.parseInt(cmd[2]);
                    int etat = Integer.parseInt(cmd[0]);
                    Color color = new Color(Integer.parseInt(cmd[5]), Integer.parseInt(cmd[6]), Integer.parseInt(cmd[7]));
                    getGUI().getPanelMines().getTabCases()[x][y].showCase(etat, color);
                    getGUI().updateScore(Integer.parseInt(cmd[3]), cmd[4], color);
                    if (Integer.parseInt(cmd[0]) == 9) {
                        if (Integer.parseInt(cmd[3]) == numJoueur && nbJoueursEnCours != 1) {
                            setLost(true);
                            perdu();
                            gui.addMsg_online("Partie perdue.");
                        } else if (Integer.parseInt(cmd[3]) != numJoueur){
                            gui.addMsg_online("Le joueur " + cmd[3] + " a perdu.");
                        }
                        nbJoueursEnCours -- ;
                    }
                } else if (Integer.parseInt(cmd[0]) == START) { // info début partie
                    relancer(Level.valueOf(cmd[2]));
                    nbJoueursEnCours = Integer.parseInt(cmd[1]);
                    gui.addMsg_online("Démarrage partie.");
                    gui.initScore(nbJoueursEnCours);
                    start();
                } else if (Integer.parseInt(cmd[0]) == QUIT){ //un joueur a quitté la partie
                    if (Integer.parseInt(cmd[1]) != numJoueur){
                        if (Integer.parseInt(cmd[1]) == 0){
                            serveurDeconnecte();
                            gui.addMsg_online("La partie a été coupé par le serveur.");
                        } else {
                            nbJoueursEnCours -- ;
                            getGUI().updateScore(Integer.parseInt(cmd[1]), String.valueOf(QUIT), Color.WHITE);
                            gui.addMsg_online("Le joueur " + cmd[1] + " a quitté la partie.");
                        }
                    }
                } else if (Integer.parseInt((cmd[0])) == FINISH) { //fin de partie
                    if (Integer.parseInt(cmd[1]) == numJoueur){
                        setWon(true);
                        gagne(Integer.parseInt(cmd[2]));
                        gui.addMsg_online("Bravo! T'as gagné.");
                    } else {
                        gui.addMsg_online("Le joueur " + cmd[1] + " a gagné avec un score de " + cmd[2] + ".");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void popUpconnexionReussie(String host, String port, int numJoueur){
        JOptionPane.showConfirmDialog(null,
                "Tu es bien connecté sur "+host+":"+port+".\n" +
                        "Tu es le joueur numéro "+numJoueur+".",
                "Connexion réussie",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void popUpconnexionEchoue(String host, String port){
        JOptionPane.showConfirmDialog(null,
                "La connexion à "+host+":"+port+" a échoué...",
                "Connexion échouée",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    public void deconnexionServeur(){
        try {
            sortieOnline.writeUTF(String.valueOf(QUIT));
            gui.addMsg_online("Déconnexion réussie");
            quitCo();
            JOptionPane.showConfirmDialog(null,
                    "Tu as bien été déconnecté.",
                    "Déconnexion réussie",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e){
            JOptionPane.showConfirmDialog(null,
                    "Tu n'as pas été déconnecté.",
                    "Déconnexion échouée",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    private void quitCo(){
        connected = false;
        try {
            gui.boutonOnline.setText("Connexion");
            threadOnline = null;
            entreeOnline.close();
            sortieOnline.close();
            sock.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}