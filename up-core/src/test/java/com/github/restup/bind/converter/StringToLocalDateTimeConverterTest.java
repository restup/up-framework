package com.github.restup.bind.converter;

import static com.github.restup.test.assertions.Assertions.assertThrows;
import static org.junit.Assert.assertEquals;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.Test;

public class StringToLocalDateTimeConverterTest {


    @Test
    public void testConvertLenient() {
        StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();
        LocalDateTime localDateTime = converter.apply("2015-06-05");
        assertEquals(LocalDateTime.of(2015, 6, 5, 0, 0), localDateTime);

        localDateTime = converter.apply("2015-06-05T09:25");
        assertEquals(LocalDateTime.of(2015, 6, 5, 9, 25), localDateTime);

        localDateTime = converter.apply("2015-06-05T09:25:23");
        assertEquals(LocalDateTime.of(2015, 6, 5, 9, 25, 23), localDateTime);
    }

    @Test
    public void testConvertStrict() {
        StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter(false);
        assertThrows(() -> converter.apply("2015-06-05"), DateTimeParseException.class)
        .hasNoCause();
    }

    @Test
    public void testAndThen() {
        StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();
        LocalDate localDate = converter.andThen(l -> l.toLocalDate()).apply("2015-06-05T09:25:23");
        assertEquals(LocalDate.of(2015, 6, 5), localDate);
    }
    
}
