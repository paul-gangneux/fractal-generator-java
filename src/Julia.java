import java.util.function.Function;

// A main to test this class
// ImageGenerator ig = new ImageGenerator();
//
// Function<Complex,Complex> f = z -> z.multiply(z).add(new Complex(-0.729, 0.1889));
// TwoDoublesToInt func = Julia.JuliaFactory(1000, 0, 2, f);
// ig.create(func, "images/image.png");
 

public class Julia implements TwoDoublesToInt {
	private int max = 1000;
	private int min = 0;
	private int radius = 2;
	private Function<Complex,Complex> func
		= z -> z.multiply(z).add(new Complex(-0.729, 0.1889));

	public static Julia JuliaFactory(
		int max, int min, int radius, Function<Complex,Complex> f) {
		Julia x = new Julia();
		x.max = max;
		x.min = min;
		x.radius = radius;
		x.func = f;

		return x;
	}

	/**
	 * Fonction utilisee pour parser la fonction f(x) d'un ensemble de julia.
	 * Operations supportees: + - / * et parentheses
	 */
	public static Function<Complex,Complex> parseFxFromString(String fx) {
		Function<Complex,Complex> func
			= z -> z.multiply(z).add(new Complex(-0.729, 0.1889));

		return func;
	}

	@Override
	public int doublesToInt(double x, double y) {
		int res = 0;
		Complex z = new Complex(x, y);
		while (res < max-1 && z.abs() <= radius) {
			z = func.apply(z);
			res++;
		}
		return res;
	}

	@Override
	public int maxValue() { return max; }

	@Override
	public int minValue() { return min; }
}
