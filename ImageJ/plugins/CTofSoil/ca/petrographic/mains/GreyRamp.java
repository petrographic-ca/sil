package ca.petrographic.mains;

import ca.petrographic.SilLibrary;
import ij.ImagePlus;

public class GreyRamp {
  public static void main(String argv[]) {
    System.setProperty("java.awt.headless", "true"); // suppress IJ GUI
    ImagePlus imp = SilLibrary.makeGreyRamp(10, 10, 10);
    System.out.println("stub - ImagePlus obj at: " + System.identityHashCode(imp));
  }
}
