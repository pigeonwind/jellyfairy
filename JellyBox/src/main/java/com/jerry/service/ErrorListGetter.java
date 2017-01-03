package com.jerry.service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**s
 * Created by jerryDev on 2016. 12. 28..
 */
public class ErrorListGetter implements  Service{

    public static final String REQUEST_PARAM_WAS_SYSTEM_OUT_LOG_COLLECTED_RESULT = "collectedResult";
    public static final String REQUEST_PARAM_START_DATE = "startDate";
    public static final String REQUEST_PARAM_END_DATE = "endDate";

    @Override
    public Object call(Object requestObject) {
        List<Map<String,Object>> list = (List<Map<String, Object>>) ((Map<String,Object>)requestObject).get( REQUEST_PARAM_WAS_SYSTEM_OUT_LOG_COLLECTED_RESULT );
        Predicate<Map<String,Object>> dateFilter =null;

        return list.stream().parallel().filter( dateFilter ).map( (Map map)-> map.get( "code" ) ).distinct().collect( Collectors.toList() );
    }
}
