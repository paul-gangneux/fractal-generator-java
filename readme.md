# Projet CPOO

## Compiler et lancer le projet

Nous avons fourni un makefile:
  - `make` compile le projet
  - `make run` lance le projet avec l'interface graphique
  - `make demo` lance plusieurs commandes typiques et génère des fractales enregistrées dans `images/` au format png

## Utilisation de l'interface graphique
L'interface graphique est composée d'un panneau d'affichage et d'un panneau de réglage.
Le panneau de réglage a 3 sous parties, le paramétrage de l'image (taille, discrétisation coordonnées et teintes),
la fractale en elle même (Julia ou Mandelbrot, nombre d’itération, fonction dans le cas d'un ensemble de Julia),
et enfin de réglages de la sauvegarde (chemin et nom du fichier).
On peut de plus générer un fichier texte décrivant la fractale générée.

## Utilisation de la ligne de commande
Pour utiliser la ligne de commande, d'abord compilez le projet avec `make`,
puis lancez le avec `java -classpath bld Main [OPTIONS]`

Exemple: `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`

Les options de la ligne de commande sont les suivantes:
```
--width: Largeur de l'image
--height: Hauteur de l'image
--zoom: Zoom de l'image
--shiftx: Décalage de l'image sur X
--shifty: Décalage de l'image sur Y
--output: Path de sortie de l'image
--mandelbrot: Créer une représentation d'un ensemble de Mandelbrot.
--julia: Créer une représentation d'un ensemble de Julia utilisant la fonction fournie pour le calcul du pas suivant.
``` 

### Entrer un polynôme pour un ensemble de Julia
On utilise la notation polonaise, avec les opérateurs + et \*.
Un nombre complexe est représente comme ceci: `c [partie réelle] [partie imaginaire]`
On a également l’entrée de la fonction z.
On représente donc f(z) = z\*z+(-0.729 + 0.1889i) comme ceci:
    `+ * z z c -0.729 0.1889`

Un appel typique serait donc `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`
