public class Cli {
  public void run(String... args) {
    ImageGenerator imgg = new ImageGenerator();
    String output = "images/output.png";
    String f = "+ * z z c -0.729 0.1889";
    // "z.multiply(z).add(new Complex(-0.729, 0.1889));";

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
          f = sa[1];
          break;
        default:
          System.out.println(arg);
          usage();
          System.exit(1);
          break;
      }
    }

    TwoDoublesToInt func = null;
    if (isMandelbrot) {
      func = new Mandelbrot();
    } else {
      try {
        func = Julia.JuliaFactory(1000, 0, 2, f);
      } catch (IllegalArgumentException e) {
        System.out.println(f + " n'est pas une fonction valide");
        System.exit(1);
      }
    }
    imgg.setFractalGenerationFunction(func);
    imgg.generateBuffer();
    imgg.createImage(output);
  }

  private void usage() {
    // TODO
    //
    System.err.println(
        "--width: Largeur de l'image\n"
            + "--height: Hauteur de l'image\n"
            + "--zoom: Zoom de l'image\n"
            + "--shiftx: Decalage de l'image sur X\n"
            + "--shifty: Decalage de l'image sur Y\n"
            + "--output: Path de sortie de l'image\n"
            + "--mandelbrot: Creer une representation d'un ensemble de mandelbrot.\n"
            + "--julia: Creer une representation d'un ensemble de Julia utilisant la fonction"
            + " fournie pour le calcul du pas suivant.");
  }
}
