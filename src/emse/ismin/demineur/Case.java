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

    private boolean left_click = false;
    private boolean right_click = false;

    public boolean isClicked(){
        return left_click;
    }

    public Case(int x, int y,  Demineur demineur){
        this.demineur = demineur;
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(DIM, DIM));
        addMouseListener(this);
    }

    public void newPartie(){
        right_click = false;
        left_click = false;
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

        Color bg_color;
        if (left_click) {
            boolean isMine = demineur.getChamp().isMine(x, y);
            if (!isMine) {
                int minesAutour = demineur.getChamp().minesAutour(x, y);
                bg_color = new Color(getColor(minesAutour)); //couleur background
                gc.setColor(bg_color);
                gc.fillRect(x_rect, y_rect, getWidth(), getHeight());
                gc.setColor(Color.BLACK); //couleur texte
                if (minesAutour != 0) {
                    drawCenteredString(gc, String.valueOf(minesAutour), x_rect, y_rect, getWidth(), getHeight(), font);
                }
            } else {
                bg_color = new Color(COULEUR_MINE); //couleur background
                gc.setColor(bg_color);
                gc.fillRect(x_rect, y_rect, getWidth(), getHeight());
                try {
                    BufferedImage image = ImageIO.read(new File("img/bombe.png"));
                    gc.drawImage(image, getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (right_click && !demineur.isLost()) {
            bg_color = new Color(COULEUR_NEUTRE); //couleur background
            gc.setColor(bg_color);
            gc.fillRect(x_rect, y_rect, getWidth(), getHeight());
            try {
                BufferedImage image = ImageIO.read(new File("img/flag.png"));
                gc.drawImage(image, getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gc.setColor(new Color(COULEUR_NEUTRE)); //couleur background
            gc.fillRect(x_rect, y_rect, getWidth(), getHeight());
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {
            left_click = true;
            if (!demineur.isLost() && !demineur.isWon()) {
                demineur.getChamp().nbClick++;
            }

        } else if (e.getButton() == MouseEvent.BUTTON3)
            right_click = true;

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
