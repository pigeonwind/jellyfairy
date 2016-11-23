package jerry.codetraining.ch1;

import com.jerry.util.function.IntegerParser;
import com.jerry.validation.Validator;

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
    private Validator inteagerValidator, positiveValidator;

    public SimpleTipCalculator() {
        format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        bill=0;
        percentage=0;
        integerParser = (Object object)-> (Integer)object;
        roundUpOperator = (double floatNum) -> ( (int) (floatNum*DOUBLE_100+0.5d))/DOUBLE_100;
        inteagerValidator = new Validator( (Object target)-> target instanceof Integer );
        positiveValidator = new Validator( (Object target)-> (Integer)target >= 0 );
    }
    void input(Object bill, Object percentage) throws Exception {
        this.bill = parseInt( bill, integerParser);
        this.percentage = parseInt( percentage, integerParser);
    }
    public Integer parseInt(Object object ,IntegerParser parser) throws Exception{
        if(inteagerValidator.validate( object ) && positiveValidator.validate( object ) ) {
            return parser.parseInt( object );
        }else{
            throw new RuntimeException("Please enter a valid number.)");
        }
    }
    public SimpleTipCalculator inputBillAmount(Object bill) throws Exception {
        this.bill = parseInt( bill,integerParser );
        return this;
    }
    public SimpleTipCalculator inputPercentage(Object percentage) throws Exception {
        this.percentage = parseInt( percentage ,integerParser );
        return this;
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
