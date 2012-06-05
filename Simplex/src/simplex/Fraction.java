package simplex;

public final class Fraction implements Comparable<Fraction> {

    public static final Fraction ZERO = new Fraction(0);
    public static final Fraction ONE = new Fraction(1);
    private int numerator_;
    private int denominator_;

    public static Fraction add(Fraction a, Fraction b) {
        return new Fraction(a.getNumerator() * b.getDenominator() + b.getNumerator() * a.getDenominator(), a.getDenominator() * b.getDenominator());
    }

    public static Fraction substract(Fraction a, Fraction b) {
        return add(a, b.opposite());
    }

    public static Fraction multiply(Fraction a, Fraction b) {
        return new Fraction(a.getNumerator() * b.getNumerator(), a.getDenominator() * b.getDenominator());
    }

    public static Fraction divide(Fraction a, Fraction b) {
        return multiply(a, b.inverse());
    }

    public Fraction(int numerator, int denominator) {
        setNumerator(numerator);
        setDenominator(denominator);
        simplify();
    }

    public Fraction(int number) {
        this(number, 1);
    }

    public Fraction() {
        this(0, 1);
    }

    public int getNumerator() {
        return numerator_;
    }

    public void setNumerator(int value) {
        numerator_ = value;
    }

    private int getDenominator() {
        return denominator_;
    }

    private void setDenominator(int value) {
        if (value == 0) {
            throw new ArithmeticException("divide by zero");
        }
        denominator_ = value;
    }

    public Fraction add(Fraction fraction) {
        setNumerator(getNumerator() * fraction.getDenominator() + fraction.getNumerator() * getDenominator());
        setDenominator(getDenominator() * fraction.getDenominator());
        simplify();
        return this;
    }

    public Fraction add(int number) {
        return add(new Fraction(number));
    }

    public Fraction substract(Fraction fraction) {
        return add(fraction.opposite());
    }

    public Fraction substract(int number) {
        return substract(new Fraction(number));
    }

    public Fraction multiply(Fraction fraction) {
        setNumerator(getNumerator() * fraction.getNumerator());
        setDenominator(getDenominator() * fraction.getDenominator());
        simplify();
        return this;
    }

    public Fraction multiply(int number) {
        return multiply(new Fraction(number));
    }

    public Fraction divide(Fraction fraction) {
        return multiply(fraction.inverse());
    }

    public Fraction divide(int number) {
        return divide(new Fraction(number));
    }

    public Fraction opposite() {
        return new Fraction(-getNumerator(), getDenominator());
    }

    public Fraction inverse() {
        return new Fraction(getDenominator(), getNumerator());
    }

    private void simplify() {
        if (getNumerator() == 0) {
            setNumerator(0);
            setDenominator(1);
        } else {
            if (getDenominator() < 0) {
                setNumerator(-getNumerator());
                setDenominator(-getDenominator());
            }
            int start = Math.abs(getNumerator());
            boolean done = false;
            while ((!done) && (start > 1)) {
                if (getNumerator() % start == 0) {
                    if (getDenominator() % start == 0) {
                        setNumerator(getNumerator() / start);
                        setDenominator(getDenominator() / start);
                        done = true;
                    }
                }
                start = start - 1;
            }
        }
    }

    private void amplify(int number) {
        if (number == 0) {
            setNumerator(0);
            setDenominator(1);
        } else {
            setNumerator(getNumerator() * number);
            setDenominator(getDenominator() * number);
        }
    }

    @Override
    public String toString() {
        if (getDenominator() == 1) {
            return getNumerator() + "";
        }
        return getNumerator() + "/" + getDenominator();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + numerator_;
        hash = 19 * hash + denominator_;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof Fraction) {
            Fraction fraction = (Fraction) object;
            return fraction.getNumerator() == getNumerator() && fraction.getDenominator() == getDenominator();
        }
        return false;
    }

    @Override
    public int compareTo(Fraction fraction) {
        amplify(fraction.getDenominator());
        fraction.amplify(getDenominator());
        int result = Integer.compare(getNumerator(), fraction.getNumerator());
        simplify();
        fraction.simplify();
        return result;
    }
}
