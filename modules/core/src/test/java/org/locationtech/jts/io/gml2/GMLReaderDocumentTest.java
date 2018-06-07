package org.locationtech.jts.io.gml2;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.xml.sax.SAXException;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import test.jts.GeometryTestCase;

public class GMLReaderDocumentTest extends GeometryTestCase  {
  
  static GeometryFactory geometryFactory = new GeometryFactory();
  
  public static void main(String args[]) {
    TestRunner.run(GMLReaderDocumentTest.class);
  }
  
  public GMLReaderDocumentTest(String arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }
  
  public void testDocumentMultiLineString() throws SAXException, IOException, ParserConfigurationException {
    String gml = 
    "<gml:featureMember>"
    +"<xxx:test fid='123'>"
    +"<xxx:geometryProperty>"
    +"<gml:MultiLineString srsName='EPSG:4326'><gml:lineStringMember><gml:LineString>"
    +"<gml:coordinates>1,1 2,2</gml:coordinates>"
    +"</gml:LineString></gml:lineStringMember></gml:MultiLineString>"
    +"</xxx:geometryProperty>"
    +"<xxx:ATTR1>123</xxx:ATTR1>"
    +"</xxx:test>"
    +"</gml:featureMember>";
    
    checkGeometry(gml, "MULTILINESTRING ((1 1, 2 2))");
  }

  private void checkGeometry(String gml, String wktExpected) throws SAXException, IOException, ParserConfigurationException {
    GMLReader gr = new GMLReader();
    Geometry g = gr.read(gml, geometryFactory);
    Geometry expected = read(wktExpected);
    
    System.out.println(g);
    checkEqual(g, expected);
  }
}
