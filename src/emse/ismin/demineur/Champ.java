/**
 * @author hugo
 */

package emse.ismin.demineur;

import java.util.Random;

import static emse.ismin.demineur.Level.EASY;
import static emse.ismin.demineur.Level.MEDIUM;
import static emse.ismin.demineur.Level.HARD;

/**
 * Classe du champ de mines.
 */
public class Champ {

    private static final int NBMINESEASY = 10;
    private static final int DIMXEASY = 10;
    private static final int DIMYEASY = 10;
    private static final int NBMINESMEDIUM = 20;
    private static final int DIMXMEDIUM = 20;
    private static final int DIMYMEDIUM = 20;
    private static final int NBMINESHARD = 30;
    private static final int DIMXHARD = 30;
    private static final int DIMYHARD = 30;

    private Random alea = new Random();
    private boolean[][] monChamp;

    Level level;

    public Level getLevel() {
        return level;
    }

    int nbClick;

    public boolean isWon(){
        return nbClick == getDimX() * getDimY() - nb_mines;
    }

    int nb_mines;

    public Champ() {
        this(Level.EASY);
    }

    /** Constructeur avec un niveau en paramètre.
     * @param level Level.EASY, Level.MEDIUM ou Level.HARD.
     */
    public Champ(Level level) {
        newPartie(level);
    }

    /** Constructeur avec des dimensions et nombre de mines en paramètres.
     * @param dim_x    dimension horizontale.
     * @param dim_y    dimension verticale.
     * @param nb_mines nombre de mines.
     */
    public Champ(int dim_x, int dim_y, int nb_mines) {
        level = Level.CUSTOM;
        initChamp(dim_x, dim_y, nb_mines);
    }

    /**
     *
     * @return longueur du champ.
     */
    public int getDimX(){
        return monChamp.length;
    }

    /**
     *
     * @return largeur du champ.
     */
    public int getDimY(){ return monChamp[0].length; }

    /**
     * Fonction initialisant le champ de mines.
     *
     * @param dim_x    Dimension horizontale du champ.
     * @param dim_y    Dimension verticale du champ.
     * @param nb_mines Nombre de mines.
     */
    public void initChamp(int dim_x, int dim_y, int nb_mines) {
        this.nb_mines = nb_mines;
        monChamp = new boolean[dim_x][dim_y];

        renouvelleChamp();
    }

    /**
     * Fonction initialisant le champ selon le niveau renseigné.
     * @param level niveau de l partie.
     */
    public void newPartie(Level level){
        this.level = level;
        nbClick = 0;
        if (level == EASY) {
            initChamp(DIMXEASY, DIMYEASY, NBMINESEASY);
        } else if (level == MEDIUM) {
            initChamp(DIMXMEDIUM, DIMYMEDIUM, NBMINESMEDIUM);
        } else if (level == HARD) {
            initChamp(DIMXHARD, DIMYHARD, NBMINESHARD);
        } else {
            throw new java.lang.RuntimeException("Level given isn't supported.");
        }
    }

    /**
     * Fonction réinitialisant le champ.
     */
    public void renouvelleChamp(){
        nbClick = 0;
        champVide();
        placeMines(nb_mines);
    }

    /**
     * Fonction vidant le champ de mines (initialisation).
     */
    private void champVide() {
        for (int i = 0; i < monChamp.length; i++) {
            for (int j = 0; j < monChamp[0].length; j++) {
                monChamp[i][j] = false;
            }
        }
    }

    /**
     * Fonction plaçant les mines aléatoirement sur le champ de mines.
     *
     * @param nb_mines Nombre de mines.
     */
    private void placeMines(int nb_mines) {
        for (int i = 0; i < nb_mines; ) {
            int x = alea.nextInt(monChamp.length);
            int y = alea.nextInt(monChamp[0].length);

            if (!monChamp[x][y]) {
                monChamp[x][y] = true;
                i++;
            }
        }
    }

    /**
     * Fonction permettant de compter le nombre de mines autour d'un point du champ.
     *
     * @param x
     * @param y
     */
    public int minesAutour(int x, int y) {
        if (x > monChamp.length || x < 0 || x > monChamp[0].length || y < 0) {
            throw new java.lang.RuntimeException("Paramètres d'entrée incohérents pour ce champ.");
        }
        int nbMines = 0;
        int x_min = x == 0 ? x : x - 1;
        int y_min = y == 0 ? y : y - 1;

        int x_max = x == monChamp.length - 1 ? x : x + 1;
        int y_max = y == monChamp[0].length - 1 ? y : y + 1;

        for (int i = x_min; i <= x_max; i++) {
            for (int j = y_min; j <= y_max; j++) {
                if (!(i == x && j == y) && monChamp[i][j]) {
                    nbMines++;
                }
            }
        }
        return nbMines;
    }

    /**
     * Fonction retournant si une case est une mine.
     * @param x
     * @param y
     * @return booléen true or false.
     */
    public boolean isMine(int x, int y){
        return monChamp[x][y];
    }

    /**
     * Fonction affichant le champ de mines.
     */
    public void affText() {

        for (int i = 0; i < monChamp.length; i++) {
            for (int j = 0; j < monChamp[0].length; j++) {
                if (monChamp[i][j]) {
                    System.out.print("x");
                } else {
                    System.out.print(minesAutour(i, j));
                }
            }
            System.out.println("");
        }
    }
}
