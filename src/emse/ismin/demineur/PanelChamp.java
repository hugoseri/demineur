package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;

public class PanelChamp extends JPanel {

    public Case[][] getTabCases() {
        return tabCases;
    }

    private Case[][] tabCases;

    PanelChamp(Demineur main) {
        placeCases(main);
    }

    /**
     * Fonction permettant de remplir le champ de cases.
     *
     * @param main
     */
    public void placeCases(Demineur main) {
        setLayout(new GridLayout(main.getChamp().getDimX(), main.getChamp().getDimY()));

        tabCases = new Case[main.getChamp().getDimX()][main.getChamp().getDimY()];

        for (int i = 0; i < main.getChamp().getDimX(); i++) {
            for (int j = 0; j < main.getChamp().getDimY(); j++) {
                Case maCase;
                maCase = new Case(i, j, main);
                tabCases[i][j] = maCase;
                add(maCase);
            }
        }
    }
}
