package com.jerry.service;

import com.jerry.parser.WASSystemOutLogParser;
import com.jerry.util.function.StringParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
public class WASSystemOutLogCollector implements Service {
    static final String WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILENAME="fileName";
    static final String WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILTER="filter";
    @Override
    public Object call(Object requestObject) {
        Map<String,Object> requestParms = (Map<String, Object>) requestObject;
        String fileName = (String) requestParms.get( WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILENAME );
        Predicate<String> filter = (Predicate<String>) requestParms.get( WAS_SYSTEM_OUT_LOG_COLLECTOR_REQUEST_PARAM_FILTER);

        StringParser parser =new WASSystemOutLogParser( fileName );

        Object result=null;

        try(Stream<String> lineStream = Files.lines( Paths.get(fileName), Charset.defaultCharset())){
            result= lineStream.parallel().filter( filter ).map(parser::parseString).collect( Collectors.toList());
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
