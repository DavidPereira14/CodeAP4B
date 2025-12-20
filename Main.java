import model.*;
import controller.*;
import view.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Game model = new Game();
        GameView view = new GameView();
        GameController controller = new GameController(model, view);

        // Exemple de lancement
        List<String> joueurs = Arrays.asList("Alice", "Bob");
        model.start(joueurs);
        view.initialiserZoneJoueurs(model.getListeJoueurs(), controller);
    }
}