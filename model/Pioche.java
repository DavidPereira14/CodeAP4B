package model;

public class Pioche extends EnsembleDeCartes {
    private int idPioche;

    public Pioche() {
        super();
        this.idPioche = 7;
    }

    // Override pour empêcher le décalage : on remplace par null
    // Modifié par Blusk
    @Override
    public boolean retirerCarte(Cartes c) {
        int index = this.listeCartes.indexOf(c);
        if (index != -1) {
            this.listeCartes.set(index, null);
            return true;
        }
        return false;
    }

    // Override pour gérer les nulls
    // Fait par Blusk
    public Cartes devoilerCarte(int index) {
        if (index >= 0 && index < this.listeCartes.size()) {
            return this.listeCartes.get(index);
        }
        return null;
    }

}
