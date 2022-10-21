package ca.petrographic.mains;

import ca.petrographic.SilLibrary;
import ij.ImagePlus;
import ij.plugin.StackWriter;
import java.io.File;
import java.util.Arrays;

public class GreyRamp {

  static String usage =
    "USAGE: <width:int> <height:int> <depth:int> <target_directory:str> <(JPEG|PNG|TIFF)>";

  public static void main(String argv[]) {
    System.setProperty("java.awt.headless", "true"); // suppress IJ GUI

    int width = 0;
    int height = 0;
    int depth = 0;

    try {
      width = Integer.parseInt(argv[0]);
      height = Integer.parseInt(argv[1]);
      depth = Integer.parseInt(argv[2]);
    } catch (NumberFormatException nfe) {
      System.err.println(usage);
      return;
    }

    String target_dir = argv[3].trim();
    if (target_dir.charAt(target_dir.length() - 1) != '/') {
      target_dir += '/';
    }
    if (!(new File(target_dir)).isDirectory()) {
      System.err.println("ERROR: target_directory does not exist.");
      return;
    }

    String format = argv[4].trim();
    if (!Arrays.asList("JPEG", "PNG", "TIFF").contains(format)) {
      // TODO: not sure if our lab needs other formats from the list below ...
      // https://github.com/imagej/ImageJ/blob/49757a4485ad727b0f2ece5aa2964f8c8d7924a4/ij/plugin/StackWriter.java#L19
      System.err.println("ERROR: format should be one of `JPEG`, `PNG`, or `TIFF`.");
      return;
    }

    ImagePlus imp = SilLibrary.makeGreyRamp(width, height, depth);
    StackWriter.save(imp, target_dir, "format=" + format + " name=slice_");
  }
}
