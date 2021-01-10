import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import strategies.EnergyChoiceStrategyType;

import java.util.List;

@JsonPropertyOrder({"id", "energyNeededKW", "contractCost", "budget","producerStrategy", "isBankrupt", "contracts"})
public final class DistributorOutput {
    private Integer id;
    private Integer budget;
    private Boolean isBankrupt;
    private Integer energyNeededKW;
    private EnergyChoiceStrategyType producerStrategy;
    private List<ContractOutput> contracts;
    private Integer contractCost;

    public DistributorOutput(final Integer id, final Integer budget, final Boolean isBankrupt,
                             final List<ContractOutput> contracts, int energyNeededKW, EnergyChoiceStrategyType producerStrategy, Integer contractCost) {
        this.id = id;
        this.budget = budget;
        this.isBankrupt = isBankrupt;
        this.contracts = contracts;
        this.energyNeededKW = energyNeededKW;
        this.producerStrategy = producerStrategy;
        this.contractCost = contractCost;
    }

    public Integer getEnergyNeededKW() {
        return energyNeededKW;
    }

    public void setEnergyNeededKW(Integer energyNeededKW) {
        this.energyNeededKW = energyNeededKW;
    }

    public EnergyChoiceStrategyType getProducerStrategy() {
        return producerStrategy;
    }

    public void setProducerStrategy(EnergyChoiceStrategyType producerStrategy) {
        this.producerStrategy = producerStrategy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(final Integer budget) {
        this.budget = budget;
    }

    public boolean getIsBankrupt() {
        return isBankrupt;
    }

    public void setIsBankrupt(final Boolean isBankrupt) {
        this.isBankrupt = isBankrupt;
    }

    public List<ContractOutput> getContracts() {
        return contracts;
    }

    public void setContracts(final List<ContractOutput> contracts) {
        this.contracts = contracts;
    }

    public int getContractCost() {
        return contractCost;
    }

    public void setContractCost(int contractCost) {
        this.contractCost = contractCost;
    }
}
