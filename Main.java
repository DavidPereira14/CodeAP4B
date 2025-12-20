
import model.Game;
import view.GameView;
import controller.GameController;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciation du Modèle (les données et les règles)
        Game model = new Game();

        // 2. Instanciation de la Vue (l'interface graphique Swing)
        GameView view = new GameView();

        // 3. Instanciation du Contrôleur (le lien entre les deux)
        GameController controller = new GameController(model, view);

        // 4. On donne le contrôleur à la vue pour qu'elle puisse lui envoyer les clics
        view.setController(controller);

        // 5. Lancement du processus de démarrage (choix des joueurs)
        controller.demarrerPartie();
    }
}