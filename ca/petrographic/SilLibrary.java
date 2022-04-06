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
}
