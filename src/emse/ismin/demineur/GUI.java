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
    private JButton boutonOnline;

    private JTextArea host;
    private JTextArea port;
    private JTextArea pseudo;

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
    Compteur compteurScore;

    public void startCompteur(){
        compteurScore.startCompteur();
    }

    public void stopCompteur(){
        compteurScore.stopCompteur();
    }

    public void resetCompteur(){
        compteurScore.resetCompteur();
    }

    public int getValCompteur() {
        return compteurScore.getValCompteur();
    }

    GUI(Demineur main) {
        this.main = main;
        this.setLayout(new BorderLayout());

        setBackground(new Color(0xEEEEEE));

        //----------------------------------
        //---------- MENU TOP---------------
        //----------------------------------
        JMenuBar barreMenu = new JMenuBar();
        barreMenu.setBackground(new Color(0xDDDDDD));

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
        infos.setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Bienvenue sur le jeu du démineur !", SwingConstants.CENTER);
        welcome.setFont(new Font("Times New Roman", Font.BOLD, 15));
        infos.add(welcome, BorderLayout.NORTH);

        JPanel scoreAndLevel = new JPanel();
        JLabel score = new JLabel("Mines restantes: " + 0);
        score.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        scoreAndLevel.add(score);
        JLabel niveau = new JLabel("Niveau: " + main.getChamp().getLevel());
        niveau.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        scoreAndLevel.add(niveau);
        infos.add(scoreAndLevel, BorderLayout.CENTER);

        compteurScore = new Compteur();
        infos.add(compteurScore, BorderLayout.SOUTH);

        add(infos, BorderLayout.NORTH);

        //----------------------------------
        //---------- CHAMP MIDDLE-----------
        //----------------------------------
        panelMines = new PanelChamp(main);
        add(panelMines, BorderLayout.CENTER);

        //----------------------------------
        //---------- BOUTONS SOUTH----------
        //----------------------------------
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());

        JPanel relancerQuitter = new JPanel();
        boutonRelancer = new JButton("Relancer une partie");
        boutonRelancer.addActionListener(this);
        boutonRelancer.setBackground(new Color(0x7A7A7A));
        boutonRelancer.setForeground(new Color(0xFFFFFF));
        relancerQuitter.add(boutonRelancer);

        boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(this);
        boutonQuitter.setBackground(new Color(0x7A7A7A));
        boutonQuitter.setForeground(new Color(0xFFFFFF));
        relancerQuitter.add(boutonQuitter, BorderLayout.SOUTH);

        south.add(relancerQuitter, BorderLayout.NORTH);

        JPanel online = new JPanel();
        online.setBorder(BorderFactory.createEmptyBorder());
        online.setLayout(new BorderLayout());
        online.setBackground(new Color(0x33A1C9));

        JLabel textOnline = new JLabel("Paramètres jeu en ligne", SwingConstants.CENTER);
        textOnline.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        online.add(textOnline, BorderLayout.NORTH);

        boutonOnline = new JButton("Connexion");
        boutonOnline.setPreferredSize(new Dimension(this.getWidth(), 30));
        boutonOnline.setBorder(BorderFactory.createEmptyBorder());
        boutonOnline.addActionListener(this);
        boutonOnline.setBackground(new Color(0x00688B));
        boutonOnline.setForeground(new Color(0xFFFFFF));

        JPanel infos_online = new JPanel();
        infos_online.setPreferredSize(new Dimension(this.getWidth(), 40));
        infos_online.setBackground(new Color(0x33A1C9));

        infos_online.add(new JLabel("Hôte : "));
        host = new JTextArea(main.defaultHost);
        infos_online.add(host);

        infos_online.add(new JLabel("Port : "));
        port = new JTextArea(String.valueOf(main.defaultPort));
        infos_online.add(port);

        infos_online.add(new JLabel("Pseudo : "));
        pseudo = new JTextArea(String.valueOf(main.defaultPseudo));
        infos_online.add(pseudo);

        online.add(infos_online, BorderLayout.CENTER);
        online.add(boutonOnline, BorderLayout.SOUTH);

        south.add(online, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (boutonQuitter.equals(source) && !main.connected) {
            main.quit();
        } else if (mQuitter.equals(source) && !main.connected) {
            main.quit();
        } else if (boutonRelancer.equals(source) && !main.connected) {
            main.relancer();
        } else if (boutonOnline.equals(source)) {
            if (!main.connected) {
                main.connexionServeur(host.getText(), port.getText(), pseudo.getText());
                if (main.connected) {
                    boutonOnline.setText("Déconnexion");
                }
            } else {
                main.deconnexionServeur();
                boutonOnline.setText("Connexion");
            }
        } else if (mAPropos.equals(source)) {
            JOptionPane.showConfirmDialog(null,
                    "Créé par Hugo !",
                    "A propos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (mFacile.equals(source) && !main.connected){
            main.relancer(Level.EASY);
        } else if (mMoyen.equals(source) && !main.connected){
            main.relancer(Level.MEDIUM);
        } else if (mDifficile.equals(source) && !main.connected){
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

        //BorderLayout layout = (BorderLayout) getLayout();
        //remove(layout.getLayoutComponent(BorderLayout.CENTER));
        //panelMines = new PanelChamp(main);
        //add(panelMines, BorderLayout.CENTER);
    }
}
