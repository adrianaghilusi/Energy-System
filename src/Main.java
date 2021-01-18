import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

class Main {

    public static void main(final String[] args) throws Exception {
        String test = "C:\\Users\\user\\Desktop\\teme-proiect-etapa2-2020\\teme\\proiect-etapa2-energy-system\\checker\\resources\\in\\complex_5.json";
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
        addConsumers(consumerListInput, consumerList, consumerFactory);
        ProducerFactory producerFactory = ProducerFactory.getInstance();
        List<ProducerInput> producerInputList = inputDataSingleton.getInitialData().getProducers();
        addProducers(producerList, producerFactory, producerInputList);
        DistributorFactory distributorFactory = DistributorFactory.getInstance();
        List<DistributorInput> distributorListInput = inputDataSingleton.getInitialData()
                .getDistributors();
        List<Distributor> distributorList = new ArrayList<>();
        addDistributors(producerList, distributorFactory, distributorListInput, distributorList);
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
                List<InputProducerChanges> monthlyProducerChanges = updates.get(month - 1)
                        .getProducerChanges();
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
                                if (consumer.isUnpaidFee()) {
                                    consumer.setInitialBudget((int) (consumer.getInitialBudget()
                                            - consumer.getContractPrice()));
                                    consumer.getChosenDistributor().setInitialBudget((int)
                                            (consumer.getChosenDistributor().getInitialBudget()
                                                    + consumer.getContractPrice()));
                                }
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
                            if (month == inputDataSingleton.getNumberOfTurns()) {

                                oldDistributor
                                        .setLastContractPrice((int) oldDistributor
                                                .getContractFinalPrice(oldDistributor
                                                                .getInitialInfrastructureCost(),
                                                        oldDistributor
                                                                .getInitialProductionCost(),
                                                        newlyAddedConsumers, outThisRound.size()));

                            }
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
                //facem update pentru producatori
                boolean producerUpdates = false;
                for (var prodUpdate : monthlyProducerChanges) {
                    producerUpdates = true;
                    Integer observer = prodUpdate.getId();
                    Producer thisProducer = producerList.stream()
                            .filter(producer -> observer.equals(producer.getId()))
                            .findAny()
                            .orElse(null);
                    SimulationSystem observable = new SimulationSystem();
                    if (thisProducer == null) {
                        continue;
                    }
                    observable.addObserver(thisProducer);
                    observable.setEnergyChange(prodUpdate.getEnergyPerDistributor());
                    assertEquals(thisProducer.getEnergyPerDistributor(),
                            prodUpdate.getEnergyPerDistributor());
                    //atunci cand un producator face update, eliberam lista sa de distribuitori
                    //iar pentru fiecare distribuitor, scoatem producatorul in cauza din lista sa
                    //urmand sa ii cautam un altul
                    for (var distributor : distributorList) {
                        if (distributor.getChosenProducers().contains(thisProducer)) {
                            var chosenProducers = distributor.getChosenProducers();
                            chosenProducers.clear();
                            for (var prod : producerList) {
                                if (prod.getCurrentDistributorsList().contains(distributor)) {
                                    prod.getCurrentDistributorsList().remove(distributor);
                                    prod.setCurrentDistributors(prod.getCurrentDistributors() - 1);
                                }
                            }

                        }
                        var strategy = distributor.getProducerStrategy();
                        Strategy selectedStrategy = getStrategy(strategy);
                        int currentEnergy = 0;
                        if (distributor.getChosenProducers() != null) {
                            for (var prod : distributor.getChosenProducers()) {
                                currentEnergy = currentEnergy + prod.getEnergyPerDistributor();
                            }
                        }
                        if (currentEnergy < distributor.getEnergyNeededKW()) {
                            distributor.setChosenProducers(selectedStrategy
                                    .applyStrategy(producerList,
                                            distributor.getEnergyNeededKW(), distributor));
                        }

                        if (month != inputDataSingleton.getNumberOfTurns()) {
                            long cost = 0;
                            for (var prod : distributor.getChosenProducers()) {
                                cost = cost + prod.contractCost();
                            }
                            distributor.setInitialProductionCost(Math.round(cost / 10));
                        }

                        for (var prod : distributor.getChosenProducers()) {
                            if (prod.getMonthlyStats() == null) {
                                prod.setMonthlyStats(new HashMap<>());
                            }
                            var prodMonthlyStats = prod.getMonthlyStats();
                            if (!prodMonthlyStats.containsKey(month)) {
                                prodMonthlyStats.put(month, new ArrayList<>());
                            }
                            var monthDistr = prodMonthlyStats.get(month);

                            if (monthDistr.size() >= prod.getMaxDistributors()) {
                                var currentProducerDistributors = prod
                                        .getCurrentDistributorsList();
                                currentProducerDistributors.remove(distributor);
                                prod.setCurrentDistributors(prod.getCurrentDistributors() - 1);

                                distributor.setChosenProducers(selectedStrategy
                                        .applyStrategy(producerList,
                                                distributor.getEnergyNeededKW(), distributor));

                            } else {
                                if (!monthDistr.contains(distributor)) {
                                    monthDistr.add(distributor);
                                }

                            }

                        }
                    }
                }
                //daca nu s-au facut update-uri pentru producatori, doar updatam monthlyStats
                if (!producerUpdates) {
                    for (var distributor : distributorList) {
                        for (var prod : distributor.getChosenProducers()) {
                            if (prod.getMonthlyStats() == null) {
                                prod.setMonthlyStats(new HashMap<>());
                            }
                            var prodMonthlyStats = prod.getMonthlyStats();
                            if (!prodMonthlyStats.containsKey(month)) {
                                prodMonthlyStats.put(month, new ArrayList<>());
                            }
                        }
                    }
                }
            }
            //verificam ca lista de distribuitori curenti sa se sincronizeze cu lista de
            //distribuitori din MonthlyStats pentru luna curenta
            for (var prod : producerList) {
                if (prod.getMonthlyStats() != null && prod.getMonthlyStats().get(month) != null) {

                    if (prod.getMonthlyStats().get(month).size() != prod.getCurrentDistributors()) {

                        prod.getMonthlyStats().get(month).clear();
                        prod.getMonthlyStats().get(month).addAll(prod.getCurrentDistributorsList());

                    }
                }

            }

        }

        var outputData = new OutputLoader(args[1]);
        var output = new Output();
        output.setConsumers(new ArrayList<>());
        output.setDistributors(new ArrayList<>());
        output.setEnergyProducers(new ArrayList<>());
        writeConsumersToOutput(consumerList, output);
        writeDistributorsToOutput(distributorList, output);
        writeProducersToOutput(inputDataSingleton, producerList, output);
        outputData.setOutPutToWrite(output);
        outputData.writeData();
    }

    public static void writeProducersToOutput(InputSingleton inputDataSingleton,
                                              List<Producer> producerList, Output output) {
        for (var prod : producerList) {
            var monthlyStatsOutput = prod.getMonthlyStats();
            List<MonthlyStatsOutput> monthlyStatsList = new ArrayList<>();
            if (monthlyStatsOutput == null) {
                for (int month = 1; month <= inputDataSingleton.getNumberOfTurns(); month++) {
                    monthlyStatsList.add(new MonthlyStatsOutput(month, new ArrayList<>()));
                }
            } else {
                if (monthlyStatsOutput.size() != inputDataSingleton.getNumberOfTurns()) {
                    for (int i = 1; i < inputDataSingleton.getNumberOfTurns() + 1; i++) {
                        if (!monthlyStatsOutput.containsKey(i)) {
                            monthlyStatsList.add(new MonthlyStatsOutput(i, new ArrayList<>()));
                        }

                    }
                }

                for (var monthlyStatOutput : monthlyStatsOutput.entrySet()) {

                    monthlyStatsList.add(new MonthlyStatsOutput(monthlyStatOutput.getKey(),
                            monthlyStatOutput.getValue().stream()
                                    .map(d -> d.getId()).collect(Collectors.toList())));

                }

            }

            var sorted = monthlyStatsList.stream().sorted(Comparator
                    .comparingInt(MonthlyStatsOutput::getMonth)
            ).collect(Collectors.toList());

            output.getEnergyProducers()
                    .add((new ProducerOutput(prod.getId(),
                            prod.getMaxDistributors(), prod.getPriceKW(),
                            prod.getEnergyType(), prod.getEnergyPerDistributor(), sorted)));
        }
    }

    public static void writeConsumersToOutput(List<Consumer> consumerList, Output output) {
        for (var consumer : consumerList) {
            output.getConsumers().add(new ConsumerOutput(consumer.getId(),
                    consumer.isOutOfGame(), consumer.getInitialBudget()));
        }
    }

    public static void writeDistributorsToOutput(List<Distributor> distributorList, Output output) {
        for (var dis : distributorList) {
            var contractstList = dis.getContracts();
            contractstList = contractstList.stream().sorted(Comparator
                    .comparingInt(Consumer::getContractualTimeLeft)
                    .thenComparing(Consumer::getId)).collect(Collectors.toList());
            List<ContractOutput> contractOutputs = new ArrayList<>();
            //stabilim costul final al contractului pentru fiecare distribuitor
            int maxMonth = 0;
            int lastCost = 0;
            for (var contract : contractstList) {
                contractOutputs.add(new ContractOutput(contract.getId(),
                        (int) contract.getContractPrice(), contract.getContractualTimeLeft()));
                if (maxMonth < contract.getContractualTimeLeft()) {
                    maxMonth = contract.getContractualTimeLeft();
                    lastCost = (int) contract.getContractPrice();
                }
            }
            if (lastCost == 0 && dis.getLastContractPrice() == null) {
                dis.setLastContractPrice((int) dis
                        .getContractFinalPrice(dis.getInitialInfrastructureCost(),
                                dis.getInitialProductionCost(), new ArrayList<>(), 0));
            } else {
                if (dis.getLastContractPrice() == null) {
                    dis.setLastContractPrice(lastCost);
                }

            }
            output.getDistributors().add(new DistributorOutput(dis.getId(),
                    dis.getInitialBudget(), dis.isBankrupt(), contractOutputs,
                    dis.getEnergyNeededKW(), dis.getProducerStrategy(),
                    dis.getLastContractPrice()));
        }
    }

    //metoda adauga distribuitorii din lista de input si le aplica strategia
    public static void addDistributors(List<Producer> producerList,
                                       DistributorFactory distributorFactory,
                                       List<DistributorInput> distributorListInput,
                                       List<Distributor> distributorList) {
        for (var distributor : distributorListInput) {
            var distributorToAdd = distributorFactory.getDistributor();
            distributorToAdd = new Distributor(distributor.getId(), distributor.getContractLength(),
                    distributor.getInitialBudget(), distributor.getInitialInfrastructureCost(),
                    distributor.getEnergyNeededKW(), distributor.getProducerStrategy());
            var strategy = distributor.getProducerStrategy();
            Strategy selectedStrategy = getStrategy(strategy);

            distributorToAdd.setChosenProducers(selectedStrategy.applyStrategy(producerList,
                    distributor.getEnergyNeededKW(), distributorToAdd));

            long cost = 0;
            for (var prod : distributorToAdd.getChosenProducers()) {
                cost = cost + prod.contractCost();
            }
            distributorToAdd.setInitialProductionCost(Math.round(cost / 10));
            distributorList.add(distributorToAdd);
        }
    }

    //metoda adauga producatorii din lista de input
    public static void addProducers(List<Producer> producerList,
                                    ProducerFactory producerFactory,
                                    List<ProducerInput> producerInputList) {
        for (var producer : producerInputList) {
            var producerToAdd = producerFactory.getProducer();
            producerToAdd = new Producer(producer.getId(), producer.getEnergyType(),
                    producer.getMaxDistributors(), producer.getPriceKW(),
                    producer.getEnergyPerDistributor());
            producerToAdd.setCurrentDistributors(0);
            producerToAdd.setCurrentDistributorsList(new ArrayList<>());
            producerList.add(producerToAdd);
        }
    }

    //metoda adauga consumatorii din lista de input
    public static void addConsumers(List<ConsumerInput> consumerListInput,
                                    List<Consumer> consumerList, ConsumerFactory consumerFactory) {
        for (var consumer : consumerListInput) {
            var consumerToAdd = consumerFactory
                    .getConsumer(ConsumerEnum.valueOf("Basic"));
            consumerToAdd = new Consumer(consumer.getId(), consumer
                    .getInitialBudget(), consumer.getMonthlyIncome());
            consumerList.add(consumerToAdd);
        }
    }

    //metoda primeste de la un distribuitor un tip de strategie si o returneaza pe cea optima
    public static Strategy getStrategy(EnergyChoiceStrategyType strategy) {
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
        return selectedStrategy;
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
