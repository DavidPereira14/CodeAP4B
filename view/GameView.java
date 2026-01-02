package view;

import model.Cartes;
import model.Joueur;
import controller.GameController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameView extends JFrame {
    private JPanel panelCentre;
    private JPanel panelGrille;
    private JPanel panelNord, panelSud, panelEst, panelOuest;
    private JPanel panelMainJoueurActif;
    private JPanel containerJoueursNord;

    // Composants pour l'affichage de la manche (Haut-Droit)
    private JPanel panelCartesChoisies;
    private JLabel[] labelsCartesChoisies;
    // Liste interne pour synchroniser l'affichage sans modifier le modèle
    private List<Cartes> cartesMancheInterne = new ArrayList<>();

    private JLabel labelMessage;
    private JLabel labelNbCartesGrille;
    private JButton[] boutonsGrille;
    private String CHEMIN_DOS = "carte/back.png";
    private GameController controller;

    private final int CARTE_W = 70;
    private final int CARTE_H = 100;
    // Dimensions pour la grille centrale (plus grandes)
    private final int GRID_CARTE_W = 100;
    private final int GRID_CARTE_H = 140;

    public GameView() {
        this.setTitle("Trio - Table de Jeu");
        this.setSize(1200, 800);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // 1. Zone Centrale
        panelCentre = new JPanel(new BorderLayout());
        labelMessage = new JLabel("Bienvenue dans Trio !", SwingConstants.CENTER);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 18));
        panelCentre.add(labelMessage, BorderLayout.NORTH);

        initGrille();
        this.add(panelCentre, BorderLayout.CENTER);

        // 2. Zones Joueurs et Manche
        panelNord = new JPanel(new BorderLayout());
        panelSud = new JPanel(new BorderLayout());
        panelEst = new JPanel(new GridBagLayout());
        panelOuest = new JPanel(new GridBagLayout());

        // Initialisation du bloc "Manche" en haut à droite
        initPanelCartesChoisies();
        panelNord.add(panelCartesChoisies, BorderLayout.EAST);

        // Conteneur joueurs haut-gauche
        containerJoueursNord = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNord.add(containerJoueursNord, BorderLayout.WEST);

        // Ajout du message au centre du bandeau Nord
        panelNord.add(labelMessage, BorderLayout.CENTER);

        // Panneau main joueur (Sud)
        panelMainJoueurActif = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMainJoueurActif.setBorder(BorderFactory.createTitledBorder("Vos Cartes"));
        panelSud.add(panelMainJoueurActif, BorderLayout.SOUTH);

        this.add(panelNord, BorderLayout.NORTH);
        this.add(panelSud, BorderLayout.SOUTH);
        this.add(panelEst, BorderLayout.EAST);
        this.add(panelOuest, BorderLayout.WEST);

        this.setVisible(true);
    }

    private void initPanelCartesChoisies() {
        panelCartesChoisies = new JPanel(new GridLayout(1, 3, 5, 0));
        panelCartesChoisies.setBorder(BorderFactory.createTitledBorder("Manche"));
        labelsCartesChoisies = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            labelsCartesChoisies[i] = new JLabel();
            labelsCartesChoisies[i].setPreferredSize(new Dimension(CARTE_W, CARTE_H));
            labelsCartesChoisies[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            labelsCartesChoisies[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelCartesChoisies.add(labelsCartesChoisies[i]);
        }
    }

    // Fonction demandée pour mettre à jour les 3 rectangles
    public void actualiserCartesManche(List<Cartes> selection) {
        for (int i = 0; i < 3; i++) {
            if (selection != null && i < selection.size()) {
                Cartes c = selection.get(i);
                labelsCartesChoisies[i].setIcon(changerImageRedimensionnee(c.getImage(), CARTE_W, CARTE_H));
            } else {
                labelsCartesChoisies[i].setIcon(null);
            }
        }
    }

    public void actualiserTourJoueur(String nom) {
        labelMessage.setText("C'est au tour de " + nom);
        labelMessage.setForeground(Color.BLUE);
    }

    public void revelerCarteDepuisMain(int idJoueur, Cartes carte) {
        // Empêche d'ajouter la même carte si on clique plusieurs fois sur le bouton
        if (!cartesMancheInterne.contains(carte) && cartesMancheInterne.size() < 3) {
            cartesMancheInterne.add(carte);
            actualiserCartesManche(cartesMancheInterne);
        }

        ImageIcon icone = changerImageRedimensionnee(carte.getImage(), 150, 220);
        JOptionPane.showMessageDialog(this,
                "Valeur : " + carte.getValeur(),
                "Joueur " + idJoueur + " révèle une carte",
                JOptionPane.INFORMATION_MESSAGE, icone);
    }

    public void initialiserZoneJoueurs(List<Joueur> joueurs, GameController controller) {
        containerJoueursNord.removeAll();
        panelEst.removeAll();
        panelOuest.removeAll();

        // Sécurité : Ré-attacher les composants fixes au panelNord après nettoyage
        panelNord.add(panelCartesChoisies, BorderLayout.EAST);
        panelNord.add(labelMessage, BorderLayout.CENTER);
        panelNord.add(containerJoueursNord, BorderLayout.WEST);

        BorderLayout layoutSud = (BorderLayout) panelSud.getLayout();
        Component centerC = layoutSud.getLayoutComponent(BorderLayout.CENTER);
        if (centerC != null)
            panelSud.remove(centerC);

        JPanel containerJStatiqueSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSud.add(containerJStatiqueSud, BorderLayout.CENTER);

        for (int i = 0; i < joueurs.size(); i++) {
            boolean estHorizontal = (i % 2 == 0);
            JPanel p = creerPanelJoueur(joueurs.get(i), controller, estHorizontal);
            switch (i % 4) {
                case 0:
                    containerJStatiqueSud.add(p);
                    break;
                case 1:
                    panelOuest.add(p);
                    break;
                case 2:
                    containerJoueursNord.add(p);
                    break;
                case 3:
                    panelEst.add(p);
                    break;
            }
        }
        rafraichirVue();
    }

    private JPanel creerPanelJoueur(Joueur j, GameController controller, boolean estHorizontal) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(j.getNom()));
        Dimension dim = estHorizontal ? new Dimension(260, 160) : new Dimension(160, 280);
        p.setPreferredSize(dim);

        JButton btnHaut = new JButton("Haut");
        JButton btnBas = new JButton("Bas");
        btnHaut.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), true));
        btnBas.addActionListener(e -> controller.choisirCarteJoueur(j.getId(), false));

        JPanel cmd = new JPanel(new GridLayout(1, 2, 5, 0));
        cmd.add(btnHaut);
        cmd.add(btnBas);
        p.add(cmd);

        JPanel trios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Cartes c : j.getStock().getCartes()) {
            trios.add(new JLabel(changerImageRedimensionnee(c.getImage(), 30, 45)));
        }
        p.add(new JScrollPane(trios));
        return p;
    }

    public void afficherMainJoueurActif(Joueur j) {
        panelMainJoueurActif.removeAll();
        ((TitledBorder) panelMainJoueurActif.getBorder()).setTitle("Main de " + j.getNom());
        for (Cartes c : j.getDeckJoueur().getCartes()) {
            panelMainJoueurActif.add(new JLabel(changerImageRedimensionnee(c.getImage(), 80, 115)));
        }
        rafraichirVue();
    }

    private void initGrille() {
        panelGrille = new JPanel(new GridLayout(3, 3, 10, 10));
        boutonsGrille = new JButton[9];
        for (int i = 0; i < 9; i++) {
            boutonsGrille[i] = new JButton();
            ImageIcon dos = changerImageRedimensionnee(CHEMIN_DOS, GRID_CARTE_W, GRID_CARTE_H);
            if (dos != null) {
                boutonsGrille[i].setIcon(dos);
                boutonsGrille[i].setContentAreaFilled(false);
                boutonsGrille[i].setBorderPainted(false);
            }
            boutonsGrille[i].setPreferredSize(new Dimension(GRID_CARTE_W, GRID_CARTE_H));
            panelGrille.add(boutonsGrille[i]);
        }
        JPanel wrapperCentral = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        wrapperCentral.add(panelGrille, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        labelNbCartesGrille = new JLabel("Cartes restantes : 9");
        wrapperCentral.add(labelNbCartesGrille, gbc);

        panelCentre.add(wrapperCentral, BorderLayout.CENTER);
    }

    public void revelerCarteGrille(int index, Cartes c) {
        if (c != null) {
            boutonsGrille[index].setEnabled(false);
            // AJOUT : Ajout visuel dans le rectangle de manche
            if (cartesMancheInterne.size() < 3) {
                cartesMancheInterne.add(c);
                actualiserCartesManche(cartesMancheInterne);
            }
            boutonsGrille[index].setIcon(changerImageRedimensionnee(c.getImage(), GRID_CARTE_W, GRID_CARTE_H));
        } else {
            boutonsGrille[index].setEnabled(false);
            boutonsGrille[index].setIcon(null);
        }
    }

    public void redimensionnerGrille(int nbCartes) {
        panelGrille.removeAll();
        int rows = 3, cols = 3;
        if (nbCartes == 8) {
            rows = 2;
            cols = 4;
        } else if (nbCartes == 6) {
            rows = 2;
            cols = 3;
        }

        panelGrille.setLayout(new GridLayout(rows, cols, 10, 10));
        boutonsGrille = new JButton[nbCartes];

        for (int i = 0; i < nbCartes; i++) {
            boutonsGrille[i] = new JButton();
            ImageIcon dos = changerImageRedimensionnee(CHEMIN_DOS, GRID_CARTE_W, GRID_CARTE_H);
            if (dos != null) {
                boutonsGrille[i].setIcon(dos);
                boutonsGrille[i].setContentAreaFilled(false);
                boutonsGrille[i].setBorderPainted(false);
            }
            boutonsGrille[i].setPreferredSize(new Dimension(GRID_CARTE_W, GRID_CARTE_H));

            final int idx = i;
            // Réattacher le controller si présent
            if (this.controller != null) {
                boutonsGrille[i].addActionListener(e -> controller.choisirCarteGrille(idx));
            }

            panelGrille.add(boutonsGrille[i]);
        }

        if (labelNbCartesGrille != null) {
            labelNbCartesGrille.setText("Cartes restantes : " + nbCartes);
        }

        // Rafraîchir l'affichage
        panelGrille.revalidate();
        panelGrille.repaint();
    }

    public void actualiserGrille(model.Pioche pioche) {
        // Sécurité : éviter dépassement d'index si la pioche change de taille

        // Note: pioche.getCartes().size() est constant (taille de la liste avec nulls)
        // if implemented that way.
        // Actually Pioche contains `ArrayList<Cartes>` which might have different size?
        // Pioche is initialized with null replacements in retirerCarte.
        // But `listeCartes` size stays same.

        for (int i = 0; i < boutonsGrille.length; i++) {
            Cartes c = pioche.devoilerCarte(i);
            if (c == null) {
                boutonsGrille[i].setEnabled(false);
                boutonsGrille[i].setIcon(null);
            } else {
                boutonsGrille[i].setIcon(changerImageRedimensionnee(CHEMIN_DOS, GRID_CARTE_W, GRID_CARTE_H));
                boutonsGrille[i].setEnabled(true);
            }
        }
        if (labelNbCartesGrille != null) {
            labelNbCartesGrille.setText("Cartes restantes : " + pioche.getNbCartesRestantes());
        }
    }

    public void cacherCartesGrille() {
        // RESET : On vide la liste interne et l'affichage quand on cache les cartes
        cartesMancheInterne.clear();
        actualiserCartesManche(cartesMancheInterne);

        for (JButton b : boutonsGrille) {
            if (b.isEnabled())
                b.setIcon(changerImageRedimensionnee(CHEMIN_DOS, GRID_CARTE_W, GRID_CARTE_H));
        }
    }

    public void actualiserTout(List<Joueur> j, Joueur act, model.Pioche p) {
        cartesMancheInterne.clear();
        actualiserCartesManche(cartesMancheInterne);

        initialiserZoneJoueurs(j, controller);
        afficherMainJoueurActif(act);
        actualiserGrille(p);
        actualiserTourJoueur(act.getNom());
    }

    public List<String> demanderNomsJoueurs() {
        List<String> joueurs = new ArrayList<>();
        String input = JOptionPane.showInputDialog(this, "Nombre de joueurs (3-6) ?");
        if (input == null)
            return new ArrayList<>();
        try {
            int nb = Math.min(6, Math.max(3, Integer.parseInt(input)));
            for (int i = 0; i < nb; i++) {
                String n = JOptionPane.showInputDialog(this, "Nom joueur " + (i + 1));
                joueurs.add(n != null && !n.isEmpty() ? n : "J" + (i + 1));
            }
        } catch (Exception e) {
        }
        return joueurs;
    }

    private ImageIcon changerImageRedimensionnee(String chemin, int w, int h) {
        if (chemin == null)
            return null;
        try {
            ImageIcon icon = new ImageIcon(chemin);
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    public void setController(GameController c) {
        this.controller = c;
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            boutonsGrille[idx].addActionListener(e -> controller.choisirCarteGrille(idx));
        }
    }

    public void afficherMessage(String m) {
        labelMessage.setText(m);
    }

    public void afficherVictoire(String n) {
        JOptionPane.showMessageDialog(this, "Gagné par " + n);
    }

    public void actualiserScores(List<Joueur> j) {
        initialiserZoneJoueurs(j, this.controller);
    }

    private void rafraichirVue() {
        this.revalidate();
        this.repaint();
    }
}