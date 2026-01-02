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
            view.redimensionnerGrille(game.getGrilleCentrale().getTaille());
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

        // Utilisation de la méthode tirerCarteJoueur du modèle
        // qui gère le retrait du deck et la mise en réserve
        Cartes c = game.tirerCarteJoueur(idJoueur - 1, estHaute);

        if (c != null) {
            view.revelerCarteDepuisMain(idJoueur, c);
            view.afficherMainJoueurActif(game.getJoueurActif());
            view.afficherMessage(game.getJoueurActif().getNom() + " révèle une carte de "
                    + game.getJoueurParId(idJoueur - 1).getNom());
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
        // Le modèle a déjà fait nextPlayer(), donc le joueur qui a gagné le trio
        // est le "joueur précédent"
        Joueur gagnantPotentiel = game.getJoueurPrecedent();

        view.afficherMessage("Trio trouvé par " + gagnantPotentiel.getNom() + " !");
        game.viderReserve();

        // 1. On vérifie la victoire sur le BON joueur
        if (game.checkVictory(gagnantPotentiel)) {
            // On actualise une dernière fois pour voir le trio s'ajouter au score
            view.actualiserTout(game.getListeJoueurs(), game.getJoueurActif(), game.getGrilleCentrale());
            interactionVerrouillee = true;
            view.afficherVictoire(gagnantPotentiel.getNom());
        } else {
            // 2. Si pas de victoire, on attend un peu et on passe au joueur suivant
            // visuellement
            interactionVerrouillee = true; // On bloque pendant l'animation
            Timer timer = new Timer(2000, e -> {
                view.cacherCartesGrille();
                view.actualiserTout(game.getListeJoueurs(), game.getJoueurActif(), game.getGrilleCentrale());
                interactionVerrouillee = false;
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Actions en cas d'erreur (cartes différentes).
     * Verrouille l'écran 2 secondes pour mémorisation.
     */
    private void gererErreur() {
        interactionVerrouillee = true;
        view.afficherMessage("Raté ! Les cartes retournent chez leurs propriétaires.");

        Timer timer = new Timer(2000, e -> {
            // --- LOGIQUE DE RESTITUTION PRÉCISE ---
            // Le modèle rend chaque carte au bon joueur (soi-même ou adversaire)
            game.restituerToutesLesCartes();

            view.cacherCartesGrille();
            // On rafraîchit tout pour que chaque joueur retrouve ses cartes
            view.actualiserTout(game.getListeJoueurs(), game.getJoueurActif(), game.getGrilleCentrale());
            interactionVerrouillee = false;
        });
        timer.setRepeats(false);
        timer.start();
    }
}