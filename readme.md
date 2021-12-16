### Entrer un polynome pour un ensemble de Julia
On utilise la notation polonaise, avec les operateurs +, -, * et /.
Un nombre complexe est represente comme ceci: (c [partie reelle] [partie imaginaire])
On a egalement l'entree de la fonction z.
On represente donc f(z) = z\*z+(-0.729 + 0.1889i) comme ceci:
	`+ * z z c -0.729 0.1889`

Un appel typique serai donc `java -classpath bld Main --julia="+ * z z c -0.729 0.1889"`
