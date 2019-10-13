package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;

import static emse.ismin.demineur.Case.drawCenteredString;

public class Compteur extends JPanel implements Runnable {

    public Color bleu_tres_clair = new Color(0x41B3D9);
    public Color bleu_clair = new Color(0x00A3D9);
    public Color bleu_gris = new Color(0x30839F);
    public Color bleu_fonce = new Color(0x00688B);
    public Color bleu_nuit = new Color(0X004359);

    private final static int DIM = 25;

    private Thread processScore;

    public int getValCompteur() {
        return valCompteur;
    }

    private int valCompteur;

    Compteur() {
        setPreferredSize(new Dimension(DIM * 6, DIM));
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);

        Font font = new Font("Raleway", Font.PLAIN, 12);
        gc.setFont(font);

        gc.setColor(getColor(valCompteur));

        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setColor(Color.BLACK);

        drawCenteredString(gc, "Temps écoulé : " + String.valueOf(valCompteur), 0, 0, getWidth(), getHeight(), font);
    }

    /**
     * Fonction initialisant et déclenchant le compteur.
     */
    public void startCompteur() {
        valCompteur = 0;

        processScore = new Thread(this);
        processScore.start();
    }

    /**
     * Fonction arrêtant le compteur.
     */
    public void stopCompteur() {
        processScore = null;
    }

    /**
     * Fonction réinitialisant le compteur.
     */
    public void resetCompteur() {
        valCompteur = 0;
        repaint();
    }

    /**
     * Fonction incrémentant le compteur chaque seconde.
     */
    @Override
    public void run() {
        while (processScore != null) {
            try {
                processScore.sleep(1000);
                if (processScore != null) { //peut passer null s'il y a un stopCompteur pendant sleep
                    valCompteur += 1;
                    repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fonction de gestion de la couleur de fond du compteur en fonction de sa valeur.
     *
     * @param valeurCompteur valeur du compteur.
     * @return color couleur du fond du compteur.
     */
    private Color getColor(int valeurCompteur) {
        Color color;
        if (valeurCompteur == 0) {
            color = Color.WHITE;
        } else {
            color = bleu_tres_clair;
        }
        return color;
    }
}
