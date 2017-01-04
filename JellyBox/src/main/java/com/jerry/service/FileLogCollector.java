package com.jerry.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jerry.parser.ParserFactory;

/**
 * Created by jerryDev on 2017. 1. 2..
 */
public class FileLogCollector implements Service{
    static final String REQUEST_PARAM_FILEPATH="fileName";
    static final String REQUEST_PARAM_PARSERNAME="parserName";
    static final String REQUEST_PARAM_FILTER="filter";
    
    @Override
    public Object call(Map<String, Object> requestObject) {
    	
        String fileName = (String) requestObject.get( REQUEST_PARAM_FILEPATH );
        String parserName = (String) requestObject.get( REQUEST_PARAM_PARSERNAME );
        Optional<Predicate<String>> OptionalFilter = Optional.ofNullable((Predicate<String>)requestObject.get(REQUEST_PARAM_FILTER));
        Object result=null;
        try(Stream<String> lineStream = Files.lines( Paths.get(fileName), Charset.defaultCharset())){
            result= lineStream.parallel().filter( OptionalFilter.orElse((String line)->true)).map(ParserFactory.create(parserName, fileName)::parseString).collect( Collectors.toList());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
