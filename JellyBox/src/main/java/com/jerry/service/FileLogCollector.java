package com.jerry.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jerry.parser.ParserFactory;
import com.jerry.parser.WASSystemOutLogParser;
import com.jerry.util.function.StringParser;

/**
 * Created by jerryDev on 2017. 1. 2..
 */
public class FileLogCollector implements Service{
    static final String FILE_LOG_COLLECTOR_REQUEST_PARAM_FILENAME="fileName";
    static final String FILE_LOG_COLLECTOR_REQUEST_PARAM_PARSERNAME="parserName";
    static final String FILE_LOG_COLLECTOR_REQUEST_PARAM_FILTER="filter";
    
    @Override
    public Object call(Map<?, ?> requestObject) {
    	
        String fileName = (String) requestObject.get( FILE_LOG_COLLECTOR_REQUEST_PARAM_FILENAME );
        String parserName = (String) requestObject.get( FILE_LOG_COLLECTOR_REQUEST_PARAM_PARSERNAME );
        Predicate<String> filter =  (Predicate<String>) requestObject.get( FILE_LOG_COLLECTOR_REQUEST_PARAM_FILENAME)wodu);
        
        Object result=null;

        try(Stream<String> lineStream = Files.lines( Paths.get(fileName), Charset.defaultCharset())){
            result= lineStream.parallel().filter( filter ).map(ParserFactory.create(parserName, fileName)::parseString).collect( Collectors.toList());
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
