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
        int width = bricks_wide * brick_width
            + (bricks_wide + 1) * margin_thickness;
        int height = bricks_high * brick_height
            + (bricks_high + 1) * margin_thickness;
        int depth = bricks_deep * brick_depth
            + (bricks_deep + 1) * margin_thickness;
        ImageStack outStack = new ImageStack(width, height);

        int width_period = brick_width + margin_thickness;
        int height_period = brick_height + margin_thickness;
        int depth_period = brick_depth + margin_thickness;

        for(int id = 1; id <= depth; id ++) {
            byte[] raw_bytes = new byte[width * height];

            // Bricks are White (0xFF); Margins are Black (0x0) [default];
            // add bricking logic here
            // start in a margin -- encase all bricks in the margin.

            // dial the pixel, and ask if it's a brick or a void --
            // should just be modular --
            // one dimension first -- width;

            for(int iw = 0; iw < width; iw ++) {
                for(int ih = 0; ih < height; ih ++) {
                    boolean plane_w =
                        iw % width_period > ;
                    if (iw % width_period > margin_thickness &&
                        ih % height_period > margin_thickness &&
                        id % depth_period > margin_thickness ||
                        plane_w
                    ) {
                        raw_bytes[ih * width + iw] = (byte)0xFF;
                    }
                }
            }

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
