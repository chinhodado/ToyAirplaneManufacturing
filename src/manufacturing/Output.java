package manufacturing;

public class Output {
    ToyAirplaneManufacturing model;

    // SSOVs
    public int numSpitfireProducedDaily;
    public int numF16ProducedDaily;
    public int numConcordeProducedDaily;

    // Constructor
    protected Output(ToyAirplaneManufacturing model) {
        this.model = model;
    }
}
