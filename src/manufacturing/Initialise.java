package manufacturing;

import simulationModelling.ScheduledAction;

class Initialise extends ScheduledAction {
    Manufacturing model;

    // Constructor
    public Initialise(Manufacturing model) {
        this.model = model;
    }

    double[] ts = { 0.0, -1.0 }; // -1.0 ends scheduling
    int tsix = 0; // set index to first entry.

    @Override
    public double timeSequence() {
        return ts[tsix++]; // only invoked at t=0
    }

    @Override
    public void actionEvent() {
        int id; // Machine/Conveyor identifiers
        for (id = Constants.M1; id <= Constants.M3; id++) {
            model.rMachines[id] = new Machines(); // Creates the object/entity
            model.rMachines[id].busy = false;
            model.rMachines[id].component = Machines.NO_COMP;
            // qConveyor array created in constructor to initialise parameters
        }
    }
}
