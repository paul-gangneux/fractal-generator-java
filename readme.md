# Projet CPOO

## Compiler et lancer le projet

Nous avons fourni un makefile:
  - `make` compile le projet
  - `make run` lance le projet avec l'interface graphique
  - `make demo` lance plusieurs commandes typiques et genere des fractales enregistrees dans `images/` au format png

## Utilisation de l'interface graphique
L'interface graphique est composee d'un panneau d'affichage et d'un panneau de reglage.
Le panneau de reglage a 3 sous parties, le parametrage de l'image (taille, discretisation coordonees et teintes),
la fractale en elle meme (Julia ou mandelbrot, nombre d'iteration, fonction dans le cas d'un ensemble de Julia),
et enfin de reglages de la sauvegarde (chemin et nom du fichier).

## Utilisation de la ligne de commande
Pour utiliser la ligne de commande, d'abord compilez le projet avec `make`,
puis lancez le avec `java -classpath bld Main [OPTIONS]`

Exemple: `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`

Les options de la ligne de commande sont les suivantes:
```
--width: Largeur de l'image
--height: Hauteur de l'image
--zoom: Zoom de l'image
--shiftx: Decalage de l'image sur X
--shifty: Decalage de l'image sur Y
--output: Path de sortie de l'image
--mandelbrot: Creer une representation d'un ensemble de mandelbrot.
--julia: Creer une representation d'un ensemble de Julia utilisant la fonction fournie pour le calcul du pas suivant.
``` 

### Entrer un polynome pour un ensemble de Julia
On utilise la notation polonaise, avec les operateurs + et \*.
Un nombre complexe est represente comme ceci: `c [partie reelle] [partie imaginaire]`
On a egalement l'entree de la fonction z.
On represente donc f(z) = z\*z+(-0.729 + 0.1889i) comme ceci:
	`+ * z z c -0.729 0.1889`

Un appel typique serait donc `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`
