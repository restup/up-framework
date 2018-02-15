package com.github.restup.assertions;

import static org.assertj.core.api.Assertions.catchThrowable;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.errors.RequestError;

public class Assertions {

    public static RequestErrorAssertions assertThat(RequestError error) {
        return new RequestErrorAssertions(error);
    }

    public static RequestErrorAssertions assertThat(RequestError.Builder b) {
        return assertThat(b.build());
    }

    public static <E extends Throwable> RequestErrorExceptionAssertions<?> assertThrows(ThrowingCallable f) {
        Throwable thrownException = catchThrowable(f);
        
        return new RequestErrorExceptionAssertions<>(thrownException);
    }

    public static <E extends Throwable> RequestErrorExceptionAssertions<?> assertThat(RequestErrorException ex) {
        return new RequestErrorExceptionAssertions<>(ex);
    }
    
    private Assertions() {
        super();
    }
}
