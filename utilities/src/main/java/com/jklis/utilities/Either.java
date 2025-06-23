package com.jklis.utilities;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Either<L, R> {

    private static final String VL_NT_EXST_MSG = "Requested value isn't set";

    private L left;
    private R right;
    
    private Either() {
    }
    
    public Either(R right) {
        this.right = right;
    }

    public L left() {
        return getValue(left);
    }

    public R right() {
        return getValue(right);
    }
    
    public boolean isLeft() {
        return Objects.nonNull(left);
    }
    
    public boolean isRight() {
        return Objects.nonNull(right);
    }

    private <C> C getValue(C value) {
        if (Objects.isNull(value)) {
            throw new NoSuchElementException(VL_NT_EXST_MSG);
        }
        return value;
    }
    
    public static <L, R> Either<L, R> withLeft(L left) {
        Either<L,R> e = new Either<>();
        e.left = left;
        return e;
    }

    public static <L, R> Either<L, R> withRight(R right) {
        Either<L,R> e = new Either<>();
        e.right = right;
        return e;
    }


}
