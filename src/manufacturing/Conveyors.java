package manufacturing;

import java.util.ArrayList;

public class Conveyors {
    // Implement the queue using an ArrayList object
    // Size is initialised to 0
    private ArrayList<Component> conveyor = new ArrayList<Component>();
    // Lenght attribute (for M2 and M3 - these are parameters
    protected int length = -1; // Gives the maximum length of the conveyor. -1
                               // indicates infinite size

    // getters/setters and standard procedures
    protected int getN() {
        return (conveyor.size());
    }

    protected void spInsertQue(Component comp) {
        conveyor.add(comp);
    }

    protected Component spRemoveQue() {
        Component comp = Machines.NO_COMP;
        if (conveyor.size() != 0)
            comp = conveyor.remove(0);
        return (comp);
    }
}
