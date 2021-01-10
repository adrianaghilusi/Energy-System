public final class DistributorFactory {
    private static DistributorFactory distributorFactory = null;

    /**
     * Vom folosi metoda pentru pattern-ul Singleton
     */
    public static DistributorFactory getInstance() {
        if (distributorFactory == null) {
            distributorFactory = new DistributorFactory();
        }
        return distributorFactory;
    }

    public Distributor getDistributor() {
        //momentan avem un singur tip de distribuitor
        return new Distributor();
    }
}
