Rendu projet arbre B+

Stratégies
    - Lorsque je split un noeud contenant 4 clés (ordre 3), j'ai d'un coté un noeud avec 2 clés et de l'autre 1 clé.
    - La valeur qui remonte est la plus petite valeur du second noeud.
    - J'ai choisi d'utiliser le plymorphisme pour mon projet
    - Pour la suppression je favorise toujours une redistribution (Si je ne peux pas prendre dans les noeuds adjacents je fusionne)

Structure
    - classe BTree avec pour attributs :
        - un noeud racine
        - le nombre de clés que peut contenir un bloc
    - classe Noeud avec pour attributs :
        - Type du noeud
        - Collection de clés
        - Collection de valeurs
        - Collection de Noeuds (pointeurs)
        - Noeud parent
        - Noeud droit (chainage classique)
        - Noeud gauche (chainage a double sens)

J'ai préféré adopter un chainage à double sens pour faciliter certains de mes algorithmes


Fonctionnalités réalisées :
    - Insertion
    - Affichage
    - Recherche
    - Suppression simple
    - Redistribution noeuds feuille à droite
    - Redistribution noeuds feuille à gauche
    - Fusion noeuds feuilles
    - Fusion noeuds intermédiaires

Fonctionnalité non terminée
    - Redistribution noeuds intermédiaire (Parfois les clés dans les noeuds parents ne sont pas correctement mis à jour ce qui à pour concéquence de rendre inacessibles certaines clés)

Fonctionnalités non traités
    - Cas où on créer une nouvelle racine
