import java.util.Observable;
import java.util.Observer;

public class SimulationSystem extends Observable {
    private Integer energyChange;

    public void setEnergyChange(Integer energyChange) {
        this.energyChange = energyChange;
        setChanged();
        notifyObservers(energyChange);
    }
}
