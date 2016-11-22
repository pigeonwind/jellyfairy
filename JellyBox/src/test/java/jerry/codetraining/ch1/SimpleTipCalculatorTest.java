package jerry.codetraining.ch1;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleTipCalculatorTest {
	int bill,percentage;
	String tip,total,output;
	SimpleTipCalculator calculator;
	@Before
	public void setUp() throws Exception {
		bill=200;
		percentage=15;
		tip="$30.00";
		total="$230.00";
		
		StringBuilder result = new StringBuilder();
		result.append("Tip: ");result.append(tip);result.append("\n");
		result.append("Total: ");result.append(total);
		output=result.toString();
		calculator = new SimpleTipCalculator();
		calculator.input(bill,percentage);
		calculator.process();
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void getTipTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getTipTest");
		// given
		String expected=tip;
		// when
		String actual=calculator.getTip();
		// then
		assertThat(actual, is(expected));
	}
	
	@Test
	public void getTotalTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getTotalTest");
		// given
		String expected=total;
		// when
		String actual=calculator.getTotal();
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void outputTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "outputTest");
		// given
		String expected=output;
		// when
		String actual=calculator.output();
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void roundUpTest() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "roundUpTest");
		// given
		String expected=output;
		// when
		String actual=calculator.output();
		System.out.println(expected);
		System.out.println(actual);
		// then
		assertThat(actual, is(expected));
		
	}
	

}
