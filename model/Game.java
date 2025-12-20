package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private EnsembleDeCartes paquetInitial; // Pour la génération
    private ArrayList<Joueur> listeJoueurs;
    private Pioche grilleCentrale;
    private int idJoueurActif;

    // Liste temporaire pour les cartes retournées pendant le tour actuel
    private List<Cartes> cartesReveleesCeTour;

    public Game() {
        this.paquetInitial = new EnsembleDeCartes();
        this.listeJoueurs = new ArrayList<>();
        this.grilleCentrale = new Pioche();
        this.cartesReveleesCeTour = new ArrayList<>();
        this.idJoueurActif = 0;
    }

    // --- INITIALISATION ---

    public void start(List<String> nomsJoueurs) {
        for (String nom : nomsJoueurs) {
            this.listeJoueurs.add(new Joueur(nom));
        }
        preparerJeuDeCartes();
        distribuer();
    }

    /**
     * Application stricte de vos définitions de cartes
     */
    private void preparerJeuDeCartes() {
        // Liste de vos cartes spécifiques (Valeur, Nom, Image)
        Object[][] vosCartes = {
                { 77, "AP4A", "carte/AP4B.png" }, { 75, "CMIA", "carte/CMIA.png" },
                { 79, "IA41", "carte/IA41.png" }, { 83, "IF3B", "carte/IF3B.png" },
                { 70, "LO21", "carte/LO21.png" }, { 52, "LP25", "carte/LP25.png" },
                { 8, "MDA", "carte/MDA.png" }, { 81, "MT3F", "carte/MT3F.png" },
                { 55, "PC40", "carte/PC40.png" }, { 85, "RE4F", "carte/RE4F.png" },
                { 10, "SO10", "carte/SO10.png" }, { 89, "SP03", "carte/SP03.png" }
        };

        for (Object[] data : vosCartes) {
            int val = (int) data[0];
            String nom = (String) data[1];
            String img = (String) data[2];

            // On ajoute 3 exemplaires de chaque carte pour former les trios
            for (int i = 0; i < 3; i++) {
                this.paquetInitial.ajouterCarte(new Cartes(val, nom, img));
            }
        }
        this.paquetInitial.melanger();
    }

    private void distribuer() {
        // 1. On remplit la grille (pioche) avec 9 cartes face cachée
        for (int i = 0; i < 9; i++) {
            this.grilleCentrale.ajouterCarte(this.paquetInitial.retirerDerniereCarte());
        }

        // 2. On distribue le reste (27 cartes) équitablement aux joueurs
        int nbJoueurs = this.listeJoueurs.size();
        int indexJoueur = ThreadLocalRandom.current().nextInt(nbJoueurs);

        while (!this.paquetInitial.estVide()) {
            Cartes c = this.paquetInitial.retirerDerniereCarte();
            this.listeJoueurs.get(indexJoueur).getDeckJoueur().ajouterCarte(c);
            indexJoueur = (indexJoueur + 1) % nbJoueurs;
        }

        // 3. Tri des mains (important pour pouvoir demander "plus haute / plus basse")
        for (Joueur j : listeJoueurs) {
            j.getDeckJoueur().trierCartes();
        }
    }

    // --- LOGIQUE DU TOUR (Appelée par le Controller) ---

    /**
     * Reçoit une carte sélectionnée (depuis la grille ou la main d'un joueur)
     * 
     * @param c La carte choisie
     * @return 0: Continue, 1: Trio Trouvé, -1: Erreur/Fin de tour
     */
    public int selectionnerCarte(Cartes c) {
        cartesReveleesCeTour.add(c);

        // Si ce n'est pas la première carte, on compare
        if (cartesReveleesCeTour.size() > 1) {
            if (c.getValeur() != cartesReveleesCeTour.get(0).getValeur()) {
                terminerLeTour(false);
                return -1; // Échec
            }
        }

        // Si on a les trois
        if (cartesReveleesCeTour.size() == 3) {
            validerTrioGagne();
            terminerLeTour(true);
            return 1; // Succès
        }

        return 0; // Continue le tour
    }

    private void validerTrioGagne() {
        Joueur jActif = getJoueurActif();
        for (Cartes c : cartesReveleesCeTour) {
            jActif.getStock().ajouterCarte(c);

            if (!this.grilleCentrale.retirerCarte(c)) {
                for (Joueur j : listeJoueurs) {
                    if (j.getDeckJoueur().retirerCarte(c)) {
                        break;
                    }
                }
            }
        }
    }

    private void terminerLeTour(boolean estSucces) {
        cartesReveleesCeTour.clear();
        if (!estSucces) {
            nextPlayer();
        }
    }

    // --- UTILITAIRES ---

    public void nextPlayer() {
        this.idJoueurActif = (this.idJoueurActif + 1) % this.listeJoueurs.size();
    }

    public boolean checkVictory() {
        return getJoueurActif().getStock().a_gagner();
    }

    public Joueur getJoueurActif() {
        return this.listeJoueurs.get(this.idJoueurActif);
    }

    public List<Joueur> getListeJoueurs() {
        return this.listeJoueurs;
    }

    public Pioche getGrilleCentrale() {
        return this.grilleCentrale;
    }
}