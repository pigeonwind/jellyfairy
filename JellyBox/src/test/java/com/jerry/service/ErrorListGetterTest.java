package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import java.util.List;
import java.util.Map;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
public class ErrorListGetterTest {
    private List<Map> dataList;
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void callServiceTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "callService" );
        Service errorListService = new ErrorListGetter();
        Object request=null;
        // given
        Object expected = null;

        // when
        Object actual = errorListService.call( request);
        // then
        assertThat( actual, is( expected ) );

    }

}
