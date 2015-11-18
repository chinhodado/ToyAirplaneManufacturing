package manufacturing;

import simulationModelling.Activity;

class CompProcessing extends Activity {
    Manufacturing model;
    int id; // identifers for R.Machines and Q.Conveyors

    protected CompProcessing(Manufacturing md) {
        model = md;
    }

    public static boolean precondition(Manufacturing md) {
        boolean retVal = false;
        if (md.udp.machineReadyForProcessing() != Constants.NONE)
            retVal = true;
        return (retVal);
    }

    @Override
    public void startingEvent() {
        id = model.udp.machineReadyForProcessing(); // identify the machine that
                                                    // processes the part
        // Place Identifier in name of behaviour object for logging, used by
        // showSbl()
        this.name = "M" + (id + 1);
        // Set machine to busy and move component from queue to machine
        model.rMachines[id].busy = true;
        model.rMachines[id].component = model.qConveyors[id].spRemoveQue();
    }

    @Override
    public double duration() {
        // determine the duration
        return model.rvp.uProcTime(id, model.rMachines[id].component.uType);
    }

    @Override
    public void terminatingEvent() {
        model.rMachines[id].busy = false;
        if (id != Constants.M1) {
            model.rMachines[id].component = Machines.NO_COMP;
            // No need to implement SP.Leave - Java has a garbage collector
        }
    }
}
