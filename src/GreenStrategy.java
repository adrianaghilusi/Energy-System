import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreenStrategy implements Strategy{
    @Override
    public List<Producer> applyStrategy(List<Producer> producerList, Integer totalNeeded) {
     var sortedList =   producerList
                .stream()
                .sorted(Comparator.comparing(Producer::getEnergyType).thenComparing(Producer::getPriceKW)).collect(Collectors.toList());
     int energyProvided=0;
     List<Producer> producers = new ArrayList<>();
     for(var prod : sortedList){
         if(energyProvided < totalNeeded){
             energyProvided = energyProvided + prod.getEnergyPerDistributor();
             producers.add(prod);
         }
         else
         {
             break;
         }
     }
    return producers;
    }
}
