package manufacturing;

public class UDPs {
    Manufacturing model;

    // Constructor
    protected UDPs(Manufacturing md) {
        model = md;
    }

    // User Defined Procedures

    // Conveyor M2 or M3 is ready to receive the component in Machine M1
    protected int conveyorReadyForComp() {
        int convId = Constants.NONE;
        // Check all conveyors
        if (!model.rMachines[Constants.M1].busy && model.rMachines[Constants.M1].component != Machines.NO_COMP) {
            // Check conveyor to Machine M2
            if (model.rMachines[Constants.M1].component.uType == Component.CompType.A
                    && model.qConveyors[Constants.M2].getN() < model.qConveyors[Constants.M2].length)
                convId = Constants.M2;
            // Check Conveyor to Machine M3
            if (model.rMachines[Constants.M1].component.uType == Component.CompType.B
                    && model.qConveyors[Constants.M3].getN() < model.qConveyors[Constants.M3].length)
                convId = Constants.M3;
        }
        return (convId);
    }

    // One of the three machines is ready for processing
    protected int machineReadyForProcessing() {
        int machineId = Constants.NONE;
        // Check all three machines
        for (int id = Constants.M1; id <= Constants.M3 && machineId == Constants.NONE; id++) {
            if (!model.rMachines[id].busy && model.rMachines[id].component == Machines.NO_COMP
                    && model.qConveyors[id].getN() != 0)
                machineId = id;
        }
        return (machineId);
    }
}
