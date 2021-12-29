public class Cli {
  public void run(String... args) {
    ImageGenerator imgg = new ImageGenerator();
    String output = "images/output.png";
    String f = "+ * z z c -0.729 0.1889";

    boolean isMandelbrot = false;
    boolean makeTextFile = false;
    boolean printTime = false;

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
          imgg.setZoom(Double.parseDouble(sa[1]));
          break;
        case "--shiftx":
          imgg.setShiftX(Double.parseDouble(sa[1]));
          break;
        case "--shifty":
          imgg.setShiftY(Double.parseDouble(sa[1]));
          break;
        case "--x1":
          imgg.setX1(Double.parseDouble(sa[1]));
          break;
        case "--y1":
          imgg.setY1(Double.parseDouble(sa[1]));
          break;
        case "--x2":
          imgg.setX2(Double.parseDouble(sa[1]));
          break;
        case "--y2":
          imgg.setY2(Double.parseDouble(sa[1]));
          break;
        case "--step":
          imgg.setStep(Double.parseDouble(sa[1]));
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
          imgg.setIntensity(Float.parseFloat(sa[1]));
          break;
        case "--luminosity":
          imgg.setDrawFunction("Luminosité");
          break;
        case "--antialiasing":
          imgg.setAntiAliasing(true);
          imgg.setAntiAliasingAmount(Integer.parseInt(sa[1]));
          break;
        case "--singlethread":
          imgg.setIsSingleThread(true);
          break;
        case "--time":
          printTime = true;
          break;
        case "--help":
          usage();
          System.exit(0);
          break;
        default:
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
    double time1 = System.currentTimeMillis();
    imgg.generateBuffer();
    double time2 = System.currentTimeMillis();
    imgg.createImage(output);
    if (makeTextFile) imgg.createTextFile(output);
    if (printTime)
      System.out.println("Fractale générée en " + ((time2 - time1) / 1000) + " secondes");
  }

  private void usage() {
    System.err.println(
        "\n"
            + "Fractalmaker: générateur de fractales\n\n"
            + "  Utilisation:  java -jar fractalmaker.jar [arguments]\n\n"
            + "  Si aucun argument n'est donné, lance l'interface graphique\n\n"
            + "Arguments: \n\n"
            + "  --width=[arg] --height=[arg]\n"
            + "        Largeur et hauteur de l'image. Modifie aussi x1, y1, x2 et y2\n\n"
            + "  --zoom=[arg]\n"
            + "        Zoom de l'image. Plus le nombre est proche de 0, plus le zoom est grand\n\n"
            + "  --shiftx=[arg] --shifty=[arg]\n"
            + "        Decalage de l'image sur X et Y\n\n"
            + "  --x1=[arg] --y1=[arg] --x2=[arg] --y2=[arg]\n"
            + "        Coordonnées des points opposés du rectangle représenté sur le plan\n"
            + "        complexe. Modifie la taille de l'image\n\n"
            + "  --step=[arg]\n"
            + "        Pas de discrétisation. Plus il est petit, plus l'image est grande\n\n"
            + "  --output=[arg]\n"
            + "        Nom de sortie de l'image\n\n"
            + "  --mandelbrot\n"
            + "        Crée une representation d'un ensemble de mandelbrot\n\n"
            + "  --julia=[arg]\n"
            + "        Crée une representation d'un ensemble de Julia utilisant la fonction\n"
            + "        fournie à l'argument\n\n"
            + "  --text\n"
            + "        Crée un fichier texte décrivant la fractale\n\n"
            + "  --intensity=[arg]\n"
            + "        Intensité de l'affichage\n\n"
            + "  --luminosity\n"
            + "        L'image sera en noir et blanc (et un peu bleu)\n\n"
            + "  --iterations=[arg]\n"
            + "        Nombre d'itérations de la fonction complexe avant affichage\n\n"
            + "  --antialiasing=[arg]\n"
            + "        Qualité de l'anti-crénelage. Une image avec un anti-crénelage de qualité\n"
            + "        n prendra n^2 fois plus de temps à être calculée. Maximum conseillé : 4\n\n"
            + "  --singlethread\n"
            + "        Désactive le multi-threading\n\n"
            + "  --time\n"
            + "        Affiche la durée du calcul de l'image\n\n"
            + "  --help\n"
            + "        Affiche l'aide\n");
  }
}
