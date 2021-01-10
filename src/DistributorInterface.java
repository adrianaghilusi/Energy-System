import java.util.List;

public interface DistributorInterface {
    /**
     * Calculeaza profitul pe baza costului de productie
     */
    double getProfit(Integer productionCost);

    /**
     * Calculeaza costul final al contractului pentru un consumator
     */
    double getContractFinalPrice(Integer infrastructureCost,
                                 Integer productionCost,
                                 List<Consumer> newlyAddedConsumers,
                                 int outThisRound);


}
