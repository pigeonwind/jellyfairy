package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.jerry.parser.WASSystemOutLogParser;
import com.jerry.util.function.StringParser;
import org.junit.*;

import java.io.IOException;
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
 * Created by jerryDev on 2016. 12. 28..
 */
public class WASSystemOutLogCollectorTest {
    String fileName,filePath;
    @Before
    public void setUp() throws Exception {
        fileName="MES2.App02_SystemOut.log";
        filePath=System.getProperty("user.dir")+"/testResource/"+fileName;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getWasSystemOut_byFileNameTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "getWasSystemOut_byFileName" );
        // given
        String testFilePath = filePath;
        Predicate<String> lineFilterAtFirstCharIsSlash=(String line)-> line.regionMatches( 0,"/",0,1 );
        Map<String,Object> requestParmameterMap = new HashMap<>(  );
        {
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILENAME, filePath );
            requestParmameterMap.put( WASSystemOutLogCollector.WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILTER, lineFilterAtFirstCharIsSlash );
        }
        Object expected = null;
        StringParser parser = new WASSystemOutLogParser( testFilePath );
        try(Stream<String> lineStream = Files.lines( Paths.get(testFilePath), Charset.defaultCharset())){
            List<Object> actualParsedLines = lineStream.parallel().filter(lineFilterAtFirstCharIsSlash).map(parser::parseString).peek( out::println ).collect( Collectors.toList());
            expected = actualParsedLines;
        }catch (IOException e) {
            e.printStackTrace();
        }
        Service service = new WASSystemOutLogCollector();
        // when
        Object actual = service.call( requestParmameterMap);
        // then
        assertThat( actual, is( expected ) );
    }
}
