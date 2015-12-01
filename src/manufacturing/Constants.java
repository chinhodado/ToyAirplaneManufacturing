package manufacturing;

import manufacturing.entity.Bin;

public class Constants {
    public static final int CAST = 0;
    public static final int CUT_GRIND = 1;
    public static final int COAT = 2;
    public static final int INSPECT_PACK = 3;

    public static final int F16 = 0;
    public static final int CONCORDE = 1;
    public static final int SPITFIRE = 2;

    // number of castings needed for F16, Concorde and Spitfire
    public static final int[] NUM_CASTING_NEEDED = new int[] { 325, 390, 217 };

    public static final int IN = 0;
    public static final int OUT = 1;

    public static final int NONE = -1;
    public static final Bin NO_BIN = null;
}
