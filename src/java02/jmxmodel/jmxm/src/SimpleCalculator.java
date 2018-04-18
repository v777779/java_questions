package java02.jmxmodel.jmxm.src;

public class SimpleCalculator {

    public int add(final int augend, final int addend) {
        return augend + addend;
    }

    public int subtract(final int minuend, final int subtrahend) {
        return minuend - subtrahend;
    }

    public int multiply(final int factor1, final int factor2) {
        return factor1 * factor2;
    }

    public double divide(final int dividend, final int divisor) {
        return dividend / divisor;
    }
}