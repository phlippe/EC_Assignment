package island;

public class IslandParams {

    public enum ExchangeType{
        BEST, // Best individual of an island
        RANDOM, // Pick random individual of an island
        MULTI_CULTI // Pick individual that differs the most from the other island
    }

    public enum TopologyType{
        RING,
        COMPLETE
    }

    private int epoch_size;
    private int no_individual_exchange;
    private ExchangeType exchangeType = ExchangeType.RANDOM;
    private TopologyType topologyType = TopologyType.RING;
    private String name;


    public IslandParams(int epoch_size, int no_individual_exchange){
        setEpochSize(epoch_size);
        setNoIndividualExchange(no_individual_exchange);
        setName("");
    }

    public int getEpochSize() {
        return epoch_size;
    }

    public void setEpochSize(int epoch_size) {
        this.epoch_size = epoch_size;
    }

    public int getNoIndividualExchange() {
        return no_individual_exchange;
    }

    public void setNoIndividualExchange(int no_individual_exchange) {
        this.no_individual_exchange = no_individual_exchange;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public TopologyType getTopologyType() {
        return topologyType;
    }

    public void setTopologyType(TopologyType topologyType) {
        this.topologyType = topologyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
