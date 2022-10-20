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

    static private int query_shear_cycle_coord(
            int target_a, int target_b, int target_c,
            int a_max, double shear_ab, double shear_ac) {
        int source_a = (int)(
            target_a + (target_b * shear_ab) + (target_c * shear_ac));
        source_a = ((((source_a/a_max) +1) * a_max) + source_a) % a_max;
        return source_a;
    }

    static private int[] query_shear_cycle_source_coords(
            int target_x, int target_y, int target_z,
            int width, int height, int depth,
            double shear_xy, double shear_yx,
            double shear_xz, double shear_zx,
            double shear_yz, double shear_zy) {
        /* Shear +xy --

        eg applying source -> target w m = +1 ==

        ABCDEF <- ABCDEF
        ABCDEF <- BCDEFA
        ABCDEF <- CDEFAB

        source <- target
        */

        int x = target_x;
        int y = target_y;
        int z = target_z;

        x = query_shear_cycle_coord(x, y, z, width, shear_xy, shear_xz);
        y = query_shear_cycle_coord(y, x, z, height, shear_yx, shear_yz);
        z = query_shear_cycle_coord(z, x, y, depth, shear_zx, shear_zy);

        return new int[]{x, y, z};
    }

    static public ImagePlus makeShearCycle(
            ImagePlus source,
            double shear_xy, double shear_yx,
            double shear_xz, double shear_zx,
            double shear_yz, double shear_zy) {
        /* Shear [m] is just Delta[X] / Delta[Y] -- stated opposite of Slope.

        +-----+     +-----+         +-----+   +-----+
        |     |    /     /        /     /     |     |
        |  .  |   /  .  /       /  .  /      /  .  /
        |     |  /     /      /     /       |     |
        +-----+ +-----+     +-----+         +-----+
         m = 0   m = 1       m = 2          m = 1/2  (approx ... prettied lol)

        Anyway, let's define this in an image processing orientation, since
        we're going to get funky floating-point-values as parameters --

        To do so, we define the inverse-function, so at each destination pixel,
        we query that coordinate, and ask where-from it came
        in the origin image.

        As well, to keep things bounded, we'll use circular permutation
        (modulus math, "cycle") so we can keep the volumes the same size;
        later, we'll change this but not now.

        We can use regular floor(R +.5) to get the nearest pixel,
        and can worry about anti-aliasing instead later.
        */
        int width = source.getStack().getWidth();
        int height = source.getStack().getHeight();
        int depth = source.getStack().getSize();

        ImageStack outStack = new ImageStack(width, height);

        for(int z = 0; z < depth; z ++) {
            int[] raw_data = new int[width * height];
            for(int x = 0; x < width; x ++) {
                for(int y = 0; y < height; y ++) {
                    int[] src_coords = query_shear_cycle_source_coords(
                        x, y, z,
                        width, height, depth,
                        shear_xy, shear_yx,
                        shear_xz, shear_zx,
                        shear_yz, shear_zy);
                    int sx = src_coords[0];
                    int sy = src_coords[1];
                    int sz = src_coords[2];
                    raw_data[y*width+x] =
                        (int)source.getStack().getVoxel(sx, sy, sz);
                }
            }
            outStack.addSlice(new FloatProcessor(width, height, raw_data));
        }

        return new ImagePlus(
            source.getTitle() + "_shear_cycle",
            outStack);
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
