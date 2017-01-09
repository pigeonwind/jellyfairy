package com.jerry.parser;

import static java.lang.System.out;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.jerry.util.function.StringParser;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.FileReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jerryDev on 2017. 1. 7..
 */
public class IHSPluginXmlParserTest {
    private String ihsPluginFilePath;

    @Before
    public void setUp() throws Exception {
        ihsPluginFilePath = System.getProperty("user.dir")+"/testResource/"+"plugin-cfg-gerp.xml";
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void xmlParsing_IhsWasMap_Test() throws Exception {
        out.printf( "========= %sTest() START =========\n", "xmlParsing_IhsWasMap_" );
        // given

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


        BiFunction<String,String,List<String>> getList = (expression,attrName) -> {
            ArrayList<String> list = new ArrayList<>(  );
            NodeList nodeList = compileExpression.andThen( toNodeListFunction ).apply( expression );
            for (int index = 0; index < nodeList.getLength(); index++) {
                list.add( nodeList.item( index ).getAttributes().getNamedItem(attrName).getTextContent() );
            }
            return list;
        };
        List<String> serverList;
        {// get ServerList
            String attributeName="Name";
            String expression = "//*/ServerCluster/Server";
            serverList = getList.apply( expression,attributeName );
            out.println(serverList);
        }// ihs-was map
        String ihsNameFilter = "-(.*?)\\.xml";

        List<String> clusterList;
        {// was-server map
            //route >> clusterName -  UrigroupName
            String expression = "//*/ServerCluster";
            String attrName = "Name";
            clusterList =  getList.apply( expression,attrName );
            out.println(clusterList);
        }
        List<Map<String,String>> uriGorupList;
        {// uriGroup
            String attrName="UriGroup";
//            String expression =String.format("//*/Route[@ServerCluster=%s]",serverClusterValue);
            Function<String,String> configureExpression = str->String.format("//*/Route[@%s=\"%s\"]","ServerCluster",str);

            Function<String,String> getUriGroupFunction =configureExpression.andThen(compileExpression).andThen( toNodeFunction )
                                                .andThen( node->node.getAttributes().getNamedItem(attrName).getTextContent() )
                                                ;
            Function<String,Map<String,String>> mapFunction = serverCluster->{
                HashMap<String,String> reulst = new HashMap<>(  );
                reulst.put( "ServerCluster",serverCluster );
                reulst.put( "uriGroup",getUriGroupFunction.apply( serverCluster ) );

                return reulst;
            };

            uriGorupList=clusterList.stream().peek( out::println )
                                            .map( mapFunction )
                                            .peek( out::println )
                                            .collect( Collectors.toList() );
        }

        Object expected = serverList.stream().map( (serverName)->
                                                    {Map<String,Object> inputDataMap = new HashMap<>(  );
                                                    inputDataMap.put( "wasServerName",serverName );
                                                    inputDataMap.put("ihsServerName", DefaultParser.REGEX_PARSE_OPERATOR.parse( ihsPluginFilePath,ihsNameFilter ));
                                                    return inputDataMap    ;} ).collect( Collectors.toList() );


        // when
        Object actual = null;
        // then
        assertThat( actual, is( expected ) );

    }


}
