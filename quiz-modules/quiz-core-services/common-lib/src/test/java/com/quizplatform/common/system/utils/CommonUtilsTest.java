package com.quizplatform.common.system.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommonUtilsTest {
    @Test
    void base_Collection_Utils() {
        List<Object> emptyArrList = new ArrayList<>();
        assertAll("Empty Collection or Array should return true if empty, false if not",
                () -> assertTrue(CommonUtils.isEmptyOrNull(emptyArrList)),
                () -> assertTrue(CommonUtils.isEmptyOrNull((List<Object>) null)),
                () -> assertFalse(CommonUtils.isEmptyOrNull(List.of(new Object())))
        );

        assertAll("Empty Collection or Array should return true if empty, false if not",
                () -> assertTrue(CommonUtils.nonEmptyNorNull(List.of(new Object()))),
                () -> assertFalse(CommonUtils.nonEmptyNorNull(emptyArrList))
        );
    }

    @Test
    @DisplayName("Should return true if any string is empty or blank")
    void blankOrEmpty_String_Utils(){
        List<String> emptyTestCases = Arrays.asList("", " ", null);
        // "non-empty" --> false
        emptyTestCases.forEach(
                tc -> assertTrue(CommonUtils.isEmptyOrBlank(tc), String.format("Empty, null or blank string should return true. Input '%s'", tc))
        );

        assertTrue(CommonUtils.isEmptyOrBlank(""));
    }


    @Test
    @DisplayName("Should throw IllegalArgumentException when suppliers array is null")
    void should_Throw_WhenSuppliersArray_IsNull() {
        Supplier<String>[] nullSuppliers = null;

        assertThatThrownBy(() -> CommonUtils.anyNull(nullSuppliers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one supplier must be provided for null check");
    }

    @Test
    @DisplayName("Should return true if any supplier is null")
    void should_Return_True_When_AnySupplier_ReturnsNull(){
        assertTrue(CommonUtils.anyNull(() -> "value1", () -> null, Object::new));
    }

    @Test
    @DisplayName("Should return true if any object is null")
    void should_Return_True_When_AnyObject_Null(){
        assertTrue(CommonUtils.anyNull(new Object(), null));
    }

    @Test
    @DisplayName("Should return true when supplier throws NullPointerException")
    void shouldReturnTrueWhenSupplierThrowsNPE() {
        Supplier<String> validSupplier = () -> "valid";
        Supplier<String> npeSupplier = () -> {
            String nullString = null;
            return nullString.toString(); // This will throw NPE
        };

        boolean result = CommonUtils.anyNull(validSupplier, npeSupplier);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return true when null passed through throws NullPointerException")
    void shouldReturnTrueWhenNullPassedThrowsNPE() {
        Object nullObj = null;

        boolean result = CommonUtils.anyNull(nullObj);

        assertTrue(result);
    }
}