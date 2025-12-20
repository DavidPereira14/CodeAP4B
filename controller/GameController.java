package controller;

import model.*;
import view.*;
import javax.swing.Timer; // Si vous utilisez Swing
import javax.swing.text.View;

import java.util.List;

public class GameController {
    private Game game;
    private View view;
    private boolean interactionVerrouillee = false;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        // Ici, on lierait les boutons de la vue aux méthodes ci-dessous
    }

    public void choisirCarteGrille(int index) {
        if (interactionVerrouillee)
            return;

        Cartes c = model.getGrilleCentrale().devoilerCarte(index);
        traiterSelection(c);
    }

    public void choisirCarteJoueur(int idJoueur, boolean estHaute) {
        if (interactionVerrouillee)
            return;
        Joueur j = game.getListeJoueurs().get(idJoueur - 1);
        Cartes c;
        if (estHaute) {
            c = j.getDeckJoueur().recupererCPH();
        } else {
            c = j.getDeckJoueur().recupererCPB();
        }
        traiterSelection(c);
    }

    private void traiterSelection(Cartes c) {
        if (c == null)
            return;

        int resultat = game.selectionnerCarte(c);
        view.afficherCarteRevelee(c);

        if (resultat == -1) {
            gererErreur();
        } else if (resultat == 1) {
            gererSucces();
        }
    }

    private void gererErreur() {
        interactionVerrouillee = true;
        view.afficherMessage("Raté ! Passage au joueur suivant...");

        Timer timer = new Timer(2000, e -> {
            view.cacherCartesNonGagnees();
            view.actualiserTourJoueur(game.getJoueurActif().getNom());
            interactionVerrouillee = false;
        });
        timer.setRepeats(false);
        timer.start();
    }

}
