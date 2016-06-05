package me.stuntguy3000.java.telegames.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer} for three values.
 *
 * @author Nick Robson
 *
 * @param <A> The first value's type.
 * @param <B> The second value's type.
 * @param <C> The third value's type.
 */
public interface TriConsumer<A, B, C> {

    /**
     * Accepts values.
     *
     * @param a The first value.
     * @param b The second value.
     * @param c The third value.
     */
    void accept(A a, B b, C c);

    /**
     * Composes this TriConsumer with another.
     *
     * @param after The TriConsumer to execute after this one.
     *
     * @return A new TriConsumer that executes this one and then the given one.
     */
    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);

        return (a, b, c) -> {
            this.accept(a, b, c);
            after.accept(a, b, c);
        };
    }

}
