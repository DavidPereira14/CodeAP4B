package view;

import model.Cartes;
import model.Game;
import model.Joueur;
import javax.swing.*;
import controller.GameController;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameView extends JFrame {
    private JPanel panelGrille; // Pour les 9 cartes au centre
    private JPanel panelJoueurs; // Pour les 4 joueurs
    private JLabel labelMessage; // Pour afficher le message
    private JButton[] boutonsGrille; // Les 9 boutons de la grille
    private List<JButton> boutonsActionJoueurs; // Pour "Haut/Bas"
    private String CHEMIN_DOS = "carte/back.png";
    private GameController controller;

    public GameView() {

        this.setTitle("Trio");
        this.setSize(1000, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));

        // 1. Zone de message (Haut)
        labelMessage = new JLabel("Bienvenue dans Trio ! Attente du lancement...", SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(labelMessage, BorderLayout.NORTH);

        // 2. Grille centrale 3x3 (Centre)
        panelGrille = new JPanel(new GridLayout(3, 3, 5, 5));
        boutonsGrille = new JButton[9];
        for (int i = 0; i < 9; i++) {
            boutonsGrille[i] = new JButton();
            boutonsGrille[i].setIcon(changerImageRedimensionnee(CHEMIN_DOS));
            boutonsGrille[i].setPreferredSize(new Dimension(100, 150));
            panelGrille.add(boutonsGrille[i]);
        }

        this.add(panelGrille, BorderLayout.CENTER);

        // 3. Zone des Joueurs (Droite)
        panelJoueurs = new JPanel();
        panelJoueurs.setLayout(new BoxLayout(panelJoueurs, BoxLayout.Y_AXIS));
        panelJoueurs.setPreferredSize(new Dimension(250, 0));
        panelJoueurs.setBorder(BorderFactory.createTitledBorder("Joueurs"));
        this.add(panelJoueurs, BorderLayout.EAST);

        this.setVisible(true);
    }

    public List<String> demanderNomsJoueurs() {
        List<String> joueurs = new ArrayList<>();
        String input = JOptionPane.showInputDialog(this, "Nombre de joueurs (3 - 6) ?");
        if (input != null) {
            try {
                int nb = Math.min(6, Math.max(3, Integer.parseInt(input)));
                for (int i = 0; i < nb; i++) {
                    String nom = JOptionPane.showInputDialog(this, "Nom du joueur " + (i + 1) + " ?");
                    joueurs.add(nom != null && !nom.isEmpty() ? nom : "Joueur " + (i + 1));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return joueurs;
    }

    public void setController(GameController controller) {
        this.controller = controller;
        for (int i = 0; i < 9; i++) {
            final int index = i;
            this.boutonsGrille[i].addActionListener(e -> controller.choisirCarteGrille(index));
        }
    }

    public void initialiserZoneJoueurs(List<Joueur> joueurs, GameController controller) {
        panelJoueurs.removeAll();
        for (Joueur J : joueurs) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel n = new JLabel(J.getNom() + "(Trios : 0)");

            JButton btnHaut = new JButton("Haut");
            JButton btnBas = new JButton("Bas");

            btnHaut.addActionListener(e -> controller.choisirCarteJoueur(J.getId(), true));
            btnBas.addActionListener(e -> controller.choisirCarteJoueur(J.getId(), false));

            p.add(n);
            p.add(btnHaut);
            p.add(btnBas);
            panelJoueurs.add(p);
        }
        panelJoueurs.revalidate();
        panelJoueurs.repaint();
    }

    private ImageIcon changerImageRedimensionnee(String chemin) {
        ImageIcon icone = new ImageIcon(chemin);
        Image img = icone.getImage();

        Image nouvelleImage = img.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(nouvelleImage);
    }

    public void revelerCarteGrille(int index, Cartes c) {
        boutonsGrille[index].setIcon(changerImageRedimensionnee(c.getImage()));
    }

    public void revelerCarteDepuisMain(int idJoueur, Cartes carte) {
        ImageIcon icone = chargerImageRedimensionnee(carte.getImage());
        JOptionPane.showMessageDialog(this, "Carte révélée : " + carte.getNom(), "Main de joueur " + idJoueur,
                JOptionPane.INFORMATION_MESSAGE, icone);
    }

    public void cacherCartesGrille() {
        for (JButton b : boutonsGrille) {
            b.setIcon(changerImageRedimensionnee(CHEMIN_DOS));
        }
    }

    public void actualiserTourJoueur(String nom) {
        labelMessage.setText("Cest au tour de " + nom);
        labelMessage.setForeground(Color.BLUE);
    }

    public void actualiserScores(List<Joueur> joueurs) {
        initialiserZoneJoueurs(joueurs, this.controller);
    }

    public void afficherMessage(String msg) {
        labelMessage.setText(msg);
    }

    public void afficherVictoire(String nom) {
        JOptionPane.showMessageDialog(this, "BRAVO ! " + nom + " a gagné la partie !");
    }

    private ImageIcon chargerImageRedimensionnee(String chemin) {
        try {
            ImageIcon icone = new ImageIcon(chemin);
            Image img = icone.getImage();
            Image nouvelleImg = img.getScaledInstance(110, 160, Image.SCALE_SMOOTH);
            return new ImageIcon(nouvelleImg);
        } catch (Exception e) {
            return null; // Retourne un carré vide si l'image manque
        }
    }

}
