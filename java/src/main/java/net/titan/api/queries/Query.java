package net.titan.api.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Query<T, Q extends Query<T, Q>> implements Iterable<T> {
    protected final List<T> items;

    protected Query(Collection<T> source) {
        this.items = new ArrayList<>(source == null ? Collections.emptyList() : source);
    }

    public Q where(Predicate<? super T> predicate) {
        if (predicate == null) return self();
        items.removeIf(item -> !predicate.test(item));
        return self();
    }

    public Q when(boolean condition, Consumer<? super Q> action) {
        if (condition && action != null) action.accept(self());
        return self();
    }

    public Q sortBy(Comparator<? super T> comparator) {
        if (comparator != null) items.sort(comparator);
        return self();
    }

    public int count() { return items.size(); }
    public boolean any() { return !items.isEmpty(); }
    public boolean empty() { return items.isEmpty(); }

    public Optional<T> first() {
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    public List<T> toList() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public List<T> items() {
        return toList();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        items.forEach(action);
    }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    @SuppressWarnings("unchecked")
    protected Q self() {
        return (Q) this;
    }
}
