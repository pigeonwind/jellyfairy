package jerry.codetraining.ch1;

import java.text.NumberFormat;
import java.util.Locale;

class SimpleTipCalculator {
    private static final float FLOAT_100=100f;
    private int bill, percentage;
    private float tip, total;
    private final NumberFormat format;
    public SimpleTipCalculator() {
        format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        bill=0;
        percentage=0;
    }
    void input(Object bill, Object percentage) {
        inputBillAmount(bill);
        inputPercentage(percentage);
    }

    public SimpleTipCalculator inputBillAmount(Object bill) {
        isValid(bill);
        this.bill = (Integer) bill;
        return this;
    }
    public SimpleTipCalculator inputPercentage(Object percentage) {
        isValid(percentage);
        this.percentage =  (Integer)percentage;
        return this;
    }
    private void isValid(Object object) {
        if(! (object instanceof Integer)){
            throw new IllegalArgumentException("Please enter a valid number.");
        }
    }
    SimpleTipCalculator process() {
        this.tip = bill*(percentage/FLOAT_100);
        this.total = bill + tip;
        return this;
    }
    String output() {
        return String.format("Tip: %s\nTotal: %s", getTip(), getTotal());
    }
    String getTip() {
        return format.format(roundUp(tip));
    }
    String getTotal() {
        return format.format(roundUp(total));
    }

    float roundUp(float targetNumber) {
        return ( (int) (targetNumber*FLOAT_100+0.5f))/FLOAT_100;
    }
}
