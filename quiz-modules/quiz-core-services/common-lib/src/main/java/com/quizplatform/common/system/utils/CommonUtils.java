package com.quizplatform.common.system.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public final class CommonUtils {

    public static <T extends Collection<?>> boolean isEmptyOrNull(T collection) {
        return Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);
    }

    public static <U> boolean isEmptyOrNull(U[] array) {
        return Optional.of(array)
                .map(arr -> arr.length == 0)
                .orElse(true);
    }

    public static <T extends Collection<?>> boolean nonEmptyNorNull(T collection) {
        return !isEmptyOrNull(collection);
    }

    public static boolean isEmptyOrBlank(String str) {
        return Objects.isNull(str) || str.trim().isBlank();
    }

    /**
     * Safely checks if any of the values provided by the suppliers is null.
     * Method handles potential NullPointerExceptions during supplier evaluation.
     *
     * @param suppliers variable number of suppliers to evaluate and check their results
     * @return true if any supplier returns null or throws NullPointerException during evaluation,
     * false if all suppliers successfully provide non-null values
     * @throws IllegalArgumentException if no suppliers are provided
     */
    public static boolean anyNull(Supplier<?>... suppliers) {
        if(Objects.isNull(suppliers) || suppliers.length == 0){
            throw new IllegalArgumentException("At least one supplier must be provided for null check");
        }

        try{
            Object[] vals = Arrays.stream(suppliers)
                    .map(Supplier::get)
                    .toArray();

            return anyNull(vals);
        }catch (NullPointerException npe){
            return true;
        }

    }


    /**
     * Checks if any of the provided values is null.
     * This is the core logic method that performs the actual null checking.
     *
     * @param values array of values to check
     * @return true if any value is null, false otherwise
     */
    public static boolean anyNull(Object... values){
        if(Objects.isNull(values)){
            return true;
        }

        return Arrays.stream(values)
                .anyMatch(Objects::isNull);
    }
}

