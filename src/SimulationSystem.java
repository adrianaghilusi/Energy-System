import java.util.Observable;

public class SimulationSystem extends Observable {
    private Integer energyChange;

    /**
     * Metoda urmareste update-ul de energie lunara al unui producator si notifica producatorul
     * cu privire la aceasta schimbare
     */
    public void setEnergyChange(Integer energyChange) {
        this.energyChange = energyChange;
        setChanged();
        notifyObservers(energyChange);
    }
}
