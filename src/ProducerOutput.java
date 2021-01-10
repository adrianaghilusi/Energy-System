import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import entities.EnergyType;

import java.util.List;

@JsonPropertyOrder({"id", "maxDistributors", "priceKW","energyType", "energyPerDistributor", "monthlyStats"})
public class ProducerOutput {
    Integer id;
    EnergyType energyType;
    Integer maxDistributors;
    Double priceKW;
    Integer energyPerDistributor;
    List<MonthlyStatsOutput> monthlyStats;

    public ProducerOutput(Integer id,Integer maxDistributors, Double priceKW,EnergyType energyType, Integer energyPerDistributor,   List<MonthlyStatsOutput> monthlyStats) {
        this.id = id;
        this.energyType = energyType;
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
        this.monthlyStats = monthlyStats;
    }

    public List<MonthlyStatsOutput> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(List<MonthlyStatsOutput> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public void setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
    }

    public Integer getMaxDistributors() {
        return maxDistributors;
    }

    public void setMaxDistributors(Integer maxDistributors) {
        this.maxDistributors = maxDistributors;
    }

    public Double getPriceKW() {
        return priceKW;
    }

    public void setPriceKW(Double priceKW) {
        this.priceKW = priceKW;
    }

    public Integer getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    public void setEnergyPerDistributor(Integer energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }
}
