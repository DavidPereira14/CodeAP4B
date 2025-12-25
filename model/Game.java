package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private EnsembleDeCartes paquetInitial; // Pour la génération
    private ArrayList<Joueur> listeJoueurs;
    private Pioche grilleCentrale;
    private int idJoueurActif;

    // Structure interne pour lier une carte à son propriétaire d'origine
    private class ReserveInfo {
        Cartes carte;
        int idProprietaire;

        ReserveInfo(Cartes c, int id) {
            this.carte = c;
            this.idProprietaire = id;
        }
    }

    // Liste des cartes sorties des mains avec leurs propriétaires
    private List<ReserveInfo> cartesEnReserve = new ArrayList<>();

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

    private void preparerJeuDeCartes() {
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

            for (int i = 0; i < 3; i++) {
                this.paquetInitial.ajouterCarte(new Cartes(val, nom, img));
            }
        }
        this.paquetInitial.melanger();
    }

    private void distribuer() {
        for (int i = 0; i < 9; i++) {
            this.grilleCentrale.ajouterCarte(this.paquetInitial.retirerDerniereCarte());
        }

        int nbJoueurs = this.listeJoueurs.size();
        int indexJoueur = ThreadLocalRandom.current().nextInt(nbJoueurs);

        while (!this.paquetInitial.estVide()) {
            Cartes c = this.paquetInitial.retirerDerniereCarte();
            this.listeJoueurs.get(indexJoueur).getDeckJoueur().ajouterCarte(c);
            indexJoueur = (indexJoueur + 1) % nbJoueurs;
        }

        for (Joueur j : listeJoueurs) {
            j.getDeckJoueur().trierCartes();
        }
    }

    // --- LOGIQUE DU TOUR ---

    public int selectionnerCarte(Cartes c) {
        cartesReveleesCeTour.add(c);

        if (cartesReveleesCeTour.size() > 1) {
            if (c.getValeur() != cartesReveleesCeTour.get(0).getValeur()) {
                terminerLeTour(false);
                return -1; // Échec
            }
        }

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

    /**
     * Rend chaque carte de la réserve à son propriétaire d'origine.
     */
    public void restituerToutesLesCartes() {
        for (ReserveInfo info : cartesEnReserve) {
            Joueur proprio = getJoueurParId(info.idProprietaire);
            if (proprio != null) {
                proprio.getDeckJoueur().ajouterCarte(info.carte);
                proprio.getDeckJoueur().trierCartes();
            }
        }
        this.cartesEnReserve.clear();
    }

    public void viderReserve() {
        this.cartesEnReserve.clear();
    }

    public Joueur getJoueurParId(int id) {
        if (id >= 0 && id < listeJoueurs.size()) {
            return listeJoueurs.get(id);
        }
        return null;
    }

    public Cartes tirerCarteJoueur(int idJoueur, boolean estHaut) {
        Joueur j = getJoueurParId(idJoueur);
        if (j == null) return null;

        // Appel de vos méthodes de DeckJoueur
        Cartes c = estHaut ? j.getDeckJoueur().recupererCPH() : j.getDeckJoueur().recupererCPB();

        if (c != null) {
            j.getDeckJoueur().retirerCarte(c);
            // On mémorise la carte ET l'ID du joueur à qui on l'a prise
            this.cartesEnReserve.add(new ReserveInfo(c, idJoueur));
            return c;
        }
        return null;
    }

    private void terminerLeTour(boolean estSucces) {
        cartesReveleesCeTour.clear();
       nextPlayer();
    }

    // --- UTILITAIRES ---

    public void nextPlayer() {
        this.idJoueurActif = (this.idJoueurActif + 1) % this.listeJoueurs.size();
    }

    public boolean checkVictory(Joueur j) {
        return j.getStock().a_gagner();
    }
    
    public Joueur getJoueurPrecedent() {
        int idPrecedent = (idJoueurActif - 1 + listeJoueurs.size()) % listeJoueurs.size();
        return listeJoueurs.get(idPrecedent);
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