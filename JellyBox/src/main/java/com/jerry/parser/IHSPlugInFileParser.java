package com.jerry.parser;

import com.jerry.util.function.Parser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by jerryDev on 2017. 1. 10..
 */
public class IHSPlugInFileParser implements Parser {

    public static final String ELEMENTNAME_URIGROUP="UriGroup";
    public static final String ELEMENTNAME_CLUSTER="ServerCluster";
    public static final String ELEMENTNAME_SERVER="Server";


    public static final String SERVER_CLUSTER_MAP_LIST = "serverClusterMapList";
    public static final String CLUSTER_URIGROUP_MAP_LIST = "clusterUriGroupMapList";
    public static final String URIGROUP_URI_MAP_LIST = "uriGroupUriMapList";

    private final String pluginFileName;

    public IHSPlugInFileParser(String pluginFileName) {

        this.pluginFileName=pluginFileName;    
    }
    @Deprecated
    @Override
    public Object parse(String target) {
        throw new RuntimeException( ErrorMessage.ERR_MSG_THIS_MEHTODE_NOT_IMPLEMENTS );
    }

    @Override
    public Object parse() {
        {// file load
            InputSource inputSource;{
            try {
                inputSource = new InputSource( new FileReader( pluginFileName ) );
            } catch (FileNotFoundException e) {
                throw new RuntimeException( ErrorMessage.getErrorMessage( ErrorMessage.FILE_NOT_FOUND,pluginFileName ) ,e );
            }
            Document document;{
                try {
                    document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(  inputSource );
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        }
        return null;
    }
}
