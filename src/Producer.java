import entities.EnergyType;

import java.util.*;

public class Producer implements Observer {
    private Integer id;
    private EnergyType energyType;
    private Integer maxDistributors;
    private Double priceKW;
    private Integer energyPerDistributor;
    private Map<Integer, List<Distributor>> monthlyStats;
    private Integer currentDistributors;
    private List<Distributor> currentDistributorsList;

    public final Integer getCurrentDistributors() {
        return currentDistributors;
    }

    public final void setCurrentDistributors(Integer currentDistributors) {
        this.currentDistributors = currentDistributors;
    }

    /**
     * Calculeaza pretul conractului
     */
    long contractCost() {
        return (long) Math.floor(this.energyPerDistributor * this.priceKW);
    }

    public final Map<Integer, List<Distributor>> getMonthlyStats() {
        return monthlyStats;
    }

    public final void setMonthlyStats(Map<Integer, List<Distributor>> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public Producer() {

    }

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final EnergyType getEnergyType() {
        return energyType;
    }

    public final void setEnergyType(EnergyType energyType) {
        this.energyType = energyType;
    }

    public final Integer getMaxDistributors() {
        return maxDistributors;
    }

    public final void setMaxDistributors(Integer maxDistributors) {
        this.maxDistributors = maxDistributors;
    }

    public final Double getPriceKW() {
        return priceKW;
    }

    public final void setPriceKW(Double priceKW) {
        this.priceKW = priceKW;
    }

    public final Integer getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    public final void setEnergyPerDistributor(Integer energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }

    public Producer(Integer id, EnergyType energyType, Integer maxDistributors, Double priceKW,
                    Integer energyPerDistributor) {
        this.id = id;
        this.energyType = energyType;
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
    }

    @Override
    /**
     * Producatorul este notificat cu privire la o schimbare in cantitatea de energie oferita lunar
     */
    public void update(Observable o, Object energyChange) {
        this.setEnergyPerDistributor((Integer) energyChange);
    }

    public final List<Distributor> getCurrentDistributorsList() {
        return currentDistributorsList;
    }

    public final void setCurrentDistributorsList(List<Distributor> currentDistributorsList) {
        this.currentDistributorsList = currentDistributorsList;
    }
}
