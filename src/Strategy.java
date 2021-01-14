import java.util.List;

public interface Strategy {
    /**
     * Returneaza o lista cu producatorii potriviti strategiei cerute de un distribuitor
     */
    List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded,
                                 Distributor distributor);
}
