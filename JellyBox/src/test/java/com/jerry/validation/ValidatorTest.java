package com.jerry.validation;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

public class ValidatorTest{
    private Validator validator;
    @Before
    public void setUp() throws Exception {
        validator = new Validator(  new IsInteger() );
    }

    @Test
    public void isInteagerTest() throws Exception {
        out.printf( "========= %sTest() START =========\n","isInteager" );
        // given
        Integer testData = 10;
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
        Object testData = -10;
        Object expected = testData instanceof Integer && (Integer)testData >0;
        validator = new Validator(  new IsPositiveInteger());
        // when
        Object actual = validator.validate( testData );
        // then
        assertThat( actual, is( expected ) );

    }
}
