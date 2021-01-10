import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "isBankrupt", "budget"})
public final class ConsumerOutput {
    private Integer id;
    private Boolean isBankrupt;
    private Integer budget;

    public ConsumerOutput(final Integer id, final Boolean isBankrupt, final Integer budget) {
        this.id = id;
        this.isBankrupt = isBankrupt;
        this.budget = budget;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public boolean getIsBankrupt() {
        return isBankrupt;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(final Integer budget) {
        this.budget = budget;
    }
}
