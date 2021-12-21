### Entrer un polynome pour un ensemble de Julia
Nous utilisons le moteur de script Nashorn pour interpreter les fonctions fournies,
et exposons une classe Complex avec les methodes suivantes;
 - `Complex(double valeur_reelle, double valeur_imaginaire)`
 - `add(Complex z)`
 - `multiply(Complex z)`

Voici un exemple d'utilisation de cette classe pour calculer un ensemble de julia utilisant
la valeur `0 + 0.8i` : `java -classpath bld Main --julia="z.multiply(z).add(new Complex(0, 0.8))"`

Il est a noter que cette fonctionnalite rends le programme extremement peu securise, et ne doit pas etre exposee a un utilisateur potentiellement malveillant.

