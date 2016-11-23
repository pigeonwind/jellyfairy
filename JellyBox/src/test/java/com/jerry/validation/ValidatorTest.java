package com.jerry.validation;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

/**
 * Created by jerryDev on 2016. 11. 23..
 */
public class ValidatorTest{
    Validator validator;
    @Before
    public void setUp() throws Exception {
        validator = new Validator(  new IsInteger() );
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void isInteagerTest() throws Exception {
        out.printf( "========= %sTest() START =========\n","isInteager" );
        // given
        Integer testData = Integer.valueOf( 10 );
        Object expected = testData instanceof Integer;
        // when
        Object actual = validator.validate( testData );
        // then
        assertThat( actual, is( expected ) );
    }
    @Test
    public void isNotInteagerTest() throws Exception {
        out.printf( "========= %sTest() START =========\n","isInteager" );
        // given
        Object testData = "test";
        Object expected = testData instanceof Integer;
        // when
        Object actual = validator.validate( testData );
        // then
        assertThat( actual, is( expected ) );
    }

    @Test
    public void isNegativeIntegerTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "isNegativeInteger" );
        // given
        Object testData = Integer.valueOf( - 10 );
        Object expected = testData instanceof Integer && (Integer)testData >0;
        validator = new Validator(  new IsPositiveInteger());
        // when
        Object actual = validator.validate( testData );
        // then
        assertThat( actual, is( expected ) );

    }
}
