import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class FractalImage extends JLabel implements MouseInputListener {

  private transient ImageGenerator ig;
  private transient BufferedImage img;
  private Gui gui;

  private boolean calculating;

  private int anchorX = 0, anchorY = 0;
  private int newX = 0, newY = 0;

  public FractalImage(ImageGenerator ig, Gui gui) {
    this.gui = gui;
    this.ig = ig;
    img = ig.getImage();
    setIcon(new ImageIcon(img));
    addMouseListener(this);
    addMouseMotionListener(this);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    calculating = false;
  }

  private void redraw() {
    img = ig.getImageWithShift(newX - anchorX, newY - anchorY);
    setIcon(new ImageIcon(img));
  }

  private void recalculateAfterShift() {
    ig.setShiftX(ig.getShiftX() - (newX - anchorX) * ig.getZoom() * ig.getStep());
    ig.setShiftY(ig.getShiftY() - (newY - anchorY) * ig.getZoom() * ig.getStep());
    recalculate();
    anchorX = 0;
    anchorY = 0;
    newX = 0;
    newY = 0;
  }

  public void recalculate() {
    if (calculating) return;
    calculating = true;
    gui.disableAll();
    ig.applyShift();
    ig.applyZoom();
    gui.updateValues();
    gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    new Thread(
            () -> {
              ig.generateBuffer();
              gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
              setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
              img = ig.getImage();
              setIcon(new ImageIcon(img));
              gui.pack();
              calculating = false;
              gui.enableAll();
            })
        .start();
    // thisJframe.setLocationRelativeTo(null);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // nothing
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // nothing
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // nothing
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (calculating) return;
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    anchorX = e.getX();
    anchorY = e.getY();
    newX = e.getX();
    newY = e.getY();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (calculating) return;
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    recalculateAfterShift();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (calculating) return;
    newX = e.getX();
    newY = e.getY();
    redraw();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // nothing
  }
}
