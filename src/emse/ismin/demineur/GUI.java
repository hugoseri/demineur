package emse.ismin.demineur;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Classe de l'interface graphique du démineur.
 */
public class GUI extends JPanel implements ActionListener {

    private Demineur main;
    private JButton boutonQuitter;
    private JButton boutonRelancer;

    public PanelChamp getPanelMines() {
        return panelMines;
    }

    //private PanelChamp panelMines;
    public PanelChamp panelMines;
    private JMenuItem mFacile;
    private JMenuItem mMoyen;
    private JMenuItem mDifficile;
    private JMenuItem mQuitter;
    private JMenuItem mAPropos;
    private JPanel infos;

    GUI(Demineur main) {
        this.main = main;
        this.setLayout(new BorderLayout());

        //----------------------------------
        //---------- MENU TOP---------------
        //----------------------------------
        JMenuBar barreMenu = new JMenuBar();

        JMenu menuPartie = new JMenu("Partie");
        barreMenu.add(menuPartie);

        JMenu mRejouer = new JMenu("Rejouer");
        menuPartie.add(mRejouer);
        mRejouer.setToolTipText("Rejouer");

        mFacile = new JMenuItem("Facile");
        mFacile.addActionListener(this);
        mRejouer.add(mFacile);
        mMoyen = new JMenuItem("Moyen");
        mMoyen.addActionListener(this);
        mRejouer.add(mMoyen);
        mDifficile = new JMenuItem("Difficile");
        mDifficile.addActionListener(this);
        mRejouer.add(mDifficile);


        mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        mQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuPartie.add(mQuitter);
        mQuitter.addActionListener(this);
        mQuitter.setToolTipText("Quitter");

        barreMenu.add(Box.createGlue());

        JMenu mHelp = new JMenu("Help");
        barreMenu.add(mHelp);
        mAPropos = new JMenuItem("A propos", KeyEvent.VK_H);
        mHelp.add(mAPropos);
        mAPropos.addActionListener(this);
        mAPropos.setToolTipText("A propos");

        main.setJMenuBar(barreMenu);

        infos = new JPanel();

        JLabel welcome = new JLabel("Bienvenue sur le jeu du démineur !");
        infos.add(welcome);

        add(infos, BorderLayout.NORTH);

        generateScoreLvl();

        //----------------------------------
        //---------- CHAMP MIDDLE-----------
        //----------------------------------
        panelMines = new PanelChamp(main);
        add(panelMines, BorderLayout.CENTER);

        //----------------------------------
        //---------- BOUTONS SOUTH----------
        //----------------------------------
        JPanel south = new JPanel();

        boutonRelancer = new JButton("Relancer une partie");
        boutonRelancer.addActionListener(this);
        boutonRelancer.setBackground(new Color(0x7A7A7A));
        boutonRelancer.setForeground(new Color(0xFFFFFF));
        south.add(boutonRelancer);

        boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(this);
        boutonQuitter.setBackground(new Color(0x7A7A7A));
        boutonQuitter.setForeground(new Color(0xFFFFFF));
        south.add(boutonQuitter, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (boutonQuitter.equals(source)) {
            main.quit();
        } else if (mQuitter.equals(source)) {
            main.quit();
        } else if (boutonRelancer.equals(source)) {
            main.relancer();
        } else if (mAPropos.equals(source)) {
            JOptionPane.showConfirmDialog(null,
                    "Créé par Hugo !",
                    "A propos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (mFacile.equals(source)){
            main.relancer(Level.EASY);
        } else if (mMoyen.equals(source)){
            main.relancer(Level.MEDIUM);
        } else if (mDifficile.equals(source)){
            main.relancer(Level.HARD);
        }
    }

    public void generateScoreLvl(){
        JLabel score = new JLabel("Mines restantes: " + 0);
        infos.add(score);
        JLabel niveau = new JLabel("Niveau: " + main.getChamp().getLevel());
        infos.add(niveau);
    }
    public void generateChamp(){
        generateScoreLvl();
        BorderLayout layout = (BorderLayout) getLayout();
        remove(layout.getLayoutComponent(BorderLayout.CENTER));
        panelMines = new PanelChamp(main);
        add(panelMines, BorderLayout.CENTER);
    }
}
