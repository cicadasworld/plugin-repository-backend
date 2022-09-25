package gtcloud.plugin.repository.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateBuilder<T> {

    private List<Predicate<T>> predicates = new ArrayList<>();

    public PredicateBuilder<T> addPredicate(boolean condition, Predicate<T> predicate) {
        if (condition) {
            this.predicates.add(predicate);
        }
        return this;
    }

    public Predicate<T> build() {
        int size = this.predicates.size();
        if (size == 0) {
            return t -> true;
        } else if (size == 1) {
            return this.predicates.get(0);
        } else {
            Predicate<T> result = this.predicates.get(0);
            for (int i = 1; i < size; i++) {
                result = result.and(this.predicates.get(i));
            }
            return result;
        }
    }
}
