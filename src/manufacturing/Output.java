package manufacturing;

public class Output {
    ToyAirplaneManufacturing model;

    // SSOVs
    public int numSpitfireProducedDaily;
    public int numF16ProducedDaily;
    public int numConcordeProducedDaily;

    // for keeping track of number of casting created for each type of planes
    public int[] castingsCreated = new int[3];

    // Constructor
    protected Output(ToyAirplaneManufacturing model) {
        this.model = model;
    }
}
