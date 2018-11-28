package org.locationtech.jts.operation.overlay;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;

import test.jts.GeometryTestCase;
import test.jts.TestFiles;

import java.nio.file.Files;
import java.nio.file.Paths;

public class OverlaySnapRoundingTest extends GeometryTestCase {

  public OverlaySnapRoundingTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(OverlaySnapRoundingTest.class);
  }
  
  public void testCrossingNarrowGap() {
    Geometry a = read("POLYGON ((10 20, 22 20, 22 10, 10 10, 10 20))");
    Geometry b = read("MULTIPOLYGON (((20 5, 20 15, 30 15, 20 5)), ((20 25, 20 15.1, 30 15, 20 25)))");
    Geometry expected = read("POLYGON ((20 20, 22 20, 22 15, 22 10, 20 10, 20 15, 20 20))");
    checkIntersection(a, b, 1, expected );
  }

  public void testCrossingNarrowGapBis() {
    try {
      String filename = TestFiles.getResourceFilePath("intersection.wkt");
      String collection = new String(Files.readAllBytes(Paths.get(filename)));

      String[] polygons = collection.split("\n");
      Geometry union = read(polygons[0]);

      // When snapRounding is false (no precision model), the next exception is
      // raised (we have the same exception when a
      // ST_UnaryUnion(ST_Collect(geom)) operation is made in Postgis):
      //
      // org.locationtech.jts.geom.TopologyException: found non-noded
      // intersection
      // between LINESTRING ( 836866.2869999632 2111003.748004286,
      // 836865.4289999632 2111103.8340042857 ) and LINESTRING (
      // 836865.429 2111103.834, 836765.343 2111102.976 ) [ (836865.429,
      // 2111103.834, NaN) ]
      //
      // but when snapRounding is true (with precision model), the next
      // exception is raised:
      // org.locationtech.jts.geom.TopologyException: side location conflict
      // [ (835342.0, 2109489.0, NaN) ]
      boolean snapRounding = true;

      for ( String wkt : polygons ) {
	Geometry polygon = read(wkt);
        union = overlay(union, polygon, OverlayOp.UNION, 1, snapRounding );
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e);
    }
  }

  public void testUnionNarrowGap() {
    Geometry a = read("POLYGON ((20 5, 20 15, 30 15, 20 5))");
    Geometry b = read("POLYGON ((20 25, 20 15.1, 30 15, 20 25))");
    Geometry expected = read("POLYGON ((20 5, 20 15, 20 25, 30 15, 20 5))");
    checkOverlay(a, b, OverlayOp.UNION, 1, expected );
  }

  private Geometry overlay(Geometry a, Geometry b, int op, double scale, boolean snapRounding) {
    Geometry result;

    if ( snapRounding ) {
      PrecisionModel pm = new PrecisionModel(scale);
      result = OverlayOp.overlayOp(a, b, op, pm);
    } else {
      result = OverlayOp.overlayOp(a, b, op);
    }

    return result;
  }

  private void checkOverlay(Geometry a, Geometry b, int op, double scale, Geometry expected) {
    PrecisionModel pm = new PrecisionModel(scale);
    Geometry result = OverlayOp.overlayOp(a, b, op, pm);
    boolean isCorrect = expected.equalsExact(result);
    assertTrue(isCorrect);
  }
  
  private void checkIntersection(Geometry a, Geometry b, double scale, Geometry expected) {
    Geometry result = intersection(a, b, scale);
    boolean isCorrect = expected.equalsExact(result);
    if (! isCorrect) System.out.println("Result: " +result);
    assertTrue(isCorrect);
  }

  public static Geometry intersection(Geometry a, Geometry b, double scaleFactor) {
    PrecisionModel pm = new PrecisionModel(scaleFactor);
    Geometry result = OverlayOp.overlayOp(a, b, OverlayOp.INTERSECTION, pm);
    return result;
  }

}
