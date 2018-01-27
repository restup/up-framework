package com.github.restup.bind.converter;

import static com.github.restup.bind.converter.StringToBooleanConverter.isTrue;
import static com.github.restup.bind.converter.StringToBooleanConverter.toBoolean;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.github.restup.test.assertions.Assertions.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StringToBooleanConverterTest {
    
    @Test
    public void testTrueValues() {
        assertTrue(isTrue(true));
        assertCaseInsensitive(true, "true");
        assertCaseInsensitive(true, "on");
        assertCaseInsensitive(true, "yes");
        assertCaseInsensitive(true, "1");
        assertCaseInsensitive(true, "t");
        assertCaseInsensitive(true, "y");
    }
    
    @Test
    public void testFalseValues() {
        assertFalse(isTrue(null));
        assertFalse(isTrue(false));
        
        assertCaseInsensitive(false, "false");
        assertCaseInsensitive(false, "off");
        assertCaseInsensitive(false, "no");
        assertCaseInsensitive(false, "0");
        assertCaseInsensitive(false, "n");
        assertCaseInsensitive(false, "f");
    }
    
    @Test
    public void testIllegalArgument() {
        illegalArgument(null);
        illegalArgument("");
        illegalArgument("nosir");
    }

    private void illegalArgument(String value) {
        Throwable thrownException = catchThrowable( () -> toBoolean(value));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(value + " is not a valid boolean value")
                .hasNoCause();
    }
    
    @Test 
    public void testConstructor() {
        assertPrivateConstructor(StringToBooleanConverter.class);
    }

    private static void assertCaseInsensitive(boolean expected, String value) {
        assertEquals(expected, isTrue(value.toLowerCase()));
        assertEquals(expected, isTrue(value.toUpperCase()));
        assertEquals(expected, toBoolean(value.toLowerCase()));
        assertEquals(expected, toBoolean(value.toUpperCase()));
    }

}
