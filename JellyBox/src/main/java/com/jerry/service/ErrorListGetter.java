package com.jerry.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**s
 * Created by jerryDev on 2016. 12. 28..
 */
public class ErrorListGetter implements  Service{

    public static final String REQUEST_PARAM_INPUT_LIST = "collectedResult";
	public static final String REQUEST_PARAM_FILTER = "filter";
	public static final String REQUEST_PARAM_MAPPER = "mapper";

    @Override
    public Object call(Map<String, Object> requestObject) {
        List<Map<String,Object>> list = (List<Map<String, Object>>) requestObject.get( REQUEST_PARAM_INPUT_LIST );
        Predicate<Map<String,Object>> filter = (Predicate<Map<String, Object>>) requestObject.get(REQUEST_PARAM_FILTER);
        Function<Map<String, Object>, Map<String, Object>> mapper = (Function<Map<String, Object>, Map<String, Object>>) requestObject.get(REQUEST_PARAM_MAPPER);
        return list.stream().parallel().filter( filter ).map( mapper ).distinct().collect( Collectors.toList() );
    }
}
