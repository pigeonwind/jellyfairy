package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.json.simple.JSONObject;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
public class ErrorListGetterTest {
    private List<Map> dataList;
    private String fileName;
    private String filePath;
    private Map requestParmameterMap;

    @Before
    public void setUp() throws Exception {
        fileName="MES2.App02_SystemOut.log";
//        fileName="SystemOut.log";
        filePath=System.getProperty("user.dir")+"/testResource/"+fileName;
        requestParmameterMap = new HashMap<>(  );
        Predicate<String> lineFilterAtFirstCharIsSlash= (String line)->line.regionMatches( 0,"/",0,1 );
        {
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILENAME, filePath );
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILTER, lineFilterAtFirstCharIsSlash );
        }

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void callServiceTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "callService" );
        Service errorListService = new ErrorListGetter();
        Service selectWASSystemOutLog = new WASSystemOutLogCollector();
        List<Map<String,Object>> selectResult = (List<Map<String, Object>>) selectWASSystemOutLog.call( requestParmameterMap );

        Map<String, Object> requestParmameterMap_errorList = new HashMap<>(  );
        {
            requestParmameterMap_errorList.put( ErrorListGetter.REQUEST_PARAM_WAS_SYSTEM_OUT_LOG_COLLECTED_RESULT, selectResult );
            requestParmameterMap_errorList.put( ErrorListGetter.REQUEST_PARAM_START_DATE, selectResult );
            requestParmameterMap_errorList.put( ErrorListGetter.REQUEST_PARAM_END_DATE, selectResult );

        }
        Object expected = selectResult.stream().parallel().map( (Map map)-> {JSONObject json =new JSONObject(); json.put( "serverName",map.get("serverName") ); json.put( "code",map.get("code") ); ;return json;} ).distinct().peek( System.out::println ).collect( Collectors.toList() );
        // given
        // when
        Object actual = errorListService.call( requestParmameterMap_errorList);
        // then
        assertThat( actual, is( expected ) );

    }

}
