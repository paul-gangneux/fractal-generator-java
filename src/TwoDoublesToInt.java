// doit transformer deux coordonnés (généralement entre -1 et 1) en un entier
// maxValue() et minValue renvoient les valeurs min et max
// retournés par la fonction applyAsInt
public interface TwoDoublesToInt {
    int doublesToInt(double x, double y);
    int maxValue();
    int minValue();
}
