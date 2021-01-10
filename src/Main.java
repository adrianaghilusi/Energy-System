import java.util.*;
import java.util.stream.Collectors;

class Main {

    public static void main(final String[] args) throws Exception {
        String test = "C:\\Users\\user\\Desktop\\teme-proiect-etapa2-2020\\teme\\proiect-etapa2-energy-system\\checker\\resources\\in\\basic_9.json";
        InputLoader loader = new InputLoader(args[0]);
        var inputData = loader.readData();
        InputSingleton inputDataSingleton = InputSingleton.getInstance();
        inputDataSingleton.setInitialData(inputData.getInitialData());
        inputDataSingleton.setMonthlyUpdates(inputData.getMonthlyUpdates());
        inputDataSingleton.setNumberOfTurns(inputData.getNumberOfTurns());
        List<ConsumerInput> consumerListInput = inputDataSingleton.getInitialData().getConsumers();
        List<Consumer> consumerList = new ArrayList<>();
        List<Producer> producerList = new ArrayList<>();
        ConsumerFactory consumerFactory = ConsumerFactory.getInstance();
        for (var consumer : consumerListInput) {
            var consumerToAdd = consumerFactory
                    .getConsumer(ConsumerEnum.valueOf("Basic"));
            consumerToAdd = new Consumer(consumer.getId(), consumer
                    .getInitialBudget(), consumer.getMonthlyIncome());
            consumerList.add(consumerToAdd);
        }
        ProducerFactory producerFactory = ProducerFactory.getInstance();
        List<ProducerInput> producerInputList = inputDataSingleton.getInitialData().getProducers();
        for (var producer : producerInputList) {
            var producerToAdd = producerFactory.getProducer();
            producerToAdd = new Producer(producer.getId(), producer.getEnergyType(), producer.getMaxDistributors(), producer.getPriceKW(), producer.getEnergyPerDistributor());
            producerList.add(producerToAdd);
        }
        DistributorFactory distributorFactory = DistributorFactory.getInstance();
        List<DistributorInput> distributorListInput = inputDataSingleton.getInitialData()
                .getDistributors();
        List<Distributor> distributorList = new ArrayList<>();
        for (var distributor : distributorListInput) {
            var distributorToAdd = distributorFactory.getDistributor();
            distributorToAdd = new Distributor(distributor.getId(), distributor.getContractLength(),
                    distributor.getInitialBudget(), distributor.getInitialInfrastructureCost(), distributor.getEnergyNeededKW(), distributor.getProducerStrategy());
            var strategy = distributor.getProducerStrategy();
            Strategy selectedStrategy;
            switch (strategy) {
                case QUANTITY:
                    selectedStrategy = new QuantityStrategy();
                    break;
                case GREEN:
                    selectedStrategy = new GreenStrategy();
                    break;
                case PRICE:
                    selectedStrategy = new PriceStrategy();
                    break;
                default:
                    selectedStrategy = null;
            }

            distributorToAdd.setChosenProducers(selectedStrategy.applyStrategy(producerList, distributor.getEnergyNeededKW()));
            long cost = 0 ;
            for(var prod : distributorToAdd.getChosenProducers()){
                cost = cost + prod.contractCost();
            }
            distributorToAdd.setInitialProductionCost(Math.round(cost/10));
            distributorList.add(distributorToAdd);
        }
        double paymentConstant = 1.2;
        //variabila gameOver urmareste daca toti distribuitorii au dat faliment, caz in care
        //se termina jocul
        boolean gameOver = false;
        List<InputMonthlyUpdates> updates = inputDataSingleton.getMonthlyUpdates();
        //parcurgen nrOfTurns+1 runde
        for (int month = 0; month <= inputDataSingleton.getNumberOfTurns(); month++) {
            //lista retine consumatorii ce parasesc contractul in luna curenta
            List<Consumer> outThisRound = new ArrayList<>();
            //lista retine consumatorii adaugati in aceasta runda
            List<Consumer> newlyAddedConsumers = new ArrayList<>();
            //tratam cazul rundei initiale separat
            if (month == 0) {
                for (var consumer : consumerList) {
                    //consumatorii primesc salariu si isi aleg un distribuitor
                    consumer.getSalary(consumer);
                    newlyAddedConsumers.add(consumer);
                    Distributor bestChoice = getBestChoice(distributorList, consumer,
                            newlyAddedConsumers);
                    //distribuitorul primeste pretul pe contract, daca nu ne aflam in cazul
                    //unei restante de plata
                    if (!consumer.isUnpaidFee()) {
                        bestChoice.setInitialBudget((int) (bestChoice.getInitialBudget()
                                + consumer.getContractPrice()));
                    }
                }
                //scadem cheltuielile lunare ale distribuitorilor
                for (var distributor : distributorList) {
                    distributor.setInitialBudget(distributor.getInitialBudget()
                            - distributor.getInitialInfrastructureCost());
                    if (!distributor.getContracts().isEmpty()) {
                        distributor.setInitialBudget(distributor.getInitialBudget()
                                - distributor.getInitialProductionCost()
                                * distributor.getContracts().size());
                    }
                }
            } else {
                // runda incepe prin stabilirea noilor preturi de catre distribuitori
                //si prin adaugarea in joc a noilor consumatori
                List<InputDistributorChanges> monthlyDistributorChanges = updates.get(month - 1)
                        .getDistributorChanges();
                List<InputProducerChanges> monthlyProducerChanges = updates.get(month - 1).getProducerChanges();
                List<ConsumerInput> newConsumers = updates.get(month - 1).getNewConsumers();

                for (var costChange : monthlyDistributorChanges) {
                    Integer whichDistributor = costChange.getId();
                    Distributor thisDistributor = distributorList.stream()
                            .filter(distributor -> whichDistributor.equals(distributor.getId()))
                            .findAny()
                            .orElse(null);
                    thisDistributor.setInitialInfrastructureCost(costChange
                            .getInfrastructureCost());

                }
                for (var costChange : monthlyProducerChanges) {
                    Integer whichProducer = costChange.getId();
                    Producer thisProducer = producerList.stream()
                            .filter(producer -> whichProducer.equals(producer.getId()))
                            .findAny()
                            .orElse(null);
                    thisProducer.setEnergyPerDistributor(costChange
                            .getEnergyPerDistributor());

                }
                for (var newConsumer : newConsumers) {
                    var consumerToAdd = consumerFactory
                            .getConsumer(ConsumerEnum.valueOf("Basic"));
                    consumerToAdd = new Consumer(newConsumer.getId(), newConsumer
                            .getInitialBudget(), newConsumer.getMonthlyIncome());
                    consumerToAdd.setContractualTimeLeft(0);
                    consumerList.add(consumerToAdd);
                }
                //consumatorii primesc salariu si platesc rata contractului ales
                for (var consumer : consumerList) {
                    if (consumer.isOutOfGame()) {
                        continue;
                    }
                    consumer.setInitialBudget(consumer.getInitialBudget()
                            + consumer.getMonthlyIncome());
                    //consumatorul plateste pretul contractului, daca are posibilitatea
                    if (!consumer.isUnpaidFee()
                            && consumer.getInitialBudget() >= consumer.getContractPrice()) {
                        if (consumer.getContractualTimeLeft() != 0
                                || month == inputDataSingleton.getNumberOfTurns() + 1) {
                            consumer.payContract(consumer);
                        }

                    } else {
                        //consumatorul se afla in cazul unei restante de plata
                        if (!consumer.isUnpaidFee()) {
                            consumer.setUnpaidFee(true);
                            consumer.setFailedPayment((int) consumer.getContractPrice());
                            consumer.setFailedPaymentTo(consumer.getChosenDistributor());
                            //verificam ca acest consumator sa nu aiba un contract expirat,
                            //caz in care alegem altul
                            if (consumer.getContractualTimeLeft() != 0) {
                                consumer.setContractualTimeLeft(consumer
                                        .getContractualTimeLeft() - 1);
                            } else {
                                var oldDistributor = consumer.getChosenDistributor();
                                //cautam cea mai buna varianta de contract pentru consumator
                                var bestChoice = Distributor
                                        .getBestChoice(distributorList, 0);
                                if (bestChoice == null) {
                                    gameOver = true;
                                    break;
                                }
                                if (oldDistributor == null) {
                                    bestChoice.getContracts().add(consumer);
                                    newlyAddedConsumers.add(consumer);
                                } else if (!bestChoice.getId().equals(oldDistributor.getId())) {
                                    oldDistributor.getContracts().remove(consumer);
                                    bestChoice.getContracts().add(consumer);
                                    newlyAddedConsumers.add(consumer);
                                }
                                consumer.setContractPrice(bestChoice
                                        .getContractFinalPrice(bestChoice
                                                        .getInitialInfrastructureCost(),
                                                bestChoice.getInitialProductionCost(),
                                                newlyAddedConsumers, 0));
                                consumer.setChosenDistributor(bestChoice);
                                consumer.setContractualTimeLeft(bestChoice.getContractLength() - 1);
                            }
                            continue;
                        }
                    }
                    //consumatorul a intrat in aceasta runda cu o restanta pe care verificam
                    //daca isi permite sa o achite impreuna cu contractul curent
                    if (consumer.isUnpaidFee()) {
                        if (consumer.getInitialBudget() >= (consumer.getContractPrice()
                                + paymentConstant * consumer.getFailedPayment())) {
                            consumer.setInitialBudget((int) (consumer.getInitialBudget()
                                    - consumer.getContractPrice()
                                    - paymentConstant * consumer.getFailedPayment()));
                            if (consumer.getFailedPaymentTo() != null) {
                                if (!consumer.getFailedPaymentTo().isBankrupt()) {
                                    consumer.getFailedPaymentTo()
                                            .setInitialBudget(consumer.getFailedPaymentTo()
                                                    .getInitialBudget()
                                                    + (int) paymentConstant
                                                    * consumer.getFailedPayment());
                                }
                                consumer.getChosenDistributor()
                                        .setInitialBudget(consumer
                                                .getChosenDistributor().getInitialBudget()
                                                + (int) consumer.getContractPrice());
                            }
                            consumer.setUnpaidFee(false);
                        } else {
                            //consumatorul declara faliment
                            consumer.setOutOfGame(true);
                            var chosenDistributor = consumer.getFailedPaymentTo();
                            outThisRound.add(consumer);
                            chosenDistributor.getContracts().remove(consumer);
                            chosenDistributor.setInitialBudget(chosenDistributor.getInitialBudget()
                                    - chosenDistributor.getInitialProductionCost());
                        }
                    }
                    //consumatorului i-a expirat contracul
                    if (consumer.getContractualTimeLeft() == 0 && !consumer.isOutOfGame()) {
                        //stergem contractantul din lista distribuitorului si ii
                        // permitem sa isi caute un alt contract
                        var oldDistributor = consumer.getChosenDistributor();
                        //cautam cea mai buna varianta de contract pentru consumator
                        var bestChoice = Distributor.getBestChoice(distributorList,
                                outThisRound.size());
                        if (bestChoice == null) {
                            gameOver = true;
                            break;
                        }
                        if (oldDistributor == null) {
                            bestChoice.getContracts().add(consumer);
                            newlyAddedConsumers.add(consumer);
                        } else if (!bestChoice.getId().equals(oldDistributor.getId())) {
                            oldDistributor.getContracts().remove(consumer);
                            bestChoice.getContracts().add(consumer);
                            newlyAddedConsumers.add(consumer);
                        }
                        //verificam ce consumatori urmeaza sa paraseasca distribuitorul la
                        //sfarsitul lunii pentru a putea calcula corect pretul pe contract
                        int wereHerePreviously = 0;
                        for (var old : outThisRound) {
                            if (old.getChosenDistributor().getId().equals(bestChoice.getId())) {
                                wereHerePreviously++;
                            }
                        }
                        consumer.setContractPrice(bestChoice.getContractFinalPrice(bestChoice
                                        .getInitialInfrastructureCost(),
                                bestChoice.getInitialProductionCost(),
                                newlyAddedConsumers, wereHerePreviously));
                        consumer.setChosenDistributor(bestChoice);
                        consumer.setContractualTimeLeft(bestChoice.getContractLength() - 1);

                        if (consumer.getInitialBudget() >= consumer.getContractPrice()) {
                            consumer.payContract(consumer);
                        } else {
                            consumer.setUnpaidFee(true);
                            consumer.setFailedPayment((int) consumer.getContractPrice());
                            consumer.setFailedPaymentTo(consumer.getChosenDistributor());
                        }

                    } else {
                        consumer.setContractualTimeLeft(consumer.getContractualTimeLeft() - 1);
                    }
                    //distribuitorul primeste pretul pe contract
                    var chosenDistributor = consumer.getChosenDistributor();
                    if (!consumer.isUnpaidFee()) {
                        chosenDistributor
                                .setInitialBudget((int) (chosenDistributor.getInitialBudget()
                                        + consumer.getContractPrice()));
                    }
                }
                //daca mai exista distribuitori in joc
                if (gameOver) {
                    break;
                }
                //scadem din bugetul distribuitorilor cheltuielile lunare
                for (var distributor : distributorList) {
                    if (distributor.isBankrupt()) {
                        continue;
                    }
                    for(var prod : distributor.getChosenProducers()){
                        if(prod.getMonthlyStats() == null){
                            prod.setMonthlyStats(new HashMap<>());
                        }
                        var prodMonthlyStats = prod.getMonthlyStats();
                        if(!prodMonthlyStats.containsKey(month)){
                            prodMonthlyStats.put(month, new ArrayList<>());
                        }
                        var monthDistr = prodMonthlyStats.get(month);
                        monthDistr.add(distributor);
                    }
                    distributor.setInitialBudget(distributor.getInitialBudget()
                            - distributor.getInitialInfrastructureCost());
                    if (!distributor.getContracts().isEmpty()) {
                        distributor.setInitialBudget(distributor.getInitialBudget()
                                - distributor.getInitialProductionCost()
                                * distributor.getContracts().size());
                    }
                    if (distributor.getInitialBudget() < 0) {
                        distributor.isBankrupt = true;
                        for (var consumer : consumerList) {
                            if (consumer.getChosenDistributor() != null
                                    && consumer.getChosenDistributor().getId()
                                    .equals(distributor.getId())) {
                                consumer.setChosenDistributor(null);
                                consumer.setContractualTimeLeft(0);
                                if (consumer.isUnpaidFee()) {
                                    consumer.setUnpaidFee(false);
                                }
                            }
                        }
                    }
                }

            }

        }
        var outputData = new OutputLoader(args[1]);
        var output = new Output();
        output.setConsumers(new ArrayList<>());
        output.setDistributors(new ArrayList<>());
        output.setEnergyProducers(new ArrayList<>());
        for (var consumer : consumerList) {

            output.getConsumers().add(new ConsumerOutput(consumer.getId(),
                    consumer.isOutOfGame(), consumer.getInitialBudget()));
        }
        for (var dis : distributorList) {
            var contractstList = dis.getContracts();
            contractstList = contractstList.stream().sorted(Comparator
                    .comparingInt(Consumer::getContractualTimeLeft)
                    .thenComparing(Consumer::getId)).collect(Collectors.toList());
            List<ContractOutput> contractOutputs = new ArrayList<>();
            for (var contract : contractstList) {
                contractOutputs.add(new ContractOutput(contract.getId(),
                        (int) contract.getContractPrice(), contract.getContractualTimeLeft()));
            }
            output.getDistributors().add(new DistributorOutput(dis.getId(),
                    dis.getInitialBudget(), dis.isBankrupt(), contractOutputs, dis.getEnergyNeededKW(), dis.getProducerStrategy(), (int)dis.getContractFinalPrice(dis.getInitialInfrastructureCost(), dis.getInitialProductionCost(), new ArrayList<>(), 0)));
        }
        for(var prod : producerList){
            var monthlyStatsOutput = prod.getMonthlyStats();
            List<MonthlyStatsOutput> monthlyStatsList = new ArrayList<>();
            if(monthlyStatsOutput == null){
                for (int month = 1; month <= inputDataSingleton.getNumberOfTurns(); month++){
                    monthlyStatsList.add(new MonthlyStatsOutput(month, new ArrayList<>()));
                }
            }
            else {
                for (var monthlyStatOutput : monthlyStatsOutput.entrySet()) {
                    monthlyStatsList.add(new MonthlyStatsOutput(monthlyStatOutput.getKey(), monthlyStatOutput.getValue().stream().map(d -> d.getId()).collect(Collectors.toList())));
                }
            }
            output.getEnergyProducers().add((new ProducerOutput(prod.getId(),prod.getMaxDistributors(),prod.getPriceKW(),prod.getEnergyType(),prod.getEnergyPerDistributor(),monthlyStatsList)));
        }
        outputData.setOutPutToWrite(output);
        outputData.writeData();
    }

    //metoda calculeaza cea mai buna varianta de contract pentru consumator si scade
    //din bugetul acestuia pretul contractului, avand in vedere daca acesta ramane cu restanta
    private static Distributor getBestChoice(final List<Distributor> distributorList,
                                             final Consumer consumer,
                                             final List<Consumer> newlyAddedConsumers) {
        var bestChoice = Distributor.getBestChoice(distributorList, 0);
        consumer.setContractPrice(bestChoice.getContractFinalPrice(bestChoice
                        .getInitialInfrastructureCost(), bestChoice.getInitialProductionCost(),
                newlyAddedConsumers, 0));
        consumer.setChosenDistributor(bestChoice);
        consumer.setContractualTimeLeft(bestChoice.getContractLength() - 1);
        bestChoice.getContracts().add(consumer);
        if (consumer.getInitialBudget() >= consumer.getContractPrice()) {
            consumer.payContract(consumer);
        } else {
            consumer.setUnpaidFee(true);
            consumer.setFailedPayment((int) consumer.getContractPrice());
            consumer.setFailedPaymentTo(consumer.getChosenDistributor());
        }
        return bestChoice;
    }

}
