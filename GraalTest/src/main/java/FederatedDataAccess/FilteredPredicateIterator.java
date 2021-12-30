package FederatedDataAccess;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FilteredPredicateIterator implements CloseableIterator<Predicate> {

    private Set<Predicate> filteredPredicates = new HashSet<>();
    private Iterator<Predicate> filteredIterator;

    public FilteredPredicateIterator(Collection<String> tableNames, CloseableIterator<Predicate> predicateIterator){
        try{
            while (predicateIterator.hasNext()){
                Predicate p = predicateIterator.next();
                if (tableNames.contains(p.getIdentifier().toString())){
                    filteredPredicates.add(p);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        filteredIterator = filteredPredicates.iterator();
    }

    public boolean hasNext(){
        return filteredIterator.hasNext();
    }

    public Predicate next(){
        return filteredIterator.next();
    }

    @Override
    public void close(){
    }
}
