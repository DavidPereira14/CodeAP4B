import model.Game;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        List<String> joueurs = new ArrayList<>();
        joueurs.add("Alice");
        joueurs.add("Bob");

        System.out.println("--- Début du test du jeu ---");
        game.start(joueurs);

        System.out.println("--- Lancement d'un tour ---");
        game.playTurn();
    }
}
