import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.Gnidskiy.ExpressionSolver;

public class ExpressionSolverTest {
    @Test
    void testExpressions() {
        String[] expressions = new String[] {
            "123",
            "12 + 5",
            "2 * 3 + 4 * (1 + 2)",
            "sqrt(abs(-25))! + q",
            "a + b + c * d + a"
        };

        ArrayList<HashMap<String, Double>> variables = new ArrayList<>();
        variables.add(null);
        variables.add(null);
        variables.add(null);

        variables.add(new HashMap<String, Double>() {{
            put("q", 10.0);
        }});

        variables.add(new HashMap<String, Double>() {{
            put ("a", 1.5);
            put ("b", 4.0);
            put ("c", 2.0);
            put ("d", 3.0);
        }});

        double[] results = new double[] {
            123.0,
            17.0,
            18.0,
            130.0,
            13.0
        };

        for (int i = 0; i < expressions.length; ++i) {
            assertEquals(new ExpressionSolver(expressions[i], variables.get(i)).solve(), results[i]);
        }
    }
}
