package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIServeur extends JPanel implements ActionListener {

    private Serveur serveur;
    private JButton startButton;
    private JTextArea messages;

    GUIServeur(Serveur serveur){
        this.serveur = serveur;

        setPreferredSize(new Dimension(400, 200));

        setLayout(new BorderLayout());
        JLabel welcome = new JLabel("Serveur démineur", SwingConstants.CENTER);
        welcome.setFont(new Font("Times New Roman", Font.BOLD, 15));
        add(welcome, BorderLayout.NORTH);

        messages = new JTextArea(400, 100);
        messages.setEditable(false);
        add(messages, BorderLayout.CENTER);

        startButton = new JButton("Démarrer partie");
        startButton.addActionListener(this);
        startButton.setBackground(new Color(0x7A7A7A));
        startButton.setForeground(new Color(0xFFFFFF));
        add(startButton, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (startButton.equals(source)){
            addMsg("Démarrage partie");
            serveur.newGame();
        }
    }

    public void addMsg(String msg){
        messages.append(msg+'\n');
    }
}
