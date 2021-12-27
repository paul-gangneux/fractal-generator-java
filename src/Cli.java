public class Cli {
  public void run(String... args) {
    ImageGenerator imgg = new ImageGenerator();
    String output = "images/output.png";
    String f = "+ * z z c -0.729 0.1889";

    boolean isMandelbrot = false;
    boolean makeTextFile = false;

    int iterations = 1000;

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
        case "--text":
          makeTextFile = true;
          break;
        case "--iterations":
          iterations = Integer.parseInt(sa[1]);
          break;
        case "--intensity":
          imgg.setIntensity(Integer.parseInt(sa[1]));
          break;
        case "--luminosity":
          imgg.setDrawFunction("Luminosité");
          break;
        default:
          System.out.println(arg);
          usage();
          System.exit(1);
          break;
      }
    }

    FractalFunction func = null;
    if (isMandelbrot) {
      func = new Mandelbrot(iterations);
    } else {
      try {
        func = new Julia(iterations, f);
      } catch (IllegalArgumentException e) {
        System.out.println(f + " n'est pas une fonction valide");
        System.exit(1);
      }
    }
    imgg.setFractalGenerationFunction(func);
    imgg.generateBuffer();
    imgg.createImage(output);
    if (makeTextFile) imgg.createTextFile(output);
  }

  private void usage() {
    System.err.println(
        "--width: Largeur de l'image\n"
            + "--height=[arg]: Hauteur de l'image\n"
            + "--zoom=[arg]: Zoom de l'image\n"
            + "--shiftx=[arg]: Decalage de l'image sur X\n"
            + "--shifty=[arg]: Decalage de l'image sur Y\n"
            + "--output=[arg]: Nom de sortie de l'image\n"
            + "--mandelbrot: Creer une representation d'un ensemble de mandelbrot.\n"
            + "--julia=[arg]: Créer une representation d'un ensemble de Julia utilisant la fonction"
            + " fournie pour le calcul du pas suivant.\n"
            + "--text: Créer un fichier texte decrivant la fractale\n"
            + "--intensity=[arg]: Intensité de l'affichage\n"
            + "--luminosity: L'image sera en noir et blanc (et un peu bleu)\n"
            + "--iterations=[arg]: Nombre d'itérations de la fonction complexes avant affichage\n");
  }
}
