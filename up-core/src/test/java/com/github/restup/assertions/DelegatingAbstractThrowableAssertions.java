package com.github.restup.assertions;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.presentation.Representation;

class DelegatingAbstractThrowableAssertions<SELF, D extends AbstractThrowableAssert<D, ACTUAL>, ACTUAL extends Throwable> extends org.assertj.core.api.Assertions {

    private final AbstractThrowableAssert<D,ACTUAL> assertions;

    DelegatingAbstractThrowableAssertions(AbstractThrowableAssert<D, ACTUAL> assertions) {
        super();
        this.assertions = assertions;
    }
    
    @SuppressWarnings("unchecked")
    protected SELF me() {
        return (SELF) this;
    }

    public SELF hasMessage(String message) {
        assertions.hasMessage(message);
        return me();
    }

    public SELF as(Description description) {
        assertions.as(description);
        return me();
    }

    public SELF as(String description, Object... args) {
        assertions.as(description, args);
        return me();
    }

    public SELF hasMessage(String message, Object... parameters) {
        assertions.hasMessage(message, parameters);
        return me();
    }

    public SELF isEqualToIgnoringNullFields(Object other) {
        assertions.isEqualToIgnoringNullFields(other);
        return me();
    }

    public SELF hasCause(Throwable cause) {
        assertions.hasCause(cause);
        return me();
    }

    public SELF hasNoCause() {
        assertions.hasNoCause();
        return me();
    }

    public SELF isEqualToComparingOnlyGivenFields(Object other, String... propertiesOrFieldsUsedInComparison) {
        assertions.isEqualToComparingOnlyGivenFields(other, propertiesOrFieldsUsedInComparison);
        return me();
    }

    public SELF hasMessageStartingWith(String description) {
        assertions.hasMessageStartingWith(description);
        return me();
    }

    public SELF hasMessageContaining(String description) {
        assertions.hasMessageContaining(description);
        return me();
    }

    public SELF hasStackTraceContaining(String description) {
        assertions.hasStackTraceContaining(description);
        return me();
    }

    public SELF hasMessageMatching(String regex) {
        assertions.hasMessageMatching(regex);
        return me();
    }

    public SELF isEqualToIgnoringGivenFields(Object other, String... propertiesOrFieldsToIgnore) {
        assertions.isEqualToIgnoringGivenFields(other, propertiesOrFieldsToIgnore);
        return me();
    }

    public SELF hasMessageEndingWith(String description) {
        assertions.hasMessageEndingWith(description);
        return me();
    }

    public SELF hasCauseInstanceOf(Class<? extends Throwable> type) {
        assertions.hasCauseInstanceOf(type);
        return me();
    }

    public SELF describedAs(String description, Object... args) {
        assertions.describedAs(description, args);
        return me();
    }

    public SELF describedAs(Description description) {
        assertions.describedAs(description);
        return me();
    }

    public SELF isEqualTo(Object expected) {
        assertions.isEqualTo(expected);
        return me();
    }

    public SELF isNotEqualTo(Object other) {
        assertions.isNotEqualTo(other);
        return me();
    }

    public SELF hasCauseExactlyInstanceOf(Class<? extends Throwable> type) {
        assertions.hasCauseExactlyInstanceOf(type);
        return me();
    }

    public SELF isNotNull() {
        assertions.isNotNull();
        return me();
    }

    public SELF isSameAs(Object expected) {
        assertions.isSameAs(expected);
        return me();
    }

    public SELF isNotSameAs(Object other) {
        assertions.isNotSameAs(other);
        return me();
    }

    public SELF isIn(Object... values) {
        assertions.isIn(values);
        return me();
    }

    public SELF hasNoNullFieldsOrProperties() {
        assertions.hasNoNullFieldsOrProperties();
        return me();
    }

    public SELF isNotIn(Object... values) {
        assertions.isNotIn(values);
        return me();
    }

    public SELF isIn(Iterable<?> values) {
        assertions.isIn(values);
        return me();
    }

    public SELF isNotIn(Iterable<?> values) {
        assertions.isNotIn(values);
        return me();
    }

    public SELF is(Condition<? super ACTUAL> condition) {
        assertions.is(condition);
        return me();
    }

    public SELF hasRootCauseInstanceOf(Class<? extends Throwable> type) {
        assertions.hasRootCauseInstanceOf(type);
        return me();
    }

    public SELF isNot(Condition<? super ACTUAL> condition) {
        assertions.isNot(condition);
        return me();
    }

    public SELF has(Condition<? super ACTUAL> condition) {
        assertions.has(condition);
        return me();
    }

    public SELF isInstanceOf(Class<?> type) {
        assertions.isInstanceOf(type);
        return me();
    }

    public SELF hasNoNullFieldsOrPropertiesExcept(String... propertiesOrFieldsToIgnore) {
        assertions.hasNoNullFieldsOrPropertiesExcept(propertiesOrFieldsToIgnore);
        return me();
    }

    public <T> SELF isInstanceOfSatisfying(Class<T> type, Consumer<T> requirements) {
        assertions.isInstanceOfSatisfying(type, requirements);
        return me();
    }

    public SELF isInstanceOfAny(Class<?>... types) {
        assertions.isInstanceOfAny(types);
        return me();
    }

    public SELF hasRootCauseExactlyInstanceOf(Class<? extends Throwable> type) {
        assertions.hasRootCauseExactlyInstanceOf(type);
        return me();
    }

    public SELF isNotInstanceOf(Class<?> type) {
        assertions.isNotInstanceOf(type);
        return me();
   }

    public SELF isNotInstanceOfAny(Class<?>... types) {
        assertions.isNotInstanceOfAny(types);
        return me();
    }

    public SELF hasSameClassAs(Object other) {
        assertions.hasSameClassAs(other);
        return me();
    }

    public SELF hasToString(String expectedToString) {
        assertions.hasToString(expectedToString);
        return me();
    }

    public SELF doesNotHaveSameClassAs(Object other) {
        assertions.doesNotHaveSameClassAs(other);
        return me();
    }

    public SELF isExactlyInstanceOf(Class<?> type) {
        assertions.isExactlyInstanceOf(type);
        return me();
    }

    public SELF isEqualToComparingFieldByField(Object other) {
        assertions.isEqualToComparingFieldByField(other);
        return me();
    }

    public SELF isNotExactlyInstanceOf(Class<?> type) {
        assertions.isNotExactlyInstanceOf(type);
        return me();
    }

    public SELF isOfAnyClassIn(Class<?>... types) {
        assertions.isOfAnyClassIn(types);
        return me();
    }

    public SELF hasNoSuppressedExceptions() {
        assertions.hasNoSuppressedExceptions();
        return me();
    }

    public SELF isNotOfAnyClassIn(Class<?>... types) {
        assertions.isNotOfAnyClassIn(types);
        return me();
    }


    public SELF hasSuppressedException(Throwable suppressedException) {
        assertions.hasSuppressedException(suppressedException);
        return me();
   }

    public SELF overridingErrorMessage(String newErrorMessage, Object... args) {
        assertions.overridingErrorMessage(newErrorMessage, args);
        return me();
    }

    public <T> SELF usingComparatorForFields(Comparator<T> comparator, String... propertiesOrFields) {
        assertions.usingComparatorForFields(comparator, propertiesOrFields);
        return me();
    }

    public SELF withFailMessage(String newErrorMessage, Object... args) {
        assertions.withFailMessage(newErrorMessage, args);
        return me();
    }

    public void doesNotThrowAnyException() {
        assertions.doesNotThrowAnyException();
    }

    public SELF usingComparator(Comparator<? super ACTUAL> customComparator) {
        assertions.usingComparator(customComparator);
        return me();
    }

    public SELF usingDefaultComparator() {
        assertions.usingDefaultComparator();
        return me();
    }

    public SELF withThreadDumpOnError() {
        assertions.withThreadDumpOnError();
        return me();
    }

    public SELF withRepresentation(Representation representation) {
        assertions.withRepresentation(representation);
        return me();
    }

    public SELF matches(Predicate<? super ACTUAL> predicate) {
        assertions.matches(predicate);
        return me();
    }

    public SELF matches(Predicate<? super ACTUAL> predicate, String predicateDescription) {
        assertions.matches(predicate, predicateDescription);
        return me();
    }

    public <T> SELF usingComparatorForType(Comparator<? super T> comparator, Class<T> type) {
        assertions.usingComparatorForType(comparator, type);
        return me();
    }

    public SELF satisfies(Consumer<ACTUAL> requirements) {
        assertions.satisfies(requirements);
        return me();
    }

    public SELF hasFieldOrProperty(String name) {
        assertions.hasFieldOrProperty(name);
        return me();
    }

    public SELF hasSameHashCodeAs(Object other) {
        assertions.hasSameHashCodeAs(other);
        return me();
    }

    public SELF hasFieldOrPropertyWithValue(String name, Object value) {
        assertions.hasFieldOrPropertyWithValue(name, value);
        return me();
    }

    public SELF isEqualToComparingFieldByFieldRecursively(Object other) {
        assertions.isEqualToComparingFieldByFieldRecursively(other);
        return me();
    }

    public <T> SELF returns(T expected, Function<ACTUAL, T> from) {
        assertions.returns(expected, from);
        return me();
    }
    
    
}
