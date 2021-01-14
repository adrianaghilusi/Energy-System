import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreenStrategy implements Strategy {
    @Override
    /*
      Metoda urmareste sa ofere distribuitorului cea mai buna varianta de energie de tip
      renewable. Daca energia nu este suficienta nevoilor sale sau nu gaseste niciun producator
      cu energie regenerabila, va cauta intai producatorul cu cel mai bun pret si apoi, dupa
      necesitati, producatorul cu cea mai mare cantitate de energie oferita lunar.
     */
    public List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded,
                                        Distributor distributor) {

        var greenProducers = producerList.stream()
                .filter(str -> str.getEnergyType().isRenewable())
                .sorted(Comparator.comparing(Producer::getPriceKW)
                        .thenComparing(Producer::getEnergyPerDistributor,
                                Comparator.reverseOrder())).collect(Collectors.toList());
        int energyProvided = 0;
        List<Producer> producers = new ArrayList<>();
        for (var prod : greenProducers) {
            if (prod.getCurrentDistributors() >= prod.getMaxDistributors()) {
                continue;
            }
            var currentDistributors = prod.getCurrentDistributorsList();
            if (energyProvided < totalNeeded) {
                energyProvided = energyProvided + prod.getEnergyPerDistributor();
                producers.add(prod);
                if (currentDistributors.stream().noneMatch(d -> d.getId()
                        .equals(distributor.getId()))) {
                    currentDistributors.add(distributor);
                    prod.setCurrentDistributors(prod.getCurrentDistributors() + 1);
                }

            } else {
                break;
            }
        }
        if (energyProvided < totalNeeded) {
            var otherProducers = producerList
                    .stream().sorted(Comparator.comparing(Producer::getPriceKW)
                            .thenComparing(Producer::getEnergyPerDistributor,
                                    Comparator.reverseOrder())).collect(Collectors.toList());
            for (var prod : otherProducers) {
                if (prod.getCurrentDistributors() >= prod.getMaxDistributors()) {
                    continue;
                }
                var currentDistributors = prod.getCurrentDistributorsList();
                if (currentDistributors.stream().anyMatch(d -> d.getId()
                        .equals(distributor.getId()))) {
                    continue;
                }
                if (energyProvided < totalNeeded) {
                    energyProvided = energyProvided + prod.getEnergyPerDistributor();
                    producers.add(prod);
                    if (currentDistributors.stream().noneMatch(d -> d.getId()
                            .equals(distributor.getId()))) {
                        currentDistributors.add(distributor);
                        prod.setCurrentDistributors(prod.getCurrentDistributors() + 1);
                    }
                } else {
                    break;
                }
            }
        }
        return producers;

    }
}
