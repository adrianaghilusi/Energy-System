import strategies.EnergyChoiceStrategyType;

import java.util.ArrayList;
import java.util.List;

public class Distributor implements DistributorInterface {
    private Integer id;
    private Integer contractLength;
    private Integer initialBudget;
    private Integer initialInfrastructureCost;
    private Integer initialProductionCost;
    private List<Consumer> contracts = new ArrayList<>();
    boolean isBankrupt = false;
    private Integer energyNeededKW;
    private EnergyChoiceStrategyType producerStrategy;
    private List<Producer> chosenProducers;
    private Integer lastContractPrice;


    public Distributor(Integer id, Integer contractLength, Integer initialBudget,
                       Integer initialInfrastructureCost, Integer energyNeededKW, EnergyChoiceStrategyType producerStrategy) {
        this.id = id;
        this.contractLength = contractLength;
        this.initialBudget = initialBudget;
        this.initialInfrastructureCost = initialInfrastructureCost;
        this.energyNeededKW = energyNeededKW;
        this.producerStrategy = producerStrategy;
    }

    public Distributor() {

    }

    public final Integer getLastContractPrice() {
        return lastContractPrice;
    }

    public final void setLastContractPrice(Integer lastContractPrice) {
        this.lastContractPrice = lastContractPrice;
    }

    public final List<Producer> getChosenProducers() {
        return chosenProducers;
    }

    public final void setChosenProducers(List<Producer> chosenProducers) {
        this.chosenProducers = chosenProducers;
    }

    public final EnergyChoiceStrategyType getProducerStrategy() {
        return producerStrategy;
    }

    public final void setProducerStrategy(EnergyChoiceStrategyType producerStrategy) {
        this.producerStrategy = producerStrategy;
    }

    public final Integer getEnergyNeededKW() {
        return energyNeededKW;
    }

    public final void setEnergyNeededKW(Integer energyNeededKW) {
        this.energyNeededKW = energyNeededKW;
    }

    public final List<Consumer> getContracts() {
        return contracts;
    }

    public final void setContracts(final List<Consumer> contracts) {
        this.contracts = contracts;
    }

    public final boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * Calculeaza profitul pe baza costului de productie
     */
    public final double getProfit(final Integer productionCost) {
        return Math.round(Math.floor(0.2 * productionCost));
    }

    /**
     * Calculeaza costul final al contractului pentru un consumator
     */
    public final double getContractFinalPrice(final Integer infrastructureCost,
                                              final Integer productionCost,
                                              final List<Consumer> newlyAddedConsumers,
                                              final int outThisRound) {
        List<Consumer> existingContracts = new ArrayList<>();
        for (var consumer : this.contracts) {
            boolean ok = true;
            for (var justAdded : newlyAddedConsumers) {
                if (consumer.getId().equals(justAdded.getId())) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                existingContracts.add(consumer);
            }
        }
        if (existingContracts.size() != 0) {
            return Math
                    .round(Math.floor((infrastructureCost / (existingContracts.size()
                            + outThisRound) + productionCost + getProfit(productionCost))));
        } else {
            //nu detine consumatori
            return infrastructureCost + productionCost + getProfit(productionCost);
        }
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(final Integer id) {
        this.id = id;
    }

    public final Integer getContractLength() {
        return contractLength;
    }

    public final void setContractLength(final Integer contractLength) {
        this.contractLength = contractLength;
    }

    public final Integer getInitialBudget() {
        return initialBudget;
    }

    public final void setInitialBudget(final Integer initialBudget) {
        this.initialBudget = initialBudget;
    }

    public final Integer getInitialInfrastructureCost() {
        return initialInfrastructureCost;
    }

    public final void setInitialInfrastructureCost(final Integer initialInfrastructureCost) {
        this.initialInfrastructureCost = initialInfrastructureCost;
    }

    public final Integer getInitialProductionCost() {
        return initialProductionCost;
    }

    public final void setInitialProductionCost(final Integer initialProductionCost) {
        this.initialProductionCost = initialProductionCost;
    }

    /**
     * Calculeaza cea mai buna alegere de distribuitor pentru un consumator
     */
    public static Distributor getBestChoice(final List<Distributor> distributorList,
                                            final int outThisRound) {
        List<Distributor> inGameDistributors = new ArrayList<>();
        for (var distributor : distributorList) {
            if (!distributor.isBankrupt()) {
                inGameDistributors.add(distributor);
            }
        }
        if (inGameDistributors.isEmpty()) {
            return null;
        }
        double lowestContractPrice = inGameDistributors.get(0).
                getContractFinalPrice(inGameDistributors.get(0).getInitialInfrastructureCost(),
                        inGameDistributors.get(0).getInitialProductionCost(), new ArrayList<>(),
                        outThisRound);
        Distributor chosenDistributor = inGameDistributors.get(0);
        for (var inGameDistributor : inGameDistributors) {
            double contractPrice = inGameDistributor.
                    getContractFinalPrice(inGameDistributor.getInitialInfrastructureCost(),
                            inGameDistributor.getInitialProductionCost(), new ArrayList<>(),
                            outThisRound);

            if (contractPrice < lowestContractPrice) {
                lowestContractPrice = contractPrice;
                chosenDistributor = inGameDistributor;
            }
        }
        return chosenDistributor;
    }

}
