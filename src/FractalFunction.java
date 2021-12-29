// doit transformer deux coordonnés (généralement entre -1 et 1) en un entier
// maxValue() et minValue renvoient les valeurs min et max
// retournés par la fonction doublesToInt
public interface FractalFunction {
  int doublesToInt(double x, double y);

  void setMax(int max);

  int maxValue();

  int minValue();
}
