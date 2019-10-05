package emse.ismin.demineur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de l'interface graphique du démineur.
 */
public class GUI extends JPanel implements ActionListener {

    private final Font title_font = new Font("Roboto", Font.BOLD, 18);
    private final Font text_font = new Font("Raleway", Font.PLAIN, 12);
    private final Font button_font = new Font("Raleway", Font.BOLD, 12);
    private final Font subtitle_font = new Font("Roboto", Font.BOLD, 16);

    private Demineur main;
    private JButton boutonQuitter;
    private JButton boutonRelancer;
    public JButton boutonOnline;

    private JTextArea host;
    private JTextArea port;
    public JTextArea pseudo;

    public PanelChamp getPanelMines() {
        return panelMines;
    }

    public PanelChamp panelMines;
    private JMenuItem mFacile;
    private JMenuItem mMoyen;
    private JMenuItem mDifficile;
    private JMenuItem mQuitter;
    private JMenuItem mAPropos;
    private JPanel infos;
    private JTextArea infos_jeu;
    private JScrollPane scrollBar_online;
    private JPanel infos_score;

    public Color bleu_tres_clair = new Color(0x41B3D9);
    public Color bleu_clair = new Color(0x00A3D9);
    public Color bleu_gris = new Color(0x30839F);
    public Color bleu_fonce = new Color(0x00688B);
    public Color bleu_nuit = new Color(0X004359);

    private List<JTextArea> liste_scores;

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

        setBackground(Color.WHITE);

        //----------------------------------
        //---------- MENU TOP---------------
        //----------------------------------

        //barre de menu
        {
            JMenuBar barreMenu = new JMenuBar();
            barreMenu.setBackground(bleu_nuit);
            barreMenu.setForeground(Color.WHITE);

            // menu Partie
            {
                JMenu menuPartie = new JMenu("Partie");
                menuPartie.setForeground(Color.WHITE);
                menuPartie.setFont(text_font);
                barreMenu.add(menuPartie);

                JMenu mRejouer = new JMenu("Rejouer");
                mRejouer.setFont(text_font);
                menuPartie.add(mRejouer);
                mRejouer.setToolTipText("Rejouer");

                mFacile = new JMenuItem("Facile");
                mFacile.setFont(text_font);
                mFacile.addActionListener(this);
                mRejouer.add(mFacile);
                mMoyen = new JMenuItem("Moyen");
                mMoyen.setFont(text_font);
                mMoyen.addActionListener(this);
                mRejouer.add(mMoyen);
                mDifficile = new JMenuItem("Difficile");
                mDifficile.setFont(text_font);
                mDifficile.addActionListener(this);
                mRejouer.add(mDifficile);


                mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
                mQuitter.setFont(text_font);
                mQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
                menuPartie.add(mQuitter);
                mQuitter.addActionListener(this);
                mQuitter.setToolTipText("Quitter");
            }
            barreMenu.add(Box.createGlue());

            { // menu Help
                JMenu mHelp = new JMenu("Help");
                mHelp.setForeground(Color.WHITE);
                mHelp.setFont(text_font);
                barreMenu.add(mHelp);
                mAPropos = new JMenuItem("A propos", KeyEvent.VK_H);
                mAPropos.setFont(text_font);
                mHelp.add(mAPropos);
                mAPropos.addActionListener(this);
                mAPropos.setToolTipText("A propos");
            }

            main.setJMenuBar(barreMenu);
        }

        { // Panel infos générales
            infos = new JPanel();
            infos.setLayout(new BorderLayout());
            infos.setBackground(Color.WHITE);

            JLabel welcome = new JLabel("Bienvenue sur le jeu du démineur !", SwingConstants.CENTER);
            welcome.setBorder(new EmptyBorder(15, 10, 0, 10));
            welcome.setFont(title_font);
            infos.add(welcome, BorderLayout.NORTH);

            JLabel niveau = new JLabel("Niveau: " + main.getChamp().getLevel(), SwingConstants.CENTER);
            niveau.setBorder(new EmptyBorder(10, 0, 10, 0));
            niveau.setFont(text_font);
            infos.add(niveau, BorderLayout.CENTER);

            compteurScore = new Compteur();
            infos.add(compteurScore, BorderLayout.SOUTH);
        }

        add(infos, BorderLayout.NORTH);

        //----------------------------------
        //---------- CHAMP MIDDLE-----------
        //----------------------------------
        panelMines = new PanelChamp(main);
        panelMines.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelMines.setBackground(Color.WHITE);
        add(panelMines, BorderLayout.CENTER);

        //----------------------------------
        //---------- BOUTONS SOUTH----------
        //----------------------------------
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());

        { // bouton relancer et quitter
            JPanel relancerQuitter = new JPanel();
            relancerQuitter.setBorder(new EmptyBorder(0, 0, 10, 0));
            relancerQuitter.setBackground(Color.WHITE);
            boutonRelancer = new JButton("Relancer une partie");
            boutonRelancer.setFont(button_font);
            boutonRelancer.addActionListener(this);
            boutonRelancer.setBackground(bleu_fonce);
            boutonRelancer.setForeground(Color.WHITE);
            relancerQuitter.add(boutonRelancer);

            boutonQuitter = new JButton("Quitter");
            boutonQuitter.setFont(button_font);
            boutonQuitter.addActionListener(this);
            boutonQuitter.setBackground(bleu_fonce);
            boutonQuitter.setForeground(Color.WHITE);
            relancerQuitter.add(boutonQuitter, BorderLayout.SOUTH);

            south.add(relancerQuitter, BorderLayout.NORTH);
        }

        { // Partie jeu en ligne
            JPanel online = new JPanel();
            online.setBorder(new EmptyBorder(10, 0, 0, 0));
            online.setLayout(new BorderLayout());
            online.setBackground(new Color(0x33A1C9));

            JLabel textOnline = new JLabel("Paramètres jeu en ligne", SwingConstants.CENTER);
            textOnline.setBorder(BorderFactory.createEmptyBorder());
            textOnline.setFont(subtitle_font);
            online.add(textOnline, BorderLayout.NORTH);

            boutonOnline = new JButton("Connexion");
            boutonOnline.setPreferredSize(new Dimension(this.getWidth(), 30));
            boutonOnline.setBorder(BorderFactory.createEmptyBorder());
            boutonOnline.addActionListener(this);
            boutonOnline.setBackground(bleu_fonce);
            boutonOnline.setForeground(Color.WHITE);

            { //Panel infos serveur
                JPanel infos_online = new JPanel();
                infos_online.setLayout(new BorderLayout());
                infos_online.setPreferredSize(new Dimension(this.getWidth(), 175));
                infos_online.setBackground(bleu_clair);

                JPanel infos_serveur = new JPanel();
                infos_serveur.setBorder(new EmptyBorder(10, 0, 0, 0));
                infos_serveur.setPreferredSize(new Dimension(this.getWidth(), 50));
                infos_serveur.setBackground(bleu_clair);
                infos_serveur.add(new JLabel("Hôte : "));
                host = new JTextArea(main.defaultHost);
                infos_serveur.add(host);

                infos_serveur.add(new JLabel("Port : "));
                port = new JTextArea(String.valueOf(main.defaultPort));
                infos_serveur.add(port);

                infos_serveur.add(new JLabel("Pseudo : "));
                pseudo = new JTextArea(String.valueOf(main.defaultPseudo));
                infos_serveur.add(pseudo);

                infos_online.add(infos_serveur, BorderLayout.NORTH);

                infos_score = new JPanel();
                infos_score.setBackground(bleu_clair);
                infos_score.setLayout(new BorderLayout());

                JLabel text_score = new JLabel("Score joueurs", SwingConstants.CENTER);
                text_score.setBorder(new EmptyBorder(10, 0, 10, 0));
                text_score.setFont(subtitle_font);
                infos_score.add(text_score, BorderLayout.NORTH);

                infos_online.add(infos_score, BorderLayout.CENTER);

                infos_jeu = new JTextArea();
                infos_jeu.setEditable(false);
                scrollBar_online = new JScrollPane(infos_jeu);
                scrollBar_online.setPreferredSize(new Dimension(getWidth(), 50));

                infos_online.add(scrollBar_online, BorderLayout.SOUTH);

                online.add(infos_online, BorderLayout.CENTER);
            }
            online.add(boutonOnline, BorderLayout.SOUTH);

            south.add(online, BorderLayout.SOUTH);
        }
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


    public void initScore(int nb_joueurs){
        liste_scores = new ArrayList<>();

        JPanel scores = new JPanel();
        scores.setBackground(bleu_clair);

        for (int i = 0; i < nb_joueurs; i++) {
            JPanel score_global = new JPanel();
            score_global.setBackground(bleu_clair);
            JLabel score_descr = new JLabel("Joueur "+ (i+1) + " : ");
            JTextArea score_value = new JTextArea("0");
            score_global.add(score_descr);
            score_global.add(score_value);
            liste_scores.add(score_value);
            scores.add(score_global);
        }
        infos_score.add(scores, BorderLayout.CENTER);
        main.pack();
    }

    public void updateScore(int nb_joueur, String value, Color color){
        /*
        JTextArea new_score = new JTextArea(value);
        new_score.setBackground(color);
        liste_scores.set(nb_joueur - 1, new_score);
        */
        JTextArea score = liste_scores.get(nb_joueur - 1);
        score.setBackground(color);

        switch (value){
            case "-1":
                value = "Perdu";
                break;
            case "-2":
                value = "Abandon";
                break;
            case "-3":
                value = "Gagné";
                break;
            default:
                break;
        }
        score.setText(value);
        main.pack();
    }


    public void addMsg_online(String msg){
        infos_jeu.append(msg+"\n");
        scrollBar_online.getVerticalScrollBar().setValue(scrollBar_online.getVerticalScrollBar().getMaximum());
    }
}
