package jerry.codetraining.ch1;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public class SimpleTipCalculator {

    private int bill, percentage;
    private float tip, total;
    NumberFormat format;

    public SimpleTipCalculator() {
        format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    }

    public void input(int bill, int percentage) {
        this.bill = bill;
        this.percentage = percentage;
    }

    public void process() {
//		DoubleFunction<R>
        //((float) bill*(percentage/100f));
        this.tip = bill*(percentage/100f);
//		Function<float, float> calculate = (float a,float b)->a*b/100f;
        this.total = bill + tip;
    }

    public String output() {
        return String.format("Tip: %s\nTotal: %s", getTip(), getTotal());
    }

    public String getTip() {
        return format.format(tip);
    }

    public String getTotal() {
        return format.format(total);
    }
}
