package ca.petrographic.mains;

import ca.petrographic.SilLibrary;
import ij.ImagePlus;
import ij.plugin.FolderOpener;
import ij.plugin.StackWriter;
import java.io.File;
import java.util.Arrays;

public class ShearCycle {

  static String usage =
    "USAGE: <xy:fpv> <yx:fpv> <xz:fpv> <zx:fpv> <yz:fpv> <zy:fpv>\n"
      + "       <source_directory:str> <target_directory:str>\n"
      + "       <(JPEG|PNG|TIFF)>";

  public static void main(String argv[]) {
    System.setProperty("java.awt.headless", "true"); // suppress IJ GUI

    double shear_xy = 0;
    double shear_yx = 0;
    double shear_xz = 0;
    double shear_zx = 0;
    double shear_yz = 0;
    double shear_zy = 0;

    try {
      shear_xy = Double.parseDouble(argv[0]);
      shear_yx = Double.parseDouble(argv[1]);
      shear_xz = Double.parseDouble(argv[2]);
      shear_zx = Double.parseDouble(argv[3]);
      shear_yz = Double.parseDouble(argv[4]);
      shear_zy = Double.parseDouble(argv[5]);
    } catch (NumberFormatException nfe) {
      System.err.println(usage);
      return;
    }

    String source_directory = argv[6].trim();
    if (source_directory.charAt(source_directory.length() - 1) != '/') {
      source_directory += '/';
    }
    if (!(new File(source_directory)).isDirectory()) {
      System.err.println(
        "ERROR: source_directory does not exist `" + source_directory + "`.");
      return;
    }

    String target_directory = argv[7].trim();
    if (target_directory.charAt(target_directory.length() - 1) != '/') {
      target_directory += '/';
    }
    if (!(new File(target_directory)).isDirectory()) {
      System.err.println(
        "ERROR: target_directory does not exist `" + target_directory + "`.");
      return;
    }

    String format = argv[8].trim();
    if (!Arrays.asList("JPEG", "PNG", "TIFF").contains(format)) {
      // TODO: not sure if our lab needs other formats from the list below ...
      // https://github.com/imagej/ImageJ/blob/49757a4485ad727b0f2ece5aa2964f8c8d7924a4/ij/plugin/StackWriter.java#L19
      System.err.println(
        "ERROR: format should be one of `JPEG`, `PNG`, or `TIFF`.");
      return;
    }

    // https://github.com/imagej/ImageJ/blob/4d23926782511fa322b4f9957ccea086e7c79f52/ij/plugin/FolderOpener.java
    ImagePlus source_stack = FolderOpener.open(source_directory);

    ImagePlus imp =
      SilLibrary.makeShearCycle(
        source_stack,
        shear_xy,
        shear_yx,
        shear_xz,
        shear_zx,
        shear_yz,
        shear_zy
      );
    StackWriter.save(
        imp, target_directory, "format=" + format + " name=slice_");
  }
}
