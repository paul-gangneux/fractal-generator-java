public class Mandelbrot implements TwoDoublesToInt {

    private int max=200;

    public void setMax(int max) {if (max>=0) this.max=max;}

    @Override
    public int doublesToInt(double x, double y) {
        Complex c = new Complex(x,y);
        Complex z = new Complex(0,0);
        for (int i = 0; i < max; i++) {
            z = (z.multiply(z)).add(c);
            if (z.abs() >= 2.0) return i;
        }
        return max;
    }

    @Override
    public int maxValue() {
        return max;
    }

    @Override
    public int minValue() {
        return 0;
    }
    
}