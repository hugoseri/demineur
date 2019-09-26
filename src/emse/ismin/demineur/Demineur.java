package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.SocketHandler;

public class Demineur extends JFrame implements Runnable{

    private static final String FILENAME = "files/best_scores.txt";

    Thread threadOnline;
    DataInputStream entreeOnline;
    DataOutputStream sortieOnline;

    public static final int MSG = 0;
    public static final int REFUSE = 403;
    public static final int START = 999;
    public static final int FINISH = 888;

    int dim_x = 10;
    int dim_y = 10;
    int nb_mines = 5;
    GUI gui;

    String[] args;

    public String defaultHost = "localhost";
    public int defaultPort = 10000;
    public String defaultPseudo = "Michel";

    private int numJoueur;

    public boolean connected = false;

    int score = 0;

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
        //updateFile();
        getGUI().stopCompteur();
        JOptionPane.showConfirmDialog(null,
                "BRAVO ! T'as gagné... \n Score : "+getGUI().getValCompteur(),
                "Champion !",
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
            Socket sock = new Socket(host, Integer.parseInt(port));
            entreeOnline = new DataInputStream(sock.getInputStream());
            sortieOnline = new DataOutputStream(sock.getOutputStream());
            if (pseudo.length() > 0) {
                sortieOnline.writeUTF(pseudo);
            } else {
                sortieOnline.writeUTF("Trololol");
            }
            numJoueur = entreeOnline.readInt();

            if (numJoueur == 403) { // accès refusé
                popUpconnexionEchoue(host, port);
            } else {
                popUpconnexionReussie(host, port, numJoueur);
                connected = true;
                threadOnline = new Thread(this);
                threadOnline.start();
            }


        } catch (UnknownHostException e){
            popUpconnexionEchoue(host, port);
        } catch (IOException e){
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
                //commande correspondant à une case à afficher
                System.out.println(input);
                System.out.println(cmd[0]);
                if (Integer.parseInt(cmd[0]) < 10) { // info case à afficher
                    int x = Integer.parseInt(cmd[1]);
                    int y = Integer.parseInt(cmd[2]);
                    int etat = Integer.parseInt(cmd[0]);
                    getGUI().getPanelMines().getTabCases()[x][y].showCase(etat);
                    if (Integer.parseInt(cmd[0]) == 9 && Integer.parseInt(cmd[3]) == numJoueur){
                        setLost(true);
                        perdu();
                    }
                } else if (Integer.parseInt(cmd[0]) == START) { // info début partie
                    relancer(Level.EASY);
                    start();
                } else if (Integer.parseInt((cmd[0])) == FINISH) { //fin de partie
                    if (Integer.parseInt(cmd[1]) == numJoueur){
                        setWon(true);
                        gagne();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //boucle infinie tq process non nul
        //lecture dans in
        //selon ce qui est lu: affiche mines, numéros, fin de partie
        //lecture joueur qui a cliqué a tel endroit...

    }

    private void popUpconnexionReussie(String host, String port, int numJoueur){
        System.out.println("Connexion réussie à " + host + ":" + port +". Vous êtes le joueur numéro " + numJoueur);
        JOptionPane.showConfirmDialog(null,
                "Tu es bien connecté sur "+host+":"+port+".\n" +
                        "Tu es le joueur numéro "+numJoueur+".",
                "Connexion réussie",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void popUpconnexionEchoue(String host, String port){
        System.out.println("Connexion échouée à " + host + ":" + port +".");
        JOptionPane.showConfirmDialog(null,
                "La connexion à "+host+":"+port+" a échoué...",
                "Connexion échouée",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    public void deconnexionServeur(){
        /*
        Socket sock = new Socket(host, Integer.parseInt(port));
        DataInputStream entree = new DataInputStream(sock.getInputStream());
        DataOutputStream sortie = new DataOutputStream(sock.getOutputStream());
        if (pseudo.length() > 0) {
            sortie.writeUTF(pseudo);
        } else {
            sortie.writeUTF("Trololol");
        }
        int numJoueur = entree.readInt();
         */

        // à mettre à false si déconnexion réussie uniquement
        connected = false;

        System.out.println("Déconnexion réussie");
        JOptionPane.showConfirmDialog(null,
                "Tu as bien été déconnecté.",
                "Déconnexion réussie",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        /*
        entree.close();
        sortie.close();
        sock.close();
         */
    }

    /*
    public void updateFile(){
        Path path = Paths.get(FILENAME);

        if (!Files.exists(path)){
            for (int i=0; i < Level.values().length ; i++){
                //
            }
        }

        try {
            FileOutputStream file = new FileOutputStream(path);
            BufferedOutputStream buff = new BufferedOutputStream(file);
            DataOutputStream data = new DataOutputStream(buff);
            try {
                //data.writeInt(gui.getValCompteur());
                data.writeInt(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

     */
}