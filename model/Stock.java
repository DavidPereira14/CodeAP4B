package model;

public class Stock extends EnsembleDeCartes {
    public Stock() {
        super();
    }

    public void ajouterCarte(Cartes c) {
        this.listeCartes.add(c);
    }

    public boolean a_gagner() {
        if (this.getTaille() == 9) {
            return true;
        }
        for (Cartes c : this.listeCartes) {
            if (c.getValeur() == 77) {
                return true;
            }
        }
        return false;
    }

    public Cartes premiereCarte() {
        return this.listeCartes.get(0);
    }

    public void supprimerTrio() {
        this.listeCartes.clear();
    }
}
