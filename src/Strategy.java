import java.util.List;

public interface Strategy {
    public List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded, Distributor distributor);
}
