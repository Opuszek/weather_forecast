package com.jklis.utilities;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Utilities {
    
    public static <T, Y> List<T> getLeftEithers(Collection<Either<T, Y>> eithers) {
        return eithers.stream()
                .filter(Either::isLeft)
                .map(Either::left)
                .collect(Collectors.toList());
    }

    public static <T, Y> List<Y> getRightEithers(Collection<Either<T, Y>> eithers) {
        return eithers.stream()
                .filter(Either::isRight)
                .map(Either::right)
                .collect(Collectors.toList());
    }

}
