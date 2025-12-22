package controller;

import model.*;
import view.*;
import javax.swing.Timer;
import java.util.List;

public class GameController {
    private Game game;
    private GameView view;
    private boolean interactionVerrouillee = false;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }

    /**
     * Lance la saisie des noms et initialise la partie dans le modèle et la vue.
     */
    public void demarrerPartie() {
        List<String> joueurs = view.demanderNomsJoueurs();
        if (joueurs != null && !joueurs.isEmpty()) {
            game.start(joueurs);
            view.initialiserZoneJoueurs(game.getListeJoueurs(), this);
            // On affiche la main du premier joueur
            view.afficherMainJoueurActif(game.getJoueurActif());
            view.actualiserTourJoueur(game.getJoueurActif().getNom());
        }
    }

    /**
     * Logique lorsqu'une carte de la grille centrale est cliquée.
     */
    public void choisirCarteGrille(int index) {
        if (interactionVerrouillee)
            return;

        Cartes c = game.getGrilleCentrale().devoilerCarte(index);
        if (c != null) {
            view.revelerCarteGrille(index, c);
            traiterSelection(c);
        }
    }

    /**
     * Logique lorsqu'on demande la carte haute ou basse d'un joueur.
     */
    public void choisirCarteJoueur(int idJoueur, boolean estHaute) {
        if (interactionVerrouillee)
            return;

        // On récupère le joueur cible (idJoueur commence à 1 dans votre classe Joueur)
        Joueur j = game.getListeJoueurs().get(idJoueur - 1);
        Cartes c = estHaute ? j.getDeckJoueur().recupererCPH() : j.getDeckJoueur().recupererCPB();

        if (c != null) {
            view.revelerCarteDepuisMain(idJoueur, c);
            // On affiche un log mais on garde la main active visible
            view.afficherMessage(game.getJoueurActif().getNom() + " révèle une carte de " + j.getNom());
            traiterSelection(c);
        }
    }

    /**
     * Analyse le résultat de la sélection auprès du modèle.
     */
    private void traiterSelection(Cartes c) {
        if (c == null)
            return;

        int resultat = game.selectionnerCarte(c);

        if (resultat == -1) { // Erreur : les cartes ne correspondent pas
            gererErreur();
        } else if (resultat == 1) { // Succès : Trio complété
            gererSucces();
        }
        // Si resultat == 0, on ne fait rien, on attend la carte suivante
    }

    /**
     * Actions en cas de Trio trouvé.
     */
    private void gererSucces() {
        view.afficherMessage("Trio trouvé par " + game.getJoueurActif().getNom() + " !");

        // Mise à jour complète (Scores + Main Joueur si ça change ou si trios ajoutés)
        // Fait par Blusk : on actualise la vue globale avec scores, grille, mains après
        // un succès
        view.actualiserTout(game.getListeJoueurs(), game.getJoueurActif(), game.getGrilleCentrale());

        if (game.checkVictory()) {
            interactionVerrouillee = true;
            view.afficherVictoire(game.getJoueurActif().getNom());
        }
    }

    /**
     * Actions en cas d'erreur (cartes différentes).
     * Verrouille l'écran 2 secondes pour mémorisation.
     */
    private void gererErreur() {
        interactionVerrouillee = true;
        view.afficherMessage("Raté ! Passage au joueur suivant...");

        Timer timer = new Timer(2000, e -> {
            view.cacherCartesGrille();
            // Le modèle a déjà changé de joueur (Game.java:130)
            // Fait par Blusk : on actualise la vue globale après une erreur pour être sur
            // que les cartes sont retournées
            view.actualiserTout(game.getListeJoueurs(), game.getJoueurActif(), game.getGrilleCentrale());
            interactionVerrouillee = false;
        });
        timer.setRepeats(false);
        timer.start();
    }
}