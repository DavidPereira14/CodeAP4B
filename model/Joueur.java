
package model;

public class Joueur {
    private static int nextId = 1;
    private int idJoueur;
    private String nom;
    private DeckJoueur deck;
    private Stock stock;

    public Joueur(String nom) {
        this.idJoueur = nextId++;
        this.nom = nom;
        this.deck = new DeckJoueur();
        this.stock = new Stock();
    }

    public int getId() {
        return this.idJoueur;
    }

    public String getNom() {
        return this.nom;
    }

    public DeckJoueur getDeckJoueur() {
        return this.deck;
    }

    public void afficherDeck() {
        this.deck.afficherCartes();
    }

    public Stock getStock() {
        return this.stock;
    }
}
