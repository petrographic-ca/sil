package ca.petrographic;

import java.util.Arrays;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
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
            int brick_width, int brick_height, int brick_depth,
            int bricks_wide, int bricks_high, int bricks_deep,
            int margin_thickness, int stick_thickness) {
        int width =
            (bricks_wide * brick_width)
            + ((bricks_wide + 1) * margin_thickness);
        int height =
            (bricks_high * brick_height)
            + ((bricks_high + 1) * margin_thickness);
        int depth =
            (bricks_deep * brick_depth)
            + ((bricks_deep + 1) * margin_thickness);
        ImageStack outStack = new ImageStack(width, height);
        for(int islice = 1; islice <= depth; islice ++) {
            byte[] raw_bytes = new byte[width * height];

            // Bricks are White (255); Margins are Black (0) [default];
            // add bricking logic here
            // start in a margin -- encase all bricks in the margin.

            outStack.addSlice(new ByteProcessor(width, height, raw_bytes));
        }
        ImagePlus outPlus = new ImagePlus(
            "bricks_" + outStack.getWidth() + "_"
            + outStack.getHeight() + "_" + outStack.getSize(),
            outStack);
        outPlus.setDisplayRange(0, 255);
        return outPlus;
    }
}
