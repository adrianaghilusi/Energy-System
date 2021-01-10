import java.util.List;

public final class Input {
    private Integer numberOfTurns;
    private InputInitialData initialData;
    private List<InputMonthlyUpdates> monthlyUpdates;

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public void setNumberOfTurns(final int numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public InputInitialData getInitialData() {
        return initialData;
    }

    public void setInitialData(final InputInitialData initialData) {
        this.initialData = initialData;
    }

    public List<InputMonthlyUpdates> getMonthlyUpdates() {
        return monthlyUpdates;
    }

    public void setMonthlyUpdates(final List<InputMonthlyUpdates> monthlyUpdates) {
        this.monthlyUpdates = monthlyUpdates;
    }

    public Input() {
        this.initialData = null;
        this.monthlyUpdates = null;
    }
}
