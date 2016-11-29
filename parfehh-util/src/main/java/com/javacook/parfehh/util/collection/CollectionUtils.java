package com.javacook.parfehh.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Several utils around Java collections
 */
public class CollectionUtils {

    /**
     * Checks whether there is a duplicate in <code>collection</code>.
     * @param collection the collection to examine
     * @param <T> a type parameter
     * @return true if all elements are pairwise different
     */
    public static <T> boolean pairwiseDifferent(Collection<T> collection) {
        return collection.stream().allMatch(new HashSet<>()::add);
    }

    /**
     * Checks whether there is a duplicate in the image of <code>collection</code>
     * by the function <code>mapper</code>.
     * @param collection the collection to examine
     * @param mapper the function mapping the element of <code>collection</code> first
     * @param consumer an operation that is excuted when a duplicate is found
     * @param <T> a type parameter of <code>collection</code>
     * @param <S> a type parameter of the image <code>mapper(collection)</code>
     * @return
     */
    public static <T, S> boolean pairwiseDifferent(Collection<T> collection,
                                                Function<T, S> mapper,
                                                Consumer<S> consumer) {
        Set<S> set = new HashSet<>();
        return collection.stream()
                .map(mapper)
                .allMatch(t -> {
                    if (set.add(t)) return true;
                    else {
                        consumer.accept(t);
                        return false;
                    }
                });
    }
}
