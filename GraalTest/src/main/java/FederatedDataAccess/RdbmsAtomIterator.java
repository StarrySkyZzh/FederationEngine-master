//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package FederatedDataAccess;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import java.util.LinkedList;
import java.util.List;

public class RdbmsAtomIterator implements CloseableIterator<Atom> {
    private RdbmsStore store;
    private boolean hasNextCallDone = false;
    private CloseableIterator<Predicate> predicateIt;
    private CloseableIterator<Atom> atomIt;

    RdbmsAtomIterator(RdbmsStore store) throws AtomSetException {
        this.store = store;
        this.init();
    }


    private void init() throws AtomSetException {
        this.predicateIt = this.store.predicatesIterator();
    }

    public boolean hasNext() throws IteratorException {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            while(this.predicateIt.hasNext() && (this.atomIt == null || !this.atomIt.hasNext())) {
                Predicate p = (Predicate)this.predicateIt.next();
                List<Term> terms = new LinkedList();
                VariableGenerator gen = new DefaultVariableGenerator("X");

                for(int i = 0; i < p.getArity(); ++i) {
                    terms.add(gen.getFreshSymbol());
                }

                InMemoryAtomSet atomSet = new LinkedListAtomSet();
                Atom atom = new DefaultAtom(p, terms);
                atomSet.add(atom);
                ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(atomSet);
                SqlHomomorphism solver = SqlHomomorphism.instance();

                try {
                    this.atomIt = new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this.store));
                } catch (HomomorphismException var9) {
                    throw new IteratorException(var9);
                }
            }
        }

        return this.atomIt != null && this.atomIt.hasNext();
    }

    public Atom next() throws IteratorException {
        if (!this.hasNextCallDone) {
            this.hasNext();
        }

        this.hasNextCallDone = false;
        return (Atom)this.atomIt.next();
    }

    public void close() {
        if (this.predicateIt != null) {
            this.predicateIt.close();
        }

        if (this.atomIt != null) {
            this.atomIt.close();
        }

    }
}
