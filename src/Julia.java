import java.util.EmptyStackException;
import java.util.Stack;
import java.util.function.Function;

// A main to test this class
// ImageGenerator ig = new ImageGenerator();
//
// Function<Complex,Complex> f = z -> z.multiply(z).add(new Complex(-0.729, 0.1889));
// TwoDoublesToInt func = Julia.JuliaFactory(1000, 0, 2, f);
// ig.create(func, "images/image.png");

public class Julia implements FractalFunction {
  private int max = 1000;
  private int min = 0;
  private int radius = 2;
  private String functionString = "+ * z z c -0.729 0.1889";
  private Function<Complex, Complex> func = z -> z.multiply(z).add(new Complex(-0.729, 0.1889));

  public Julia(int max, int min, int radius, String functionString)
      throws IllegalArgumentException {
    Function<Complex, Complex> f = Julia.parseFxFromString(functionString);
    if (f == null) throw new IllegalArgumentException();
    this.max = max;
    this.min = min;
    this.radius = radius;
    this.functionString = functionString;
    this.func = f;
  }

  public Julia(int max, String functionString) throws IllegalArgumentException {
    this(max, 0, 2, functionString);
  }

  public Julia() throws IllegalArgumentException {
    this(1000, 0, 2, "+ * z z c -0.729 0.1889");
  }

  public String getFunctionString() {
    return functionString;
  }

  /**
   * Fonction utilisee pour parser la fonction f(x) d'un ensemble de julia. Operations supportees: +
   * * c et z
   */
  public static Function<Complex, Complex> parseFxFromString(String fx) {
    // On parse en notation polonaise
    String[] f = fx.split(" ");
    Stack<String> top = new Stack<>();

    // il faut mettre les strings dans le sens inverse
    for (int i = f.length - 1; i >= 0; i--) {
      top.push(f[i]);
    }

    Function<Complex, Complex> func;
    try {
      func = recursiveParse(top);
    } catch (EmptyStackException | NumberFormatException e) {
      return null;
    }
    return func;
  }

  private static Function<Complex, Complex> recursiveParse(Stack<String> formula)
      throws EmptyStackException, NumberFormatException {
    String op = formula.pop();

    Function<Complex, Complex> f = z -> z;

    switch (op) {
      case "+":
        Function<Complex, Complex> f1 = recursiveParse(formula);
        Function<Complex, Complex> f2 = recursiveParse(formula);
        if (f1 == null || f2 == null) return null;
        f = z -> (f1.apply(z)).add(f2.apply(z));
        break;
      case "*":
        Function<Complex, Complex> f3 = recursiveParse(formula);
        Function<Complex, Complex> f4 = recursiveParse(formula);
        if (f3 == null || f4 == null) return null;
        f = z -> (f3.apply(z)).multiply(f4.apply(z));
        break;
      case "c":
        double d1 = Double.parseDouble(formula.pop());
        double d2 = Double.parseDouble(formula.pop());
        f = z -> new Complex(d1, d2);
        break;
      case "z":
        f = z -> z;
        break;
      case "cos":
        Function<Complex, Complex> f5 = recursiveParse(formula);
        if (f5 == null) return null;
        f = z -> (f5.apply(z)).cos();
        break;
      case "sin":
        Function<Complex, Complex> f6 = recursiveParse(formula);
        if (f6 == null) return null;
        f = z -> (f6.apply(z)).sin();
        break;
      case "power":
        Function<Complex, Complex> f7 = recursiveParse(formula);
        int n = Integer.parseInt(formula.pop());
        if (f7 == null) return null;
        f = z -> (f7.apply(z)).power(n);
        break;
      default:
        return null;
    }

    return f;
  }

  @Override
  public int doublesToInt(double x, double y) {
    int res = min;
    Complex z = new Complex(x, y);
    while (res < max && z.abs() <= radius) {
      z = func.apply(z);
      res++;
    }
    return res;
  }

  @Override
  public int maxValue() {
    return max;
  }

  @Override
  public int minValue() {
    return min;
  }

  @Override
  public void setMax(int newMax) {
    if (newMax < min) max = min + 1;
    else max = newMax;
  }
}
