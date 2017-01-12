package com.jerry.parser;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.jerry.util.function.Parser;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created by jerryDev on 2017. 1. 7..
 */
public class IHSPluginXmlParserTest {
    private String ihsPluginFilePath;
    private List<String> serverList,clusterList;
    private List<Map<String,String>> serverClusterMapList, clusterUriGroupList, uriGroupUriList;
    private Parser parser;
    @Before
    public void setUp() throws Exception {
        ihsPluginFilePath = System.getProperty("user.dir")+"/testResource/"+"plugin-cfg-gerp.xml";
        parser = ParserFactory.create( ParserFactory.PARSERNAME_IHS_PLUGIN,ihsPluginFilePath );
        initDummyData();
    }

    private void initDummyData() throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
    	InputSource intputSrource;{
            intputSrource= new InputSource( new FileReader( ihsPluginFilePath ) );
        }
        Document document;{
            document = DocumentBuilderFactory.newInstance(  ).newDocumentBuilder().parse( intputSrource );
        }
        XPath xPath;{
            xPath= XPathFactory.newInstance().newXPath();
        }
        Function<String,XPathExpression> compileExpression = (expression) -> {
            try {
                return xPath.compile( expression );
            } catch (XPathExpressionException e) {
                throw new RuntimeException( e );
            }
        };
        Function<XPathExpression,String> toStringFunction = xPathExpression -> {
            try {
                return (String)xPathExpression.evaluate( document, XPathConstants.STRING );
            } catch (XPathExpressionException e) {
                throw new RuntimeException( e );
            }
        };
        Function<XPathExpression,NodeList> toNodeListFunction = xPathExpression -> {
            try {
                return (NodeList) xPathExpression.evaluate( document, XPathConstants.NODESET );
            } catch (XPathExpressionException e) {
                throw new RuntimeException( e );
            }
        };
        Function<XPathExpression,Node> toNodeFunction = xPathExpression -> {
            try {
                return (Node) xPathExpression.evaluate( document, XPathConstants.NODE );
            } catch (XPathExpressionException e) {
                throw new RuntimeException( e );
            }
        };
        BiFunction<NodeList, String, List<String>> toStringList = (nodeList,attrName)->{
        	ArrayList<String> list = new ArrayList<>(  );
        	for (int index = 0; index < nodeList.getLength(); index++) {
                list.add( nodeList.item( index ).getAttributes().getNamedItem(attrName).getTextContent() );
            }
        	return list;
    	};
        BiFunction<String,String,List<String>> getList = (expression,attrName) -> {
            return toStringList.apply(compileExpression.andThen( toNodeListFunction ).apply( expression ), attrName);
        };
        {// get ServerList
            String attributeName="Name";
            String expression = "//*/ServerCluster/Server";
            serverList = getList.apply( expression,attributeName );
            System.out.println("=========== server list ===========");
            out.println(serverList);
        }// ihs-was map
        String ihsNameFilter = "-(.*?)\\.xml";
        {// was-server map
            String expression = "//*/ServerCluster";
            String attrName = "Name";
            clusterList =  getList.apply( expression,attrName );
            System.out.println("=========== cluster list ===========");
            out.println(clusterList);
        }
        {
        	String clusterExpressoin =  "//*/ServerCluster";
        	UnaryOperator<String> serverExpressionOperation = clusterName->String.format("//*/ServerCluster[@Name=\"%s\"]/Server",clusterName);
        	String attrName="Name";
        	Function<String,List<String>> getClusterList=compileExpression.andThen(toNodeListFunction)
        					 											  .andThen( nodeList->toStringList.apply(nodeList,attrName));
        	System.out.println("=========== cluster - server map list ===========");
        	serverClusterMapList = getClusterList.apply(clusterExpressoin).stream()
        																  .flatMap(serverCluster->{
        																							String serverExpression=serverExpressionOperation.apply(serverCluster);
        																							return getList.apply( serverExpression,attrName )
        																										  .stream()
        																										  .map(server->{Map<String,String> map=new HashMap<>();map.put("ServerCluster", serverCluster);map.put("Server", server);return map; }); } )
        			  													  .peek(out::println).collect(Collectors.toList());
        }
        BiFunction<Node,String,String> getNodeAttrContent= (node,attrName)-> node==null?"null":node.getAttributes().getNamedItem(attrName).getTextContent();
        {// uriGroup
            String attrName="UriGroup";
            Function<String,String> configureExpression = str->String.format("//*/Route[@%s=\"%s\"]","ServerCluster",str);
            Function<String,String> getUriGroupFunction =configureExpression.andThen(compileExpression)
            																.andThen( toNodeFunction )
            																.andThen( node-> getNodeAttrContent.apply(node, attrName) );
            Function<String,Map<String,String>> mapFunction = serverCluster->{
                HashMap<String,String> reulst = new HashMap<>(  );
                reulst.put( "ServerCluster",serverCluster );
                reulst.put( "UriGroup",getUriGroupFunction.apply( serverCluster ) );

                return reulst;
            };
            System.out.println("=========== uri group list ===========");
            clusterUriGroupList =clusterList.stream().map( mapFunction )
                                            .peek( out::println )
                                            .collect( Collectors.toList() );
        }
        {// uriGroups - uri
        	String attrName="Name";
        	UnaryOperator<String> uriConfigureExpression = uriGroupName->String.format("//*/UriGroup[@Name=\"%s\"]/Uri", uriGroupName);
        	Function<String,List<String>> getUriListFunction = uriConfigureExpression.andThen(compileExpression)
        																	   .andThen(toNodeListFunction)
        																	   .andThen( nodeList->toStringList.apply(nodeList,attrName));
        	BiFunction<String,String,Map<String,String>> toMap =(uriGroup,uri) ->{
        		HashMap<String,String> result=new HashMap<>();
        		result.put("UriGroup", uriGroup);
        		result.put("Uri", uri);
        		return result;
        	};
        	System.out.println("=========== uri list ===========");
        	uriGroupUriList = clusterUriGroupList.stream().flatMap( map-> {
        								String uriGrup = map.get("UriGroup");
        						  		return getUriListFunction.apply(uriGrup).stream().map(uri-> toMap.apply(uriGrup, uri));
        						  })
        						 .peek(out::println)
        						 .collect(Collectors.toList());
        }
        
	}

	@After
    public void tearDown() throws Exception {
		
    }

    @Test
    public void parseTest() throws Exception {
        out.printf( "========= %sTest() START =========\n", "parse" );
        // given
        //String moduleName ="testModule";
       //Object expected = uriGroupUriList.stream().filter(map->map.get("Uri").contains( moduleName )).map( map->map.get( "UriGroup" ) ).collect( Collectors.toList() );
        Map<String,Object> resultMap = new HashMap<>(  );

        resultMap.put( IHSPlugInFileParser.SERVER_CLUSTER_MAP_LIST,serverClusterMapList );
        resultMap.put( IHSPlugInFileParser.CLUSTER_URIGROUP_MAP_LIST, clusterUriGroupList );
        resultMap.put( IHSPlugInFileParser.URIGROUP_URI_MAP_LIST, uriGroupUriList );

        Object expected =resultMap;
        // when
        Object actual = parser.parse();
        // then
        assertThat( actual, is( expected ) );
    }
}
