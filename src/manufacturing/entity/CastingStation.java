package manufacturing.entity;

import manufacturing.Constants;

public class CastingStation {
    // not really needed to initialize stuffs manually, but let's be explicit
    public boolean busy = false;
    public Bin bin = Constants.NO_BIN;
    public double timeToFailure = 0;
    public double castingTimeLeft = 0;
    public boolean isSuspended = false;
    public int planeType = Constants.NONE;

    public CastingStation(int planeType) {
        this.planeType = planeType;
    }
}
