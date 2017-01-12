package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import com.jerry.parser.IHSLogDataLineParser;
import com.jerry.parser.ParserFactory;
import com.jerry.parser.WASSystemOutLogLineParser;
import com.jerry.util.function.Parser;

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
 * Created by jerryDev on 2017. 1. 2..
 */
public class FileLogCollectorTest {
    private String logDirectory;
    private Service service;
    @Before
    public void setUp() throws Exception {
        logDirectory = System.getProperty("user.dir")+"/testResource/";
        service = new FileLogCollector();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void ihsCallServiceTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "ihsCallServiceTest" );
        String fileName =  "access-SEC-test2.log";
        String testFilePath = logDirectory+fileName;
       
        Map<String,Object> requestParmameterMap = new HashMap<>(  );
        {
            requestParmameterMap.put( FileLogCollector.REQUEST_PARAM_FILEPATH, testFilePath );
            requestParmameterMap.put( FileLogCollector.REQUEST_PARAM_PARSERNAME, ParserFactory.PARSERNAME_IHS_LOG );
        }

        // given
        Object expected = null;
        {
        	Parser parser = new IHSLogDataLineParser(fileName);
        	try(Stream<String> lineStream = Files.lines( Paths.get(testFilePath), Charset.defaultCharset())){
        		expected = lineStream.parallel().map(parser::parse ).collect( Collectors.toList());
        	}
        }

        // when
        Object actual = service.call( requestParmameterMap);
        // then
        assertThat( actual, is( expected ) );

    }
    @Test
    public void getWasSystemOut_byFileNameTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "getWasSystemOut_byFileName" );
        // given
        String fileName =  "MES2.App02_SystemOut.log";
        String testFilePath = logDirectory+fileName;
        Predicate<String> lineFilterAtFirstCharIsSlash=(String line)-> line.regionMatches( 0,"/",0,1 );
        Map<String,Object> requestParmameterMap = new HashMap<>(  );
        {
            requestParmameterMap.put( FileLogCollector.REQUEST_PARAM_FILEPATH, testFilePath );
            requestParmameterMap.put( FileLogCollector.REQUEST_PARAM_PARSERNAME, ParserFactory.PARSERNAME_WAS_LOG );
            requestParmameterMap.put( FileLogCollector.REQUEST_PARAM_FILTER, lineFilterAtFirstCharIsSlash );
        }
        Object expected = null;
        Parser parser = new WASSystemOutLogLineParser( testFilePath );
        try(Stream<String> lineStream = Files.lines( Paths.get(testFilePath), Charset.defaultCharset())){
            List<Object> parsedLines = lineStream.parallel().filter(lineFilterAtFirstCharIsSlash).map(parser::parse ).collect( Collectors.toList());
            expected = parsedLines;
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        // when
        Object actual = service.call( requestParmameterMap);
        
        // then
        assertThat( actual, is( expected ) );
    }
}
