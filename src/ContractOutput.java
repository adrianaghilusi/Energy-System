public final class ContractOutput {
    private Integer consumerId;
    private Integer price;
    private Integer remainedContractMonths;

    public Integer getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(final Integer consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }

    public Integer getRemainedContractMonths() {
        return remainedContractMonths;
    }

    public void setRemainedContractMonths(final Integer remainedContractMonths) {
        this.remainedContractMonths = remainedContractMonths;
    }

    public ContractOutput(final Integer consumerId, final Integer price,
                          final Integer remainedContractMonths) {
        this.consumerId = consumerId;
        this.price = price;
        this.remainedContractMonths = remainedContractMonths;
    }
}
