package manufacturing;

import simulationModelling.ConditionalAction;

class MoveCOutOfM1 extends ConditionalAction {
    Manufacturing model;

    public MoveCOutOfM1(Manufacturing md) {
        this.model = md;
    }

    public static boolean precondition(Manufacturing md) {
        boolean retVal = false;
        if (md.udp.conveyorReadyForComp() != Constants.NONE)
            retVal = true;
        return (retVal);
    }

    @Override
    public void actionEvent() {
        int qid = model.udp.conveyorReadyForComp();
        model.qConveyors[qid].spInsertQue(model.rMachines[Constants.M1].component);
        model.rMachines[Constants.M1].component = Machines.NO_COMP;
    }
}
