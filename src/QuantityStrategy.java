import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuantityStrategy implements Strategy{
    @Override
    public List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded, Distributor distributor) {
        var sortedList =   producerList
                .stream()
                .sorted(Comparator.comparing(Producer::getEnergyPerDistributor).reversed()).collect(Collectors.toList());
        int energyProvided=0;
        List<Producer> producers = new ArrayList<>();
        for(var prod : sortedList){
            if(prod.getCurrentDistributors() >= prod.getMaxDistributors()) continue;
            if(energyProvided < totalNeeded){
                energyProvided = energyProvided + prod.getEnergyPerDistributor();
                producers.add(prod);
                var currentDistributors = prod.getCurrentDistributorsList();
                if(currentDistributors.stream().noneMatch(d->d.getId() == distributor.getId())){
                    currentDistributors.add(distributor);
                    prod.setCurrentDistributors(prod.getCurrentDistributors()+1);
                }
            }
            else
            {
                break;
            }
        }
        return producers;
    }
}
