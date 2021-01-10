import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder(alphabetic = true)
public class Consumer implements ConsumerInterface {
    //distribuitorul caruia trebuie sa ii plateasca restanta
    private static Distributor failedPaymentTo;
    private static Integer failedPayment;
    private Integer id;
    private Integer initialBudget;
    private Integer monthlyIncome;
    //distribuitorul de care apartine in mod curent
    private Distributor chosenDistributor;
    //plata anterioara neefectuata
    private boolean unpaidFee = false;
    //capacitatea de a ramane in joc in continuare
    private boolean outOfGame = false;
    //pretul contractului curent
    private double contractPrice;
    //numarul de luni ramas pana la expirarea contractului
    private Integer contractualTimeLeft = 0;

    public Consumer(final Integer id, final Integer initialBudget, final Integer monthlyIncome) {
        this.id = id;
        this.initialBudget = initialBudget;
        this.monthlyIncome = monthlyIncome;
    }

    public Consumer() {
    }

    public final Distributor getFailedPaymentTo() {
        return failedPaymentTo;
    }

    public final void setFailedPaymentTo(final Distributor failedPaymentTo) {
        Consumer.failedPaymentTo = failedPaymentTo;
    }

    public static Integer getFailedPayment() {
        return failedPayment;
    }

    public final void setFailedPayment(final Integer failedPayment) {
        Consumer.failedPayment = failedPayment;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Consumer)) {
            return false;
        }
        Consumer consumer = (Consumer) o;
        return id.equals(consumer.id);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    public final Integer getContractualTimeLeft() {
        return contractualTimeLeft;
    }

    public final void setContractualTimeLeft(final Integer contractualTimeLeft) {
        this.contractualTimeLeft = contractualTimeLeft;
    }

    public final double getContractPrice() {
        return contractPrice;
    }

    public final void setContractPrice(final double contractPrice) {
        this.contractPrice = contractPrice;
    }

    public final boolean isOutOfGame() {
        return outOfGame;
    }

    public final void setOutOfGame(final boolean outOfGame) {
        this.outOfGame = outOfGame;
    }

    public final boolean isUnpaidFee() {
        return unpaidFee;
    }

    public final void setUnpaidFee(final boolean unpaidFee) {
        this.unpaidFee = unpaidFee;
    }

    public final Distributor getChosenDistributor() {
        return chosenDistributor;
    }

    public final void setChosenDistributor(final Distributor chosenDistributor) {
        this.chosenDistributor = chosenDistributor;
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(final Integer id) {
        this.id = id;
    }

    public final Integer getInitialBudget() {
        return initialBudget;
    }

    public final void setInitialBudget(final Integer initialBudget) {
        this.initialBudget = initialBudget;
    }

    public final Integer getMonthlyIncome() {
        return monthlyIncome;
    }

    public final void setMonthlyIncome(final Integer monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public final void payContract(final Consumer consumer) {
        consumer.setInitialBudget((int) (consumer.getInitialBudget()
                - consumer.getContractPrice()));
    }

    public final void getSalary(final Consumer consumer) {
        consumer.setInitialBudget(consumer.getInitialBudget()
                + consumer.getMonthlyIncome());
    }
}
