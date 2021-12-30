
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public final class EqualityUtils {

    private EqualityUtils() {
    }

    /**
     * This method produces a conjunctive query based on the specified one where
     * equality atoms are removed and affected variables are replaced or merged.
     * It returns the new query and a substitution which allow to rebuild
     * answers to the original query based on answers to the returned one by
     * composition of each answer with the returned substitution.
     *
     * @param q a conjunctive query
     * @return a pair composed of the computed conjunctive query and the
     * substitution which allow to rebuild answers.
     */

    public static Pair<ConjunctiveQuery, Substitution> processEquality(ConjunctiveQuery q) {
        LinkedList<Atom> toRemove = new LinkedList<Atom>();
        Substitution s = DefaultSubstitutionFactory.instance().createSubstitution();
        CloseableIteratorWithoutException<Atom> it = q.getAtomSet().iterator();
        while (it.hasNext()) {
            Atom a = it.next();
            if (Predicate.EQUALITY.equals(a.getPredicate())) {
                if (a.getTerm(0).isVariable()) {
                    if (!updateSubstitution(s, (Variable) a.getTerm(0), a.getTerm(1))) {
                        return generateBottomResult();
                    }
                    toRemove.add(a);
                } else if (a.getTerm(1).isVariable()) {
                    if (!updateSubstitution(s, (Variable) a.getTerm(1), a.getTerm(0))) {
                        return generateBottomResult();
                    }
                    toRemove.add(a);
                } else {
                    return generateBottomResult();
                }
            }
        }
        return new ImmutablePair<ConjunctiveQuery, Substitution>(generateQuery(q, s, toRemove), s);
    }

    private static boolean updateSubstitution(Substitution s, Variable var, Term image) {
        return s.aggregate(var, image);
    }

    private static ConjunctiveQuery generateQuery(ConjunctiveQuery q, Substitution s, LinkedList<Atom> toRemove) {
        if (toRemove.isEmpty()) {
            return q;
        }

        List<Term> newAns = new LinkedList<Term>(q.getAnswerVariables());
//        newAns.removeAll(s.getTerms());

        InMemoryAtomSet newAtomSet = DefaultAtomSetFactory.instance().create();
        CloseableIteratorWithoutException<Atom> it = q.getAtomSet().iterator();
        while (it.hasNext()) {
            Atom a = it.next();
            if (!toRemove.contains(a)) {
                newAtomSet.add(s.createImageOf(a));
            }
        }

        return DefaultConjunctiveQueryFactory.instance().create(newAtomSet, newAns);
    }

    private static ImmutablePair<ConjunctiveQuery, Substitution> generateBottomResult() {
        return new ImmutablePair<ConjunctiveQuery, Substitution>(
                DefaultConjunctiveQueryFactory.instance().BOOLEAN_BOTTOM_QUERY, Substitutions.emptySubstitution());
    }

}
