import java.util.List;

public final class InputInitialData {
    private List<ConsumerInput> consumers;
    private List<DistributorInput> distributors;
    private List<ProducerInput> producers;

    public List<ProducerInput> getProducers() {
        return producers;
    }

    public void setProducers(List<ProducerInput> producers) {
        this.producers = producers;
    }

    public List<ConsumerInput> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<ConsumerInput> consumers) {
        this.consumers = consumers;
    }

    public List<DistributorInput> getDistributors() {
        return distributors;
    }

    public void setDistributors(final List<DistributorInput> distributors) {
        this.distributors = distributors;
    }
}

