package ca.petrographic;

import java.util.Arrays;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;


public class SilLibrary {

    static public ImagePlus makeGreyRamp(int width, int height, int nslices) {
        ImageStack outStack = new ImageStack(width, height);
        for(int islice = 1; islice <= nslices; islice ++) {
            int[] raw_data = new int[width * height];
            Arrays.fill(raw_data, islice);
            outStack.addSlice(new FloatProcessor(width, height, raw_data));
        }
        ImagePlus outPlus = new ImagePlus(
            "grescale_ramp_" + outStack.getWidth() + "_"
            + outStack.getHeight() + "_" + outStack.getSize(),
            outStack);
        outPlus.setDisplayRange(0, outPlus.getStack().getSize() -1);
        return outPlus;
    }

    static public ImagePlus makeBricks(
            int width, int height, int depth,
            int brick_width, int brick_height, int brick_depth,
            int margin_thickness, int stick_thickness) {
        ImageStack outStack = new ImageStack(width, height);
        for(int islice = 1; islice <= depth; islice ++) {
            int[] raw_data = new int[width * height];

            Arrays.fill(raw_data, islice);  // add bricking logic here

            outStack.addSlice(new FloatProcessor(width, height, raw_data));
        }
        ImagePlus outPlus = new ImagePlus(
            "bricks_" + outStack.getWidth() + "_"
            + outStack.getHeight() + "_" + outStack.getSize(),
            outStack);
        outPlus.setDisplayRange(0, outPlus.getStack().getSize() -1);
        return outPlus;
    }
}
