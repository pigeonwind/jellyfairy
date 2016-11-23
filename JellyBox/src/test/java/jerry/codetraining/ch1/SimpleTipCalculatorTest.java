package jerry.codetraining.ch1;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SimpleTipCalculatorTest {
	private String tip;
	private String total;
	private String output;
	private SimpleTipCalculator calculator;

	@Before
	public void setUp() throws Exception {
		int bill = 200;
		int percentage = 15;
		tip = "$30.00";
		total = "$230.00";
		StringBuilder result = new StringBuilder();
		result.append("Tip: ");
		result.append(tip);
		result.append("\n");
		result.append("Total: ");
		result.append(total);
		output = result.toString();
		calculator = new SimpleTipCalculator();
		calculator.input(bill, percentage);
		calculator.process();
	}

	@Test
	public void getTipTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getTipTest");
		// given
		String expected = tip;
		// when
		String actual = calculator.getTip();
		// then
		assertThat(actual, is(expected));
	}

	@Test
	public void getTotalTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getTotalTest");
		// given
		String expected = total;
		// when
		String actual = calculator.getTotal();
		// then
		assertThat(actual, is(expected));
	}

	@Test
	public void outputTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "outputTest");
		// given
		String expected = output;
		// when
		String actual = calculator.output();
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}

	@Test
	public void roundUpTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "roundUpTest");
		// given
		float roundedNum = 10.007f;
		float expected = 10.01f;
		// when
		float actual = calculator.roundUp(roundedNum);
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}

	@Test
	public void roundDownTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "roundDownTest");
		// given
		float roundedNum = 10.004f;
		float expected = 10.00f;
		// when
		float actual = calculator.roundUp(roundedNum);
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}

	@Test(expected = IllegalArgumentException.class)
	public void expectTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "expectTest");

		// given
		String bill="100";
		int percentage=15;
		// when
		calculator.input(bill,percentage);
		// then
	}
}
