import java.util.List;

public final class MonthlyStatsOutput {
    private Integer month;
    private List<Integer> distributorsIds;

    public MonthlyStatsOutput(Integer month, List<Integer> distributorIds) {
        this.month = month;
        this.distributorsIds = distributorIds;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<Integer> getDistributorsIds() {
        return distributorsIds;
    }

    public void setDistributorsIds(List<Integer> distributorIds) {
        this.distributorsIds = distributorIds;
    }
}
