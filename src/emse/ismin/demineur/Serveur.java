package emse.ismin.demineur;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class Serveur permettant de gérer les parties en réseau.
 */
public class Serveur extends JFrame implements Runnable {

    private static final String FILENAME = "files/historique.txt";

    GUIServeur guiServeur;
    int compteurJoueur = 0;
    private HashSet<Socket> listSockets = new HashSet<Socket>();
    private HashSet<DataInputStream> listInputs = new HashSet<DataInputStream>();
    private HashSet<DataOutputStream> listOutputs = new HashSet<DataOutputStream>();
    private HashSet<Thread> listThreads = new HashSet<Thread>();

    private int port = 10000;

    private Champ champJeu;

    public boolean partieTerminee = false;
    public boolean partieCommencee = false;
    public boolean serveurOn = false;
    private String startTime;

    private List<Integer> etatJoueurs;
    final private int PERDU = -1;
    final private int QUITTE = -2;
    final private int ENCOURS = 0;
    final private int GAGNE = -3;

    private int scoreGagnant = 0;

    private boolean broadcastFinEnvoye = false;

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

    /**
     * Fonction permettant de démarrer le serveur en réinitialisant tous ces paramètres.
     */
    public void startServeur() {
        guiServeur.addMsg("Démarrage serveur.");
        guiServeur.addMsg("Attente des joueurs.");

        if (serveurOn) {
            quit();
        }

        partieCommencee = false;
        serveurOn = true;
        compteurJoueur = 0;

        etatJoueurs = new ArrayList<>();

        try {
            gestSocket = new ServerSocket(port);
            Thread myThread = new Thread(this);
            myThread.start();
            listThreads.add(myThread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction permettant de gérer la communication avec des joueurs (clients).
     */
    @Override
    public void run() {
        try {
            //attente
            Socket socket = gestSocket.accept();
            listSockets.add(socket);
            guiServeur.addMsg("Nouveau joueur :");

            Thread myThread = new Thread(this);
            myThread.start();
            listThreads.add(myThread);

            //ouverture des streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //lecture donnée client
            String nomJoueur = in.readUTF();

            if (!partieCommencee) { // si la partie n'est pas commencée, on accepte la connexion

                guiServeur.addMsg(nomJoueur + " est connecté.");

                int idJoueur;

                compteurJoueur++;

                idJoueur = compteurJoueur;

                etatJoueurs.add(0);

                boolean joueurConnecte = true;

                //envoi d'une donnée

                int R = ThreadLocalRandom.current().nextInt(150, 256);
                int G = ThreadLocalRandom.current().nextInt(150, 256);
                int B = ThreadLocalRandom.current().nextInt(150, 256);
                String rgb = R + " " + G + " " + B;
                out.writeInt(idJoueur);

                listInputs.add(in);
                listOutputs.add(out);

                while (joueurConnecte && serveurOn) { // boucle tant que le joueur est connecté et le serveur n'est pas redémarré.
                    try {
                        if (partieCommencee && !partieTerminee && aGagne(idJoueur)) { // partie gagnée
                            partieTerminee = true;
                            partieCommencee = false;
                            scoreGagnant = etatJoueurs.get(idJoueur - 1);
                            etatJoueurs.set(idJoueur - 1, GAGNE);
                            guiServeur.addMsg(nomJoueur + " a gagné !");
                        } else { // partie en cours
                            String[] input = in.readUTF().split("\\s+");

                            if (Integer.parseInt(input[0]) == Demineur.PLAYED) { // joueur clique sur une case
                                if (!champJeu.isMine(Integer.parseInt(input[1]), Integer.parseInt(input[2]))) {
                                    etatJoueurs.set(idJoueur - 1, etatJoueurs.get(idJoueur - 1) + 1);
                                }
                                broadcastCaseCliquee(input, idJoueur, rgb);

                            } else if (Integer.parseInt(input[0]) == Demineur.QUIT) { // joueur quitte le jeu
                                joueurConnecte = false;
                                listThreads.remove(myThread);
                                listInputs.remove(in);
                                listOutputs.remove(out);
                                myThread = null;
                                in.close();
                                out.close();
                                socket.close();
                                broadcastJoueurQuitte(idJoueur);
                            }
                        }
                        if (!broadcastFinEnvoye && partieTerminee) {
                            broadcastGagnant();
                        }
                    } catch (IOException e) {
                        joueurConnecte = false;
                        e.printStackTrace();
                    }
                }
            } else { // si la partie est déjà commencée on refuse le joueur.
                out.writeInt(Demineur.REFUSE);
                guiServeur.addMsg(nomJoueur + " a tenté de rejoindre, trop tard!");
                myThread = null;
                in.close();
                out.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction permettant d'envoyer à tous les joueurs que le serveur redémarre (déconnecte tous les joueurs).
     */
    public void broadcastRedemarrageServeur() {
        for (DataOutputStream sortie : listOutputs) {
            try {
                String msg = Demineur.QUIT + " 0";
                guiServeur.addMsg("Message broadcasté : " + msg);
                sortie.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction permettant d'envoyer à tous les joueurs qu'un joueur quitte le jeu.
     *
     * @param numJoueur numéro du joueur qui quitte.
     */
    private void broadcastJoueurQuitte(int numJoueur) {
        etatJoueurs.set(numJoueur - 1, QUITTE);
        guiServeur.addMsg("Le joueur " + numJoueur + " a quitté la partie.");
        for (DataOutputStream sortie : listOutputs) {
            try {
                String msg = Demineur.QUIT + " " + numJoueur;
                guiServeur.addMsg("Message broadcasté : " + msg);
                sortie.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction permettant d'envoyer à tous les joueurs le gagnant de la partie.
     */
    private void broadcastGagnant() {
        broadcastFinEnvoye = true;
        updateFile();
        for (DataOutputStream sortie : listOutputs) {
            try {
                String msg = Demineur.FINISH + " " + getGagnant() + " " + scoreGagnant;
                guiServeur.addMsg("Message broadcasté : " + msg);
                sortie.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction permettant d'envoyer à tous les joueurs qu'une case a été cliquée par un joueur.
     *
     * @param caseCliquee coordonnées (x, y) de la case cliquée.
     * @param numJoueur   numéro du joueur ayant cliqué.
     * @param rgb         couleur du joueur ayant cliqué (couleur à afficher sur la case cliquée).
     */
    synchronized private void broadcastCaseCliquee(String[] caseCliquee, int numJoueur, String rgb) {
        for (DataOutputStream sortie : listOutputs) {
            try {
                String msg = codeCase(caseCliquee, numJoueur, rgb);
                guiServeur.addMsg("Message broadcasté : " + msg);
                sortie.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction renvoyant un message contenuant les informations nécessaires pour communiquer aux joueurs:
     * - quelle case a été cliquée,
     * - par qui,
     * - quel type de case c'est (mine, si pas mine, nombre de mines autour),
     * - la couleur de la case cliquée à afficher.
     *
     * @param caseCliquee coordonnées (x, y) de la case cliquée.
     * @param numJoueur   numéro du joueur ayant cliqué.
     * @param rgb         couleur du joueur ayant cliquée (couleur à afficher sur la case cliquée).
     * @return message renseignant les informations sur la case cliquée.
     */
    private String codeCase(String[] caseCliquee, int numJoueur, String rgb) {
        int x = Integer.parseInt(caseCliquee[1]);
        int y = Integer.parseInt(caseCliquee[2]);
        boolean isMine = champJeu.isMine(x, y);
        int nb_mines = 9;
        if (!isMine) {
            nb_mines = champJeu.minesAutour(x, y);
        } else {
            etatJoueurs.set(numJoueur - 1, PERDU);
        }
        String msg = (isMine ? 9 : nb_mines) + " " + x + " " + y + " " + numJoueur + " " + etatJoueurs.get(numJoueur - 1) + " " + rgb;
        return msg;
    }

    /**
     * Fonction permettant de tester si un joueur a gagné la partie.
     *
     * @param numJoueur numéro du joueur.
     * @return booléen informant si le joueur a gagné.
     */
    private boolean aGagne(int numJoueur) {
        boolean victoire = true;

        int i = 0;
        while (i < compteurJoueur) {
            if (i != numJoueur - 1 && etatJoueurs.get(i) >= ENCOURS) {
                victoire = false;
            }
            i++;
        }
        return victoire;
    }

    /**
     * Fonction permettant de savoir quel joueur a gagné.
     *
     * @return le numéro du joueur qui a gagné.
     */
    private int getGagnant() {
        int numGagnant = 0;
        int i = 0;
        while (numGagnant == 0 && i < compteurJoueur) {
            if (etatJoueurs.get(i) == GAGNE) {
                numGagnant = i;
            }
            i++;
        }
        return numGagnant + 1;
    }

    /**
     * Fonction  permettant de détruire les canaux de communication avec tous les joueurs (quand le serveur rédemarre).
     */
    private void quit() {
        serveurOn = false;
        partieTerminee = true;
        for (Socket socket : listSockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (DataInputStream entree : listInputs) {
            try {
                entree.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listInputs.clear();
        for (DataOutputStream sortie : listOutputs) {
            try {
                sortie.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listOutputs.clear();
        for (Thread thread : listThreads) {
            thread = null;
        }
        listThreads.clear();
        try {
            gestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction permettant de lancer une nouvelle partie.
     */
    public void newGame() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        startTime = now.format(dtf);

        guiServeur.addMsg("Démarrage partie.");
        champJeu = new Champ();
        Level level = (Level) guiServeur.levelChoice.getSelectedItem();
        champJeu.newPartie(level);
        //champJeu.affText();
        partieCommencee = true;
        partieTerminee = false;
        broadcastFinEnvoye = false;

        scoreGagnant = 0;

        for (DataOutputStream sortie : listOutputs) {
            try {
                sortie.writeUTF(Demineur.START + " " + compteurJoueur + " " + level.name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < compteurJoueur; i++) {
            if (etatJoueurs.get(i) != QUITTE) {
                etatJoueurs.set(i, ENCOURS);
            }
        }
    }

    /**
     * Fonction permettant d'enregistrer dans un fichier les informations d'une partie.
     */
    public void updateFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true));

            writer.write("Partie lancée à " + startTime + "\n");
            writer.write("Nombre de joueurs: " + compteurJoueur + "\n");
            int i = 1;
            for (Integer joueur : etatJoueurs) {
                if (joueur != QUITTE) {
                    writer.write("Joueur " + i + " - Resultat : " + (joueur == PERDU ? "Perdu" : "Gagné (score : " + scoreGagnant + ")\n"));
                    i++;
                }
            }
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
