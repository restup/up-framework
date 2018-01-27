package com.github.restup.bind.converter;

import static com.github.restup.bind.converter.ConversionUtils.toBigInteger;
import static com.github.restup.bind.converter.ConversionUtils.toByte;
import static com.github.restup.bind.converter.ConversionUtils.toCharacter;
import static com.github.restup.bind.converter.ConversionUtils.toLocalDate;
import static com.github.restup.bind.converter.ConversionUtils.toLocalTime;
import static com.github.restup.bind.converter.ConversionUtils.toLong;
import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ConversionUtilsTest {
    
    @Test
    public void testToLocalTime() {
        assertEquals(LocalTime.of(1, 21), toLocalTime("1:21"));
        assertEquals(LocalTime.of(1, 21), toLocalTime("01:21"));
    }

    @Test
    public void testToBigInteger() {
        assertEquals( BigInteger.TEN, toBigInteger(BigDecimal.TEN));
    }

    @Test
    public void testToLocalDate() {
        assertEquals(LocalDate.of(2012, 4, 29), toLocalDate("2012-04-29"));
        assertEquals(LocalDate.of(2012, 4, 29), toLocalDate("2012-04-29T00:00"));
        assertEquals(LocalDate.of(2012, 4, 29), toLocalDate("2012-04-29T00:00:00"));
        illegalToLocalDate("2012-04-29T123");
    }

    private void illegalToLocalDate(String value) {
        Throwable thrownException = catchThrowable( () -> toLocalDate(value));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(DateTimeException.class)
                .hasNoCause();
    }

    @Test
    public void testToCharacter() {
        illegalToCharacterArgument(null);
        illegalToCharacterArgument("");
        illegalToCharacterArgument("ab");
        assertEquals(Character.valueOf('a'), toCharacter("a"));
    }

    private void illegalToCharacterArgument(String value) {
        Throwable thrownException = catchThrowable( () -> toCharacter(value));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("String length must be 1")
                .hasNoCause();
    }

    @Test
    public void testToLong() {
        longOverflow("9999999999999999999999");
        longOverflow("-9999999999999999999999");
    }

    private void longOverflow(String value) {
        Throwable thrownException = catchThrowable( () -> toLong(new BigDecimal(value)));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("Numeric overflow: cannot convert .* of type \\[.*\\] to \\[.*\\]")
                .hasNoCause();
    }

    @Test
    public void testToByte() {
        byteOverflow(Long.MAX_VALUE);
        byteOverflow(Long.MIN_VALUE);
    }

    private void byteOverflow(Number value) {
        Throwable thrownException = catchThrowable( () -> toByte(value));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("Numeric overflow: cannot convert .* of type \\[.*\\] to \\[.*\\]")
                .hasNoCause();
    }

    
    @Test 
    public void testConstructor() {
        assertPrivateConstructor(ConversionUtils.class);
    }
    
}
