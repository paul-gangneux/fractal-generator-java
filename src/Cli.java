import java.util.function.Function;

public class Cli {
  public void run(String... args) {
    ImageGenerator imgg = new ImageGenerator();
    String output = "images/output.png";
    Function<Complex, Complex> f = z -> z.multiply(z).add(new Complex(-0.729, 0.1889));

    boolean isMandelbrot = false;
    for (String arg_tot : args) {
      String[] sa = arg_tot.split("=");
      String arg = sa[0];
      switch (arg) {
        case "--width":
          imgg.setWidth(Integer.parseInt(sa[1]));
          break;
        case "--height":
          imgg.setHeight(Integer.parseInt(sa[1]));
          break;
        case "--zoom":
          imgg.setZoom(Integer.parseInt(sa[1]));
          break;
        case "--shiftx":
          imgg.setShiftX(Integer.parseInt(sa[1]));
          break;
        case "--shifty":
          imgg.setShiftY(Integer.parseInt(sa[1]));
          break;
        case "--output":
          output = sa[1];
          break;
        case "--mandelbrot":
          isMandelbrot = true;
          break;
        case "--julia":
          f = Julia.parseFxFromString(sa[1]);
          break;
        default:
          System.out.println(arg);
          usage();
          break;
      }
    }

    TwoDoublesToInt func;
    if (isMandelbrot) {
      func = new Mandelbrot();
    } else {
      func = Julia.JuliaFactory(1000, 0, 2, f);
    }
    imgg.create(func, output);
  }

  private void usage() {
    System.err.println("bad arg");
  }
}
