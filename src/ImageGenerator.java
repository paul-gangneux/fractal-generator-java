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

    private double x1,y1;
    private double x2,y2;

    private double delta;

    private int threshold; // pour le multithreading

    private boolean antiAliasing;

    private class MainWork extends RecursiveAction {

        private int hmin;
        private int hmax;
        private double ox;
        private double oy;

        /*
        private int valueToColor(int v) {
            if (v>255 || v<0) return 0xff0000; //cas erreur
            if (v==255) return 0;
            return rgbToInt(255-v, 255-v, v);
            
        }
        */

        public MainWork(int hmin, int hmax, double ox, double oy) {
            this.hmin = hmin;
            this.hmax = hmax;
            this.ox = ox;
            this.oy = oy;
        }

        private int valueToColor(int v, int min, int max) {
            if (v<min || v>max) return 0x000000; //cas erreur
            if (v==max) return 0;
            return Color.HSBtoRGB((float)(v+min)/max, 0.8f, 0.7f);
        }     

        @Override
        protected void compute() {
            if (hmax-hmin>threshold) {
                invokeAll(
                    new MainWork(hmin, (hmin+hmax)/2, ox, oy),
                    new MainWork((hmin+hmax)/2, hmax, ox, oy));
            } else {
                int min = function.minValue();
                int max = function.maxValue();
                double x = ox;
                double yReset = oy + hmin*delta*zoom;
                double y = yReset;

                for (int i=0; i<width; i++) {
                    for (int j=hmin; j<hmax; j++) {
                        int val = function.doublesToInt(x,y);
                        int col = valueToColor(val, min, max);
                        image.setRGB(i,j,col);
                        y += delta*zoom;
                    }
                    x += delta*zoom;
                    y = yReset;
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
    
                        int pR = averageRed(p1, p2, p3, p4);
                        int pG = averageGreen(p1, p2, p3, p4);
                        int pB = averageBlue(p1, p2, p3, p4);
                        
                        int pixel = rgbToInt(pR,pG,pB);
                        buffer.setRGB(i, j, pixel);
                    }
                }
            }
        }
    }

    public ImageGenerator() {
        // valeurs par défaut
        zoom = 1;
        shiftX = 0;
        shiftY = 0;
        antiAliasing = false;
        threshold = 64;
        
        x1 = -1; y1 = -1;
        x2 = 1; y2 = 1;
        setDelta(0.01); // set height et width automatiquement
    }

    // setters
    public void setWidth(int width) {
        if (width<1) return;
        x2 = x1 + width*delta;
        this.width = width;
    }
    public void setHeight(int height) {
        if (height<1) return;
        y2 = y1 + height*delta;
        this.height = height;
    }
    public void setZoom(double zoom) {
        if (zoom>0) this.zoom = zoom;
    }
    public void setShiftX(double shiftX) {this.shiftX = shiftX;}
    public void setShiftY(double shiftY) {this.shiftY = shiftY;}
    public void setAntiAliasing(boolean antiAliasing) {this.antiAliasing = antiAliasing;}
    public void setPoint1(double x, double y) {
        if (x > x2-delta) x1 = x2-delta;
        else x1=x;
        if (y > y2-delta) y1 = y2-delta;
        else y1 = y;
        width = (int)((x2-x1)/delta);
        height = (int)((y2-y1)/delta);
    }
    public void setPoint2(double x, double y) {
        if (x < x1+delta) x2 = x1+delta;
        else x2 = x;
        if (y < y1+delta) y2 = y1+delta;
        else y2 = y;
        width = (int)((x2-x1)/delta);
        height = (int)((y2-y1)/delta);
    }
    public void setDelta(double d) {
        if (d <= 0) return;
        delta = d;
        width = (int)((x2 - x1) / d);
        height = (int)((y2 - y1) / d);
    }

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

    // somme les valeurs R, G ou B de plusieurs couleurs, puis renvoie leur moyenne

    private int averageRed(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractRed(c);
        }
        return sum / colors.length;
    }

    private int averageGreen(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractGreen(c);
        }
        return sum / colors.length;
    }

    private int averageBlue(int ... colors) {
        int sum = 0;
        for (int c : colors) {
            sum += extractBlue(c);
        }
        return sum / colors.length;
    }

    //todo : commenter le code sinon olivier va me taper (mais là j'ai la flemme)
    public void create(TwoDoublesToInt f, String pathname) {

        function=f;
        BufferedImage smol=null;

        int threadsNb = Runtime.getRuntime().availableProcessors();

        if (antiAliasing) {
            smol=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            height *=2;
            width *=2;
            delta /= 2;
        }
        
        image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        double ox = x1 + (1-zoom)*(x2-x1)/2 + shiftX;
        double oy = y1 + (1-zoom)*(y2-y1)/2 + shiftY;

        threshold = height/threadsNb;
        RecursiveAction work = new MainWork(0, height, ox, oy);
        ForkJoinPool pool = new ForkJoinPool();

        pool.invoke(work);        

        if (antiAliasing) {
            height /=2;
            width /=2;
            delta *=2;
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
