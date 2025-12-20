package model;

import java.util.ArrayList;
import java.util.Collections;

public class EnsembleDeCartes {
    protected ArrayList<Cartes> listeCartes;
    private int nbCartes;

    public EnsembleDeCartes() {
        this.listeCartes = new ArrayList<>();
    }

    public void ajouterCarte(Cartes c) {
        this.listeCartes.add(c);
    }

    public void supprimerC(int index) {
        if (index >= 0 && index < this.listeCartes.size()) {
            this.listeCartes.remove(index);
        }
    }

    public int getTaille() {
        return this.listeCartes.size();
    }

    public void afficherCarte(int index) {
        if (index >= 0 && index < this.listeCartes.size()) {
            System.out.println(this.listeCartes.get(index).getNom() + " " + this.listeCartes.get(index).getValeur());
        }
    }

    public Cartes retirerDerniereCarte() {
        return this.listeCartes.remove(this.listeCartes.size() - 1);
    }

    public void melanger() {
        Collections.shuffle(this.listeCartes);
    }

    public boolean estVide() {
        return this.listeCartes.isEmpty();
    }

    public boolean retirerCarte(Cartes c) {
        return this.listeCartes.remove(c);
    }
}
