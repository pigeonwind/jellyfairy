package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import com.jerry.parser.IHSLogDataParser;
import com.jerry.util.function.StringParser;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jerryDev on 2017. 1. 2..
 */
public class IHSLogCollectorTest {
    private List<Map> dataList;
    private String fileName;
    private String filePath;

    @Before
    public void setUp() throws Exception {
        fileName="access-SEC-test.log";
        filePath=System.getProperty("user.dir")+"/testResource/"+fileName;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void callServiceTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "callService" );
        Service errorListService = new ErrorListGetter();

        Map<String,Object> requestParmameterMap = new HashMap<>(  );
        {
            requestParmameterMap.put( FileLogCollector.FILE_LOG_COLLECTOR_REQUEST_PARAM_FILENAME, filePath );
        }

        // given
        Object expected = null;
        {
        	StringParser parser = new IHSLogDataParser(fileName);
        	try(Stream<String> lineStream = Files.lines( Paths.get(filePath), Charset.defaultCharset())){
        		expected = lineStream.parallel().map(parser::parseString).peek(System.out::println).collect( Collectors.toList());
        	}
        }

        // when
        Object actual = errorListService.call( requestParmameterMap);
        // then
        assertThat( actual, is( expected ) );

    }
}
