package com.jklis.utilities;

import java.util.NoSuchElementException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class EitherTest {

    private static Integer LEFT = 12;
    private static String RIGHT = "right";

    @Test
    public void leftEitherThenReturnsLeftValue() {
        Either<Integer, String> e = Either.withLeft(LEFT);
        assertThat(e.left(), equalTo(LEFT));
    }

    @Test
    public void rightEitherReturnsRightValue() {
        Either<Integer, String> e = Either.withRight(RIGHT);
        assertThat(e.right(), equalTo(RIGHT));
    }

    @Test
    public void rightEitherReturnsTrueForIsRightAndFalseForIsLeft() {
        Either<Integer, String> e = Either.withRight(RIGHT);
        assertThat(e.isRight(), is(true));
        assertThat(e.isLeft(), is(false));
    }

    @Test
    public void leftEitherReturnsTrueForIsLeftAndFalseForIsRight() {
        Either<Integer, String> e = Either.withLeft(LEFT);
        assertThat(e.isLeft(), is(true));
        assertThat(e.isRight(), is(false));
    }

    @Test
    public void leftEitherThrowsNSEExceptionWhenAskedForRight() {
        Either<Integer, String> e = Either.withLeft(LEFT);
        assertThrows(NoSuchElementException.class, () -> {
            e.right();
        });
    }

    @Test
    public void rightEitherThrowsNSEExceptionWhenAskedForLeft() {
        Either<Integer, String> e = Either.withRight(RIGHT);
        assertThrows(NoSuchElementException.class, () -> {
            e.left();
        });
    }

}
