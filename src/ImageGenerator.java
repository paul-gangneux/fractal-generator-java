import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class ImageGenerator {

    private int width, height;
    private double zoom;
    private double shiftX, shiftY;
    private TwoDoublesToInt function;
    private BufferedImage image;

    private int threshold;

    private boolean antiAliasing;

    private class MainWork extends RecursiveAction {

        private int hmin;
        private int hmax;
        private double sx; //shift x
        private double sy; //shift y
    
        /// changer cette fonction changera le rendu
        // peut être utile pour faire de jolis effets
        /*
        private int valueToColor(int v) {
            if (v>255 || v<0) return 0xff0000; //cas erreur
            if (v==255) return 0;
            return rgbToInt(255-v, 255-v, v);
            
        }
        */

        private int valueToColor(int v, int min, int max) {
            if (v<min || v>max) return 0x000000; //cas erreur
            if (v==max) return 0;
            return Color.HSBtoRGB((float)(v+min)/max, 0.8f, 0.7f);
        }

        public MainWork(int hmin, int hmax, double sx, double sy) {
            this.hmin = hmin;
            this.hmax = hmax;
            this.sx = sx;
            this.sy = sy;
        }

        @Override
        protected void compute() {
            if (hmax-hmin>threshold) {
                invokeAll(
                    new MainWork(hmin, (hmin+hmax)/2, sx, sy),
                    new MainWork((hmin+hmax)/2, hmax, sx, sy));
            } else {
                int min = function.minValue();
                int max = function.maxValue();
                for (int i=0; i<width; i++) {
                    for (int j=hmin; j<hmax; j++) {
                        double x = ((i*2-sx)*zoom)/(width);
                        double y = ((j*2-sy)*zoom)/(height);
                        int val = function.doublesToInt(x,y);
                        int col = valueToColor(val, min, max);
                        image.setRGB(i,j,col);
                    }
                }
            }
        }
    }

    private class AntiAliasingWork extends RecursiveAction {

        private int hmin, hmax;
        private transient BufferedImage buffer;

        public AntiAliasingWork(int hmin, int hmax, BufferedImage buffer) {
            this.hmin = hmin;
            this.hmax = hmax;
            this.buffer = buffer;
        }

        @Override
        protected void compute() {
            if (hmax-hmin>threshold) {
                invokeAll(
                    new AntiAliasingWork(hmin, (hmin+hmax)/2, buffer),
                    new AntiAliasingWork((hmin+hmax)/2, hmax, buffer));
            } else {
                for (int i = 0; i < width; i++) {
                    for (int j = hmin; j < hmax; j++) {
                        int p1, p2, p3, p4;
                        p1 = image.getRGB(i*2, j*2);
                        p2 = image.getRGB(i*2+1, j*2);
                        p3 = image.getRGB(i*2, j*2+1);
                        p4 = image.getRGB(i*2+1, j*2+1);
    
                        int pR = extractAndSumRed(p1, p2, p3, p4) / 4;
                        int pG = extractAndSumGreen(p1, p2, p3, p4) / 4;
                        int pB = extractAndSumBlue(p1, p2, p3, p4) / 4;
                        
                        int pixel = rgbToInt(pR,pG,pB);
                        buffer.setRGB(i, j, pixel);
                    }
                }
            }
        }
        
    }

    public ImageGenerator() {
        // valeurs par défaut
        width = 500;
        height = 500;
        zoom = 1;
        shiftX = 0;
        shiftY = 0;
        antiAliasing = false;
        threshold = 64;
    }

    // setters
    public void setWidth(int width) {this.width = width;}
    public void setHeight(int height) {this.height = height;}
    public void setZoom(double zoom) {this.zoom = zoom;}
    public void setShiftX(double shiftX) {this.shiftX = shiftX;}
    public void setShiftY(double shiftY) {this.shiftY = shiftY;}
    public void setAntiAliasing(boolean antiAliasing) {this.antiAliasing = antiAliasing;}

    // getters
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public double getZoom() {return zoom;}
    public double getShiftX() {return shiftX;}
    public double getShiftY() {return shiftY;}
    public boolean getAntiAliasing() {return antiAliasing;}

    private int rgbToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    // ces fonction extraient les composantes R, G et B depuis la valeur d'un pixel

    private int extractRed(int color) {
        return (color & 0xFF0000)>>16;
    }

    private int extractGreen(int color) {
        return (color & 0x00FF00)>>8;
    }

    private int extractBlue(int color) {
        return (color & 0x0000FF);
    }

    // somme les valeurs R, G ou B de plusieurs couleurs

    private int extractAndSumRed(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractRed(c);
        }
        return sum;
    }

    private int extractAndSumGreen(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractGreen(c);
        }
        return sum;
    }

    private int extractAndSumBlue(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractBlue(c);
        }
        return sum;
    }

    public void create(TwoDoublesToInt f, String pathname) {

        function=f;
        BufferedImage smol=null;

        int threadsNb = Runtime.getRuntime().availableProcessors();

        if (antiAliasing) {
            smol=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            height *=2;
            width *=2;
        }
        
        image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        double sx = width*shiftX*2.0/zoom+width;
        double sy = height*shiftY*2.0/zoom+height;

        threshold = height/threadsNb;
        RecursiveAction work = new MainWork(0, height, sx, sy);
        ForkJoinPool pool = new ForkJoinPool();

        pool.invoke(work);        

        if (antiAliasing) {
            height /=2;
            width /=2;
            threshold = height/threadsNb;
            RecursiveAction aawork = new AntiAliasingWork(0, height, smol);
            ForkJoinPool pool2 = new ForkJoinPool();
            pool2.invoke(aawork);  
            image = smol;
        }
        
        File file = new File(pathname);
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image=null;
    }
}
