package manufacturing.entity;

import manufacturing.Constants;

public class Station {
    // not really needed to initialize stuffs manually, but let's be explicit
    public boolean busy = false;
    public Bin bin = Constants.NO_BIN;
}
