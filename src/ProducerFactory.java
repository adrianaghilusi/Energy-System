public class ProducerFactory {
    private static ProducerFactory producerFactory = null;
    public static ProducerFactory getInstance() {
        if (producerFactory == null) {
            producerFactory = new ProducerFactory();
        }
        return producerFactory;
    }

    public Producer getProducer() {
        //momentan avem un singur tip de distribuitor
        return new Producer();
    }
}
