package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by jerryDev on 2017. 1. 2..
 */
public class IHSLogCollectorTest {
    private List<Map> dataList;
    private String fileName;
    private String filePath;

    @Before
    public void setUp() throws Exception {
        fileName="MES2.App02_SystemOut.log";
        filePath=System.getProperty("user.dir")+"/testResource/"+fileName;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void callServiceTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "callService" );
        Service errorListService = new ErrorListGetter();
        Predicate<String> lineFilterAtFirstCharIsSlash= (String line)->line.regionMatches( 0,"",0,1 );

        Map<String,Object> requestParmameterMap = new HashMap<>(  );
        {
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILENAME, filePath );
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILTER, lineFilterAtFirstCharIsSlash );
        }

        // given
        Object expected = null;
        {

        }

        // when
        Object actual = errorListService.call( requestParmameterMap);
        // then
        assertThat( actual, is( expected ) );

    }
}
