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
            int brick_w, int brick_h, int brick_d,
            int bricks_wide, int bricks_high, int bricks_deep,
            int margin, int stick) {
        int width = bricks_wide * brick_w + (bricks_wide + 1) * margin;
        int height = bricks_high * brick_h + (bricks_high + 1) * margin;
        int depth = bricks_deep * brick_d + (bricks_deep + 1) * margin;
        ImageStack outStack = new ImageStack(width, height);

        int period_w = brick_w + margin;
        int period_h = brick_h + margin;
        int period_d = brick_d + margin;

        for(int id = 0; id < depth; id ++) {  // 0-index ok bc appending hehehe
            byte[] raw_bytes = new byte[width * height];

            /*  Bricks are White (0xFF); Margins are Black (0x0) [default];

            Plane Arith -- plane_w, plane_h, plane_d

            |-M-|         |--S--|         |-M-|  - [M]argin
            |   |         |     |         |   |  - [S]tick
            |   |---------|-----|---------|   |  - brick [W]idth
            |---|                         |---|  - [P]eriod
            |   |------------W------------|   |
            |                             |   |
            |--------------P--------------|   |
            |                             |   |  */

            for(int iw = 0; iw < width; iw ++) {
                for(int ih = 0; ih < height; ih ++) {
                    boolean plane_w =
                        (int)(period_w + iw - margin - brick_w/2. + stick/2.)
                        % period_w < stick;
                    boolean plane_h =
                        (int)(period_h + ih - margin - brick_h/2. + stick/2.)
                        % period_h < stick;
                    boolean plane_d =
                        (int)(period_d + id - margin - brick_d/2. + stick/2.)
                        % period_d < stick;
                    if (iw % period_w >= margin &&
                        ih % period_h >= margin &&
                        id % period_d >= margin ||
                        plane_w && plane_h ||
                        plane_w && plane_d ||
                        plane_h && plane_d
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
