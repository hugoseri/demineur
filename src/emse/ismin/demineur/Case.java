package emse.ismin.demineur;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Classe correspondant à une case du champ de mines.
 */
public class Case extends JPanel implements MouseListener {
    private final static int DIM = 25;
    private final Demineur demineur;
    private final int x;
    private final int y;

    static final int COULEUR_NEUTRE = 0xAAAAAA;
    static final int COULEUR_MINE = 0xFF5E46;
    static final int COULEUR_0 = 0xFFFFFF;
    static final int COULEUR_1 = 0xdbfbc4;
    static final int COULEUR_2 = 0xf4fbc4;
    static final int COULEUR_3 = 0xfee47b;
    static final int COULEUR_4 = 0xffc433;
    static final int COULEUR_5 = 0xffa609;
    static final int COULEUR_6 = 0xfea053;
    static final int COULEUR_7 = 0xfea050;
    static final int COULEUR_8 = 0xfea040;

    private boolean click = false;

    private int type_case=10;

    public boolean isClicked(){
        return click;
    }

    public Case(int x, int y,  Demineur demineur){
        this.demineur = demineur;
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(DIM, DIM));
        addMouseListener(this);
    }

    public void newPartie(){
        click = false;
        repaint();
    }


    @Override
    public void paintComponent(Graphics gc){
        super.paintComponent(gc);

        Font font = new Font("Arial", Font.PLAIN, getHeight()/3);
        gc.setFont(font);

        int x_rect = 1;
        int y_rect = 1;
        //gc.fillRect(x_rect,y_rect, getWidth(), getHeight());

        if (!demineur.connected) {
            if (click) {
                boolean isMine = demineur.getChamp().isMine(x, y);
                if (!isMine) {
                    int minesAutour = demineur.getChamp().minesAutour(x, y);
                    showNotMine(gc, minesAutour, font);
                } else {
                    showMine(gc);
                }
            } else {
                showUnknown(gc);
            }
        } else {
            if (type_case == 9){ //mine
                showMine(gc);
            } else if (type_case < 9){ //pas mine
                showNotMine(gc, type_case, font);
            } else { //on ne sait pas, état initial
                showUnknown(gc);
            }
        }
    }

    /**
     * Draw a centered string.
     * @param g
     * @param text
     * @param rect_x
     * @param rect_y
     * @param rect_width
     * @param rect_height
     * @param font
     */
    public static void drawCenteredString(Graphics g, String text, int rect_x, int rect_y, int rect_width, int rect_height, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect_x + (rect_width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect_y + ((rect_height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public void showCase(int type){
        click = true;
        type_case = type;
        repaint();
    }

    private void showUnknown(Graphics gc){
        gc.setColor(new Color(COULEUR_NEUTRE)); //couleur background
        gc.fillRect(1, 1, getWidth(), getHeight());
    }

    private void showMine(Graphics gc){
        Color bg_color = new Color(COULEUR_MINE); //couleur background
        gc.setColor(bg_color);
        gc.fillRect(1, 1, getWidth(), getHeight());
        try {
            BufferedImage image = ImageIO.read(new File("img/bombe.png"));
            gc.drawImage(image, getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNotMine(Graphics gc, int type_case, Font font){
        Color bg_color = new Color(getColor(type_case)); //couleur background
        gc.setColor(bg_color);
        gc.fillRect(1, 1, getWidth(), getHeight());
        gc.setColor(Color.BLACK); //couleur texte
        if (type_case != 0) {
            drawCenteredString(gc, String.valueOf(type_case), 1, 1, getWidth(), getHeight(), font);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e){

        if (demineur.connected && !demineur.isLost() && !demineur.isWon() && !click){
            try {
                demineur.sortieOnline.writeUTF(Demineur.PLAYED+ " " + x + " " + y);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            if (e.getButton() == MouseEvent.BUTTON1 && !click) {
                click = true;
                if (!demineur.isLost() && !demineur.isWon()) {
                    demineur.getChamp().nbClick++;
                }
            }
            if (!demineur.isLost() && !demineur.isWon()) {
                if (!demineur.isStarted()) {
                    demineur.start();
                }
                repaint();
                if (demineur.getChamp().isMine(x, y)) {
                    demineur.setLost(true);
                    demineur.perdu();
                } else {
                    if (demineur.getChamp().isWon()) {
                        demineur.setWon(true);
                        demineur.gagne();
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private int getColor(int valeurCase){
        int color;
        switch(valeurCase){
            case 0:
                color = COULEUR_0;
                break;
            case 1:
                color = COULEUR_1;
                break;
            case 2:
                color = COULEUR_2;
                break;
            case 3:
                color = COULEUR_3;
                break;
            case 4:
                color = COULEUR_4;
                break;
            case 5:
                color = COULEUR_5;
                break;
            case 6:
                color = COULEUR_6;
                break;
            case 7:
                color = COULEUR_7;
                break;
            case 8:
                color = COULEUR_8;
                break;
            default:
                color = COULEUR_NEUTRE;
                break;
        }
        return color;
    }

}
