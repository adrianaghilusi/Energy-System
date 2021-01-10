public final class ConsumerFactory {
    /**
     * Vom folosi metoda pentru pattern-ul Singleton
     */
    private static ConsumerFactory consumerFactory = null;

    public static ConsumerFactory getInstance() {
        if (consumerFactory == null) {
            consumerFactory = new ConsumerFactory();
        }
        return consumerFactory;
    }

    /**
     * Consumatorii ce apar pe parcurs au potentialul de a fi de tipuri diferite in etapa viitoare
     */
    public Consumer getConsumer(final ConsumerEnum consumerType) {
        //momentan avem un singur tip de consumator, Basic
        switch (consumerType) {
            case Household -> {
                return new HouseholdConsumer();
            }
            case Industrial -> {
                return new IndustrialConsumer();
            }
            case Basic -> {
                return new Consumer();
            }
            default -> {
                return new Consumer();
            }
        }
    }
}
