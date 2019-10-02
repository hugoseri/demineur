package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIServeur extends JPanel implements ActionListener {

    private Serveur serveur;
    private JButton startServeurButton;
    private JButton startGameButton;
    private JTextArea messages;
    private JScrollPane scrollBar;

    public JComboBox levelChoice;

    GUIServeur(Serveur serveur){
        this.serveur = serveur;

        setPreferredSize(new Dimension(400, 200));

        setLayout(new BorderLayout());
        JLabel welcome = new JLabel("Serveur démineur", SwingConstants.CENTER);
        welcome.setFont(new Font("Times New Roman", Font.BOLD, 15));
        add(welcome, BorderLayout.NORTH);

        messages = new JTextArea();
        messages.setEditable(false);
        scrollBar = new JScrollPane(messages);
        scrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollBar, BorderLayout.CENTER);

        JPanel boutons = new JPanel();
        boutons.setLayout(new BorderLayout());


        JPanel game = new JPanel();

        Level[] listLevel = new Level[]{Level.EASY, Level.MEDIUM, Level.HARD};
        levelChoice = new JComboBox(listLevel);
        game.add(levelChoice);

        startGameButton = new JButton("Démarrer partie");
        startGameButton.addActionListener(this);
        startGameButton.setBackground(new Color(0x7A7A7A));
        startGameButton.setForeground(new Color(0xFFFFFF));
        game.add(startGameButton);

        boutons.add(game, BorderLayout.NORTH);

        startServeurButton = new JButton("Redémarrer serveur");
        startServeurButton.addActionListener(this);
        startServeurButton.setBackground(new Color(0x7A7A7A));
        startServeurButton.setForeground(new Color(0xFFFFFF));
        boutons.add(startServeurButton, BorderLayout.SOUTH);


        add(boutons, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (startServeurButton.equals(source)){
            serveur.broadcastRedemarrageServeur();
            serveur.startServeur();
        }else if (startGameButton.equals(source) && serveur.serveurOn){
            serveur.newGame();
        }
    }

    public void addMsg(String msg){
        messages.append(msg+'\n');
        scrollBar.getVerticalScrollBar().setValue(scrollBar.getVerticalScrollBar().getMaximum());
    }
}
