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
		output =  String.format("Tip: %s\nTotal: %s",tip,total);
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
		String actual = calculator.toString();
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void roundUpTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "roundUpTest");
		// given
		double roundedNum = 10.007d;
		double expected = 10.01d;
		// when
		double actual = calculator.roundUp(roundedNum);
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void roundDownTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "roundDownTest");
		// given
		double roundedNum = 10.004d;
		double expected = 10.00d;
		// when
		double actual = calculator.roundUp(roundedNum);
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}
	@Test(expected = RuntimeException.class)
	public void wrongInputParameterStringValueExpectExceptionTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "wrongInputParameterStringValueExpectExceptionTest");
		// given
		String bill="100";
		int percentage=15;
		// when
		calculator.input(bill,percentage);
		// then
	}
	@Test(expected = RuntimeException.class)
	public void wrongInputParameterMinusValueExpectExceptionTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "wrongInputParameterMinusValueExpectExceptionTest");
		// given
       int minusValue = -100;
		// when
		calculator.inputBillAmount(minusValue);
		// then
	}
}
