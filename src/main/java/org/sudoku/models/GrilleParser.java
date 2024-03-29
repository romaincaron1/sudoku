package org.sudoku.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sudoku.exceptions.ElementInterditException;
import org.sudoku.exceptions.HorsBornesException;
import org.sudoku.exceptions.ValeurImpossibleException;
import org.sudoku.exceptions.ValeurInitialeModificationException;

/**.
 * Méthodes utilitaire permettant de créer une Grille à partir d'un fichier texte.
 * Il est attendu que la première ligne contiennet : le symbole de case vide, suivi des symboles possibles dans la grille (en UTF-8).
 * Les autres lignes contiennent le contenu de la grille.
 * <p>
 * Exemple:
 * -1234
 * 2---
 * 1--3
 * 3--4
 * -1-2
 *
 * @author Sébastien Choplin <sebastien.choplin@u-picardie.fr>
 */
public class GrilleParser{

    /**.
     * constructeur.
     */
    public GrilleParser() {
    }
   

    /**.
     * Fonction parse.
     *
     * @param in recu
     * @throws IOException               format de grille en caractere incorrect
     * @throws ValeurImpossibleException si la grille ne respècte pas les règles
     */
    public static Grille parse(final File in)
            throws IOException, ElementInterditException, ValeurInitialeModificationException, HorsBornesException, ValeurImpossibleException {
        try (BufferedReader reader = new BufferedReader(new FileReader(in))) {


            String line = reader.readLine();
            if (line == null || line.length() == 0) {
                throw new IllegalArgumentException("pas de première ligne ?");
            }
            final int dimension = line.length() - 1;
            final char vide = line.charAt(0);
            Map<Character, ElementDeGrille> elementDeGrilleMap = new HashMap<>();
            for (int i = 1; i < line.length(); i++) {
                char value = line.charAt(i);
                if (value == vide) {
                    continue;
                }
                if (elementDeGrilleMap.containsKey(value)) {
                    throw new IllegalArgumentException("valeur possible dupliquée : " + value);
                }
                elementDeGrilleMap.put(value, new ElementDeGrilleImplAsChar(value));
            }

            if (elementDeGrilleMap.size() != dimension) {
                throw new IllegalArgumentException("pas le bon nombre de valeurs possibles");
            }
            ElementDeGrille[] elementDeGrilles = elementDeGrilleMap.values().toArray(new ElementDeGrille[]{});
            ElementDeGrille[][] grilleTab = new ElementDeGrille[dimension][dimension];


            for (int i = 0; i < dimension; i++) {
                line = reader.readLine();
                if (line == null || line.length() != dimension) {
                    throw new IOException("pas le bon nombre sur la ligne : " + line);
                }
                for (int j = 0; j < dimension; j++) {
                    char c = line.charAt(j);
                    if (c != vide) {
                        ElementDeGrille elementDeGrille = elementDeGrilleMap.get(c);
                        if (elementDeGrille == null) {
                            throw new ValeurImpossibleException(String.valueOf(c));
                        }
                        grilleTab[i][j] = elementDeGrille;
                    }
                }
            }

            return new GrilleImpl(elementDeGrilles, grilleTab);
        }
    }

}
