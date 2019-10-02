package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;

import static emse.ismin.demineur.Case.drawCenteredString;

public class Compteur extends JPanel implements Runnable {

    private final static int DIM = 25;

    private Thread processScore;

    static final int COULEUR_NEUTRE = 0xdddddd;
    static final int COULEUR_ENCOURS = 0x32CD32;

    public int getValCompteur() {
        return valCompteur;
    }

    private int valCompteur;

    Compteur(){
        setPreferredSize(new Dimension(DIM*6, DIM));
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);

        Font font = new Font("Arial", Font.PLAIN, getHeight()/2);
        gc.setFont(font);

        gc.setColor(getColor(valCompteur));

        gc.fillRect(0,0, getWidth(), getHeight());
        gc.setColor(Color.BLACK);

        drawCenteredString(gc, "Temps écoulé : "+String.valueOf(valCompteur), 0, 0, getWidth(), getHeight(), font);
    }

    public void startCompteur(){
        valCompteur = 0;

        processScore = new Thread(this);
        processScore.start();
    }

    public void stopCompteur(){
        processScore = null;
    }

    public void resetCompteur(){
        valCompteur = 0;
        repaint();
    }

    @Override
    public void run(){
        while (processScore != null){
            try {
                processScore.sleep(1000);
                if (processScore !=null) { //peut passer null s'il y a un stopCompteur pendant sleep
                    valCompteur+=1;
                    repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private Color getColor(int valeurCompteur){
        int color;
        if (valeurCompteur == 0) {
            color = COULEUR_NEUTRE;
        } else {
            color = COULEUR_ENCOURS;
        }
        return new Color(color);
    }
}
