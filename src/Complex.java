class Complex {
	private double a;
	private double b;

	public double getA() { return a; }
	public double getB() { return b; }

	public Complex(double a, double b) {
		this.a = a;
		this.b = b;
	}

	public Complex increment() {
		return new Complex(a+1.0, b+1.0);
	}

	public Complex add(Complex c) {
		return new Complex(a + c.getA(), b + c.getB());
	}

	public Complex multiply(Complex c) {
		double newA = a * c.getA() - b * c.getB();
		double newB = (a + b) * (c.getA() + c.getB()) - a * c.getA() - b * c.getB();

		return new Complex(newA, newB);
	}


	public double abs() {
		return Math.sqrt(a*a + b*b);
	}
}
