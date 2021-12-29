# Projet CPOO

## Compiler et lancer le projet

Nous avons fourni un makefile:
  - `make` compile le projet et crée une archive `fractalmaker.jar`
  - `java -jar fractalmaker.jar` ou `make run` lance le projet avec l'interface graphique
  - `java -jar fractalmaker.jar [arguments]` génère une seule image sans passer par l'interface graphique
  - `make demo` lance plusieurs commandes typiques et génère des fractales enregistrées dans `images/` au format png
  - `make clean` supprime les fichiers executables et images générées dans le répertoire `images/`

## Utilisation de l'interface graphique
L'interface graphique est composée d'un panneau d'affichage et d'un panneau de réglage.
Le panneau de réglage a 3 sous parties, le paramétrage de l'image (taille, discrétisation, coordonnées, fonction d'affichage..),
la fractale en elle même (Julia ou Mandelbrot, nombre d’itération, fonction dans le cas d'un ensemble de Julia),
et enfin de réglages de la sauvegarde (chemin et nom du fichier).
On peut de plus générer un fichier texte décrivant la fractale générée, et annuler un calcul.

Il est possible décaler l'image directement avec la souris, en cliquant et bougeant l'image. Le reste de l'interface est sensé être intuitif.

## Utilisation de la ligne de commande
Pour utiliser la ligne de commande, d'abord compilez le projet avec `make`,
puis lancez le avec `java -jar fractalmaker.jar [OPTIONS]`

Exemple: `java -jar fractalmaker.jar --julia="+ * z z c -0.729 0.1889"`

Les options de la ligne de commande peuvent être affichés avec la commande `java -jar fractalmaker.jar --help`. des exemples d'utilisation se trouvent dans les instruction de `make demo`, dans le fichier `makefile`.

les options sont: 
```
--width=[arg] --height=[arg]
      Largeur et hauteur de l'image. Modifie aussi x1, y1, x2 et y2

--zoom=[arg]
      Zoom de l'image. Plus le nombre est proche de 0, plus le zoom est grand

--shiftx=[arg] --shifty=[arg]
      Decalage de l'image sur X et Y

--x1=[arg] --y1=[arg] --x2=[arg] --y2=[arg]
      Coordonnées des points opposés du rectangle représenté sur le plan complexe. Modifie la taille de l'image

--step=[arg]
      Pas de discrétisation. Plus il est petit, plus l'image est grande

--output=[arg]
      Nom de sortie de l'image

--mandelbrot
      Crée une representation d'un ensemble de mandelbrot

--julia=[arg]
      Crée une representation d'un ensemble de Julia utilisant la fonction fournie à l'argument

--text
      Crée un fichier texte décrivant la fractale

--intensity=[arg]
      Intensité de l'affichage

--luminosity
      L'image sera en noir et blanc (et un peu bleu)

--iterations=[arg]
      Nombre d'itérations de la fonction complexe avant affichage

--antialiasing=[arg]
      Qualité de l'anti-crénelage. Une image avec un anti-crénelage de qualité n prendra n^2 fois plus de temps à être calculée. Maximum conseillé : 4
      
--singlethread
      Désactive le multi-threading

--time
      Affiche la durée du calcul de l'image

--help
      Affiche l'aide
```

### Entrer un polynôme pour un ensemble de Julia
On utilise la notation préfixe, avec les opérateurs binaires (complexe, complexe) `+`, `*`, `/`, un opérateur binaire (complexe, entier) `power` et les opétateurs unaires (complexe) `cos` et `sin`.
Un nombre complexe est représente comme ceci: `c [partie réelle] [partie imaginaire]`
On a également l’entrée de la fonction `z`.
On représente donc f(z) = z\*z+(-0.729 + 0.1889i) comme ceci:
    `+ * z z c -0.729 0.1889`, équivalent à `+ power z 2 c -0.729 0.1889`

Un appel typique serait donc `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`
