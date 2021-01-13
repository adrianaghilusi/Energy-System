import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreenStrategy implements Strategy{
    @Override
    public List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded, Distributor distributor) {
        var greenProducers = producerList.stream().filter(str -> str.getEnergyType().isRenewable()).sorted(Comparator.comparing(Producer::getPriceKW).thenComparing(Producer::getEnergyPerDistributor, Comparator.reverseOrder())).collect(Collectors.toList());
        int energyProvided=0;
        List<Producer> producers = new ArrayList<>();
        for(var prod : greenProducers){
            if(prod.getCurrentDistributors() >= prod.getMaxDistributors()) continue;
            if(energyProvided < totalNeeded){
                energyProvided = energyProvided + prod.getEnergyPerDistributor();
                producers.add(prod);
                var currentDistributors = prod.getCurrentDistributorsList();
                if(currentDistributors.stream().noneMatch(d->d.getId() == distributor.getId())){
                    currentDistributors.add(distributor);
                    prod.setCurrentDistributors(prod.getCurrentDistributors()+1);
                }
               //
            }
            else
            {
                break;
            }
        }
        if(energyProvided < totalNeeded){
            var otherProducers = producerList.stream().filter(str -> !str.getEnergyType().isRenewable()).sorted(Comparator.comparing(Producer::getPriceKW).thenComparing(Producer::getEnergyPerDistributor,Comparator.reverseOrder())).collect(Collectors.toList());
            for(var prod : otherProducers){
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
        }
        return producers;

    }
}
