import entities.EnergyType;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class Producer implements Observer {
    Integer id;
    EnergyType energyType;
    Integer maxDistributors;
    Double priceKW;
    Integer energyPerDistributor;
    Map<Integer, List<Distributor>> monthlyStats;
    long contractCost(){
        return (long) Math.floor(this.energyPerDistributor * this.priceKW);
    }

    public Map<Integer, List<Distributor>> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(Map<Integer, List<Distributor>> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public Producer() {

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

    public Producer(Integer id, EnergyType energyType, Integer maxDistributors, Double priceKW, Integer energyPerDistributor) {
        this.id = id;
        this.energyType = energyType;
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
    }

    @Override
    public void update(Observable o, Object energyChange) {
        this.setEnergyPerDistributor((Integer)energyChange);
    }
}
