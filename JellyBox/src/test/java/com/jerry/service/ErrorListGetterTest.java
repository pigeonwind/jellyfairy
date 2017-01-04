package com.jerry.service;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.json.simple.JSONObject;
import org.junit.*;

import com.jerry.parser.ParserFactory;
import com.sun.javafx.scene.paint.GradientUtils.Parser;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
public class ErrorListGetterTest {
	private String logDirectory;

	@Before
	public void setUp() throws Exception {
		logDirectory = System.getProperty("user.dir") + "/testResource/";

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void errorListByDateByServerCallServiceTest() throws Exception {
		out.printf("========= %sTest() START =========\n", "errorListByDateByServerCallServiceTest");
		Service errorListService = new ErrorListGetter();
		Service logCollectService = new FileLogCollector();
		LocalDate startDate = LocalDate.of(2016, 10, 15);
		LocalDate endDate = LocalDate.of(2016, 10, 17);
		HashMap<String, Object> requestParmameterMap = new HashMap<>();
		String filePath = logDirectory + "MES2.App02_SystemOut.log";
		Predicate<String> lineFilterAtFirstCharIsSlash = (String line) -> line.regionMatches(0, "/", 0, 1);
		{
			requestParmameterMap.put(FileLogCollector.REQUEST_PARAM_FILEPATH, filePath);
			requestParmameterMap.put(FileLogCollector.REQUEST_PARAM_PARSERNAME, ParserFactory.PARSERNAME_WAS);
			requestParmameterMap.put(FileLogCollector.REQUEST_PARAM_FILTER, lineFilterAtFirstCharIsSlash);
		}
		
		List<Map<String, Object>> selectResult = (List<Map<String, Object>>) logCollectService.call(requestParmameterMap);
		Map<String, Object> requestParmameterMap_errorList = new HashMap<>();
		
		Predicate<Map<String, Object>> filter = (Map<String, Object> data) -> {
			LocalDate targetDate = ((LocalDate) data.get("date"));
			boolean result = targetDate.isEqual(startDate)
					|| (targetDate.isAfter(startDate) && targetDate.isBefore(endDate)) || (targetDate.isEqual(endDate));
			return result;
		};
		Function<Map<String,Object>, Map<String, Object>> mapper =(Map<String, Object> map) -> {
			Map<String, Object> json = new JSONObject();
			json.put("serverName", map.get("serverName"));
			json.put("code", map.get("code"));
			json.put("date", map.get("date"));
			return json;
		};
		{
			requestParmameterMap_errorList.put(ErrorListGetter.REQUEST_PARAM_INPUT_LIST, selectResult);
			requestParmameterMap_errorList.put(ErrorListGetter.REQUEST_PARAM_FILTER, filter);
			requestParmameterMap_errorList.put(ErrorListGetter.REQUEST_PARAM_MAPPER, mapper);
		}
		// given
		Object expected = selectResult.stream().parallel().filter(filter).map( mapper).distinct().peek(System.out::println).collect(Collectors.toList());
		// when
		Object actual = errorListService.call(requestParmameterMap_errorList);
		// then
		assertThat(actual, is(expected));
	}

}
