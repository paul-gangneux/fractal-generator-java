public class Julia implements TwoDoublesToInt {
	private static int MAX = 1000;
	private static int MIN = 0;
	private static int radius = 2;

	public Complex f(Complex z) {
		// TODO
		return z.increment();
	}

	public int doublesToInt(double x, double y) {
		int res = 0;
		Complex z = new Complex(x, y);
		// TODO
		while (res < MAX-1 && z.abs() <= radius) {
			z = f(z);
			res++;
		}
		return res;
	}

	public int maxValue() { return MAX; }
	public int minValue() { return MIN; }

}
