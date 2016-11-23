package jerry.codetraining.ch1;

import com.jerry.util.function.IntegerParser;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.DoubleUnaryOperator;

class SimpleTipCalculator {
    private static final double DOUBLE_100 =100d;
    private int bill, percentage;
    private double tip, total;
    private final NumberFormat format;
    private IntegerParser integerParser;
    private DoubleUnaryOperator roundUpOperator;

    public SimpleTipCalculator() {
        format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        bill=0;
        percentage=0;
        integerParser = (Object object)-> (Integer)object;
        roundUpOperator = (double floatNum) -> ( (int) (floatNum*DOUBLE_100+0.5d))/DOUBLE_100;
    }
    void input(Object bill, Object percentage) throws Exception {
        this.bill = parseInt( bill, integerParser);
        this.percentage = parseInt( percentage, integerParser);
    }
    private void isValid(Object object) throws Exception {
        if( object instanceof Integer){
            if(((Integer) object)<0)
                trowException("Please enter a valid number (not minus).)");
        }else{
            trowException("Please enter a valid number.)");
        }
    }
    public Integer parseInt(Object object ,IntegerParser parser) throws Exception{
        isValid( object );
        return parser.parseInt(object);
    }
    public SimpleTipCalculator inputBillAmount(Object bill) throws Exception {
        this.bill = parseInt( bill,integerParser );
        return this;
    }
    public SimpleTipCalculator inputPercentage(Object percentage) throws Exception {
        this.percentage = parseInt( percentage ,integerParser );
        return this;
    }

    private void trowException(String errorMessge) throws Exception {
        throw new RuntimeException(errorMessge);
    }

    SimpleTipCalculator process() {
        this.tip = calculate( bill,percentage,(Integer a, Object b) -> a*((Integer) b/DOUBLE_100));
        this.total =calculate( bill,tip, (Integer a, Object b)-> a+(double)b );
        return this;
    }

    Double calculate(int number1, Object number2, BiFunction<Integer,Object,Double> biFunction){
        return biFunction.apply( number1,number2 );
    }
    @Override
    public String toString() {
        return String.format("Tip: %s\nTotal: %s", getTip(), getTotal());
    }
    String getTip() {
        return format.format(roundUpOperator.applyAsDouble(tip));
    }
    String getTotal() {
        return format.format(roundUpOperator.applyAsDouble(total));
    }
    double roundUp(double targetNumber ) {
        return roundUpOperator.applyAsDouble( targetNumber );
    }
}
