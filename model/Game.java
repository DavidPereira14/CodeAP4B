package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private EnsembleDeCartes listesCartes;
    private ArrayList<Joueur> listeJoueurs;
    private Pioche pioche;
    private int idJoueurActif;
    private Map<Integer, Cartes> cartesEnJeu;

    public Game() {
        this.listesCartes = new EnsembleDeCartes();
        this.listeJoueurs = new ArrayList<>();
        this.pioche = new Pioche();
        this.idJoueurActif = 0;
        this.cartesEnJeu = new HashMap<>();
    }

    // --- LOGIQUE DE DÉMARRAGE ---

    /**
     * Initialise la partie : crée les joueurs, génère les cartes et distribue.
     * 
     * @param nomsJoueurs Liste des noms récupérée par le Controller depuis la Vue.
     */

    public void start(List<String> nomsJoueurs) {
        for (String nom : nomsJoueurs) {
            this.listeJoueurs.add(new Joueur(nom));
        }

        // Générer et mélanger le jeu de cartes
        preparerJeuDeCartes();

        // Distribuer les cartes aux joueurs
        distribuer();

        System.out.println("La partie commence ! Joueur actuel : " + listeJoueurs.get(idJoueurActif).getNom());
    }

    private void preparerJeuDeCartes() {
        this.listesCartes.ajouterCarte(new Cartes(77, "AP4A", "carte/AP4B.png"));
        this.listesCartes.ajouterCarte(new Cartes(77, "AP4B", "carte/AP4B.png"));
        this.listesCartes.ajouterCarte(new Cartes(77, "AP4B", "carte/AP4B.png"));
        this.listesCartes.ajouterCarte(new Cartes(75, "CMIA", "carte/CMIA.png"));
        this.listesCartes.ajouterCarte(new Cartes(75, "CMIA", "carte/CMIA.png"));
        this.listesCartes.ajouterCarte(new Cartes(75, "CMIA", "carte/CMIA.png"));
        this.listesCartes.ajouterCarte(new Cartes(79, "IA41", "carte/IA41.png"));
        this.listesCartes.ajouterCarte(new Cartes(79, "IA41", "carte/IA41.png"));
        this.listesCartes.ajouterCarte(new Cartes(79, "IA41", "carte/IA41.png"));
        this.listesCartes.ajouterCarte(new Cartes(83, "IF3B", "carte/IF3B.png"));
        this.listesCartes.ajouterCarte(new Cartes(83, "IF3B", "carte/IF3B.png"));
        this.listesCartes.ajouterCarte(new Cartes(83, "IF3B", "carte/IF3B.png"));
        this.listesCartes.ajouterCarte(new Cartes(70, "LO21", "carte/LO21.png"));
        this.listesCartes.ajouterCarte(new Cartes(70, "LO21", "carte/LO21.png"));
        this.listesCartes.ajouterCarte(new Cartes(70, "LO21", "carte/LO21.png"));
        this.listesCartes.ajouterCarte(new Cartes(52, "LP25", "carte/LP25.png"));
        this.listesCartes.ajouterCarte(new Cartes(52, "LP25", "carte/LP25.png"));
        this.listesCartes.ajouterCarte(new Cartes(52, "LP25", "carte/LP25.png"));
        this.listesCartes.ajouterCarte(new Cartes(8, "MDA", "carte/MDA.png"));
        this.listesCartes.ajouterCarte(new Cartes(8, "MDA", "carte/MDA.png"));
        this.listesCartes.ajouterCarte(new Cartes(8, "MDA", "carte/MDA.png"));
        this.listesCartes.ajouterCarte(new Cartes(81, "MT3F", "carte/MT3F.png"));
        this.listesCartes.ajouterCarte(new Cartes(81, "MT3F", "carte/MT3F.png"));
        this.listesCartes.ajouterCarte(new Cartes(81, "MT3F", "carte/MT3F.png"));
        this.listesCartes.ajouterCarte(new Cartes(55, "PC40", "carte/PC40.png"));
        this.listesCartes.ajouterCarte(new Cartes(55, "PC40", "carte/PC40.png"));
        this.listesCartes.ajouterCarte(new Cartes(55, "PC40", "carte/PC40.png"));
        this.listesCartes.ajouterCarte(new Cartes(85, "RE4F", "carte/RE4F.png"));
        this.listesCartes.ajouterCarte(new Cartes(85, "RE4F", "carte/RE4F.png"));
        this.listesCartes.ajouterCarte(new Cartes(85, "RE4F", "carte/RE4F.png"));
        this.listesCartes.ajouterCarte(new Cartes(10, "SO10", "carte/SO10.png"));
        this.listesCartes.ajouterCarte(new Cartes(10, "SO10", "carte/SO10.png"));
        this.listesCartes.ajouterCarte(new Cartes(10, "SO10", "carte/SO10.png"));
        this.listesCartes.ajouterCarte(new Cartes(89, "SP03", "carte/SP03.png"));
        this.listesCartes.ajouterCarte(new Cartes(89, "SP03", "carte/SP03.png"));
        this.listesCartes.ajouterCarte(new Cartes(89, "SP03", "carte/SP03.png"));

        this.listesCartes.melanger();
    }

    private void distribuer() {
        int nbJoueurs = this.listeJoueurs.size();
        int indexJoueur = ThreadLocalRandom.current().nextInt(1, nbJoueurs + 1);
        while (this.listesCartes.getTaille() < 27) {
            Cartes c = this.listesCartes.retirerDerniereCarte();
            this.listeJoueurs.get(indexJoueur).getDeckJoueur().ajouterCarte(c);
            indexJoueur = (indexJoueur + 1) % nbJoueurs;
        }

        while (!this.listesCartes.estVide()) {
            Cartes c = this.listesCartes.retirerDerniereCarte();
            this.pioche.ajouterCarte(c);
        }

        for (Joueur j : listeJoueurs) {
            j.getDeckJoueur().trierCartes();
        }
    }

    public void nextPlayer() {
        this.idJoueurActif = (this.idJoueurActif + 1) % this.listeJoueurs.size();
    }

    public boolean checkVictory() {
        return this.listeJoueurs.get(this.idJoueurActif).getStock().a_gagner();
    }

    public void playTurn() {
        // Récupérer le joueur actif
        Joueur joueurActif = this.listeJoueurs.get(this.idJoueurActif);
        System.out.println("C'est au tour de " + joueurActif.getNom());
        Cartes carteChoisie = null;
        boolean tourFini = false;

        // Le joueur doit révéler 3 cartes identiques
        while (joueurActif.getStock().getTaille() < 3 && !tourFini) {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Voulez-vous révéler une carte de quelqu'un (1) ou une carte de la pioche (2) ?c");
            int choix = scanner.nextInt();
            // Verif choix correct

            if (choix == 1) {
                System.out.println("Veuillez choisir le joueur entre 1 et " + this.listeJoueurs.size()
                        + " à l'exception du votre qui est " + joueurActif.getId());
                int joueurChoisi = scanner.nextInt();
                // Verif joueurChoisi != this.getJoueurActif()
                if (joueurChoisi != joueurActif.getId()) {
                    System.out.println("Vous avez choisi le joueur " + joueurChoisi);
                    System.out
                            .println("Voulez vous qu'il vous révèle la carte la plus haute (1) ou la plus basse (2) ?");
                    int choixCarte = scanner.nextInt();
                    // Verif choixCarte == 1 ou 2
                    if (choixCarte == 1) {
                        carteChoisie = this.listeJoueurs.get(joueurChoisi - 1).getDeckJoueur().recupererCPH();
                        joueurActif.getStock().ajouterCarte(carteChoisie);
                        carteChoisie.afficherCarte();
                    }
                    if (choixCarte == 2) {
                        carteChoisie = this.listeJoueurs.get(joueurChoisi - 1).getDeckJoueur().recupererCPB();
                        joueurActif.getStock().ajouterCarte(carteChoisie);
                        carteChoisie.afficherCarte();
                    }
                }
            }
            if (choix == 2) {
                System.out.println("Veuillez choisir l'index de la carte entre 0 et ");
                int indexCarte = scanner.nextInt();
                carteChoisie = this.pioche.devoilerCarte(indexCarte);
                joueurActif.getStock().ajouterCarte(carteChoisie);
                carteChoisie.afficherCarte();
            }

            // Squelette de logique une fois la carte choisie :
            if (carteChoisie != null) {
                System.out.println("Carte choisie : ");
                carteChoisie.afficherCarte();

                // Vérification de la correspondance avec les cartes déjà révélées
                if (!joueurActif.getStock().estVide()) {
                    if (carteChoisie.getValeur() != joueurActif.getStock().premiereCarte().getValeur()) {
                        System.out.println("Raté !");
                        tourFini = true;
                        nextPlayer();
                    } else {
                        joueurActif.getStock().ajouterCarte(carteChoisie);
                    }
                } else {
                    // Pour éviter une boucle infinie dans le squelette
                    break;
                }
            }

            // Si 3 cartes identiques trouvées
            if (joueurActif.getStock().getTaille() == 3) {
                System.out.println("Trio gagné !");

                if (checkVictory()) {
                    System.out.println("Victoire de " + joueurActif.getNom());
                } else {
                    // Le joueur rejoue
                    playTurn();
                }
            } else {
                joueurActif.getStock().supprimerTrio();
                nextPlayer();
            }

        }
    }

    public Joueur getJoueurActif() {
        return this.listeJoueurs.get(this.idJoueurActif);
    }

    public Map<Integer, Cartes> getCartesEnJeu() {
        return cartesEnJeu;
    }

}
