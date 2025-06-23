package com.jklis.utilities;

import static com.jklis.utilities.Utilities.getLeftEithers;
import static com.jklis.utilities.Utilities.getRightEithers;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.Test;

public class UtilitiesTest {

    private static Integer[] LEFT_VALUES = new Integer[]{12,13,14};
    private static String[] RIGHT_VALUES = new String[]{"right1","right_2","right_3"};

    @Test
    public void getLeftEithersReturnLeftEithers() {
        assertThat(getLeftEithers(createEithers()),
                containsInAnyOrder(
                        LEFT_VALUES[0],
                        LEFT_VALUES[1],
                        LEFT_VALUES[2]
                )
        );
    }
    
    @Test
    public void getRightEithersReturnRightEithers() {
        assertThat(getRightEithers(createEithers()),
                containsInAnyOrder(
                        RIGHT_VALUES[0],
                        RIGHT_VALUES[1],
                        RIGHT_VALUES[2]
                )
        );
    }
    
    
    private List<Either<Integer,String>>createEithers() {
        return Arrays.asList(
                Either.withLeft(LEFT_VALUES[0]),
                Either.withRight(RIGHT_VALUES[0]),
                Either.withLeft(LEFT_VALUES[1]),
                Either.withRight(RIGHT_VALUES[1]),
                Either.withLeft(LEFT_VALUES[2]),
                Either.withRight(RIGHT_VALUES[2])
        );
    }

}
