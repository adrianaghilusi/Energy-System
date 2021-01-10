import java.util.List;

public final class InputMonthlyUpdates {
    private List<ConsumerInput> newConsumers;
    private List<InputDistributorChanges> distributorChanges;
    private List<InputProducerChanges> producerChanges;

    public List<ConsumerInput> getNewConsumers() {
        return newConsumers;
    }

    public void setNewConsumers(final List<ConsumerInput> newConsumers) {
        this.newConsumers = newConsumers;
    }

    public List<InputDistributorChanges> getDistributorChanges() {
        return distributorChanges;
    }

    public void setDistributorChanges(List<InputDistributorChanges> distributorChanges) {
        this.distributorChanges = distributorChanges;
    }

    public List<InputProducerChanges> getProducerChanges() {
        return producerChanges;
    }

    public void setProducerChanges(List<InputProducerChanges> producerChanges) {
        this.producerChanges = producerChanges;
    }
}
