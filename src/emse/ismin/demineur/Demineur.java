package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;

public class Demineur extends JFrame {

    int dim_x = 10;
    int dim_y = 10;
    int nb_mines = 5;
    GUI gui;

    // private Champ champ = new Champ();
    // private Champ champ = new Champ(dim_x, dim_y, nb_mines);

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

    public Demineur() {

        super("Démineur");
        gui = new GUI(this);

        setContentPane(gui);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        setVisible(true);

    }


    public static void main(String[] args) {
        System.out.println("");
        System.out.println("Lancement de l'interface du démineur.");
        System.out.println("");

        new Demineur();
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
}