package manufacturing;

import simulationModelling.ScheduledAction;

class CompArrivals extends ScheduledAction {
    Manufacturing model;

    protected CompArrivals(Manufacturing md) {
        // super(md);
        model = md;
    }

    @Override
    protected double timeSequence() {
        return model.rvp.duCArr();
    }

    @Override
    protected void actionEvent() {
        Component iCComponent = new Component();
        iCComponent.uType = model.rvp.uCompType();
        model.qConveyors[Constants.M1].spInsertQue(iCComponent);
    }
}
