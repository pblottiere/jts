package org.locationtech.jts.operation.overlay;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;

import test.jts.GeometryTestCase;

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
    Geometry expected = read("POLYGON ((20 20, 22 20, 22 10, 20 10, 20 20))");
    checkIntersection(a, b, 1, expected );
  }

  public void testUnionNarrowGap() {
    Geometry a = read("POLYGON ((20 5, 20 15, 30 15, 20 5))");
    Geometry b = read("POLYGON ((20 25, 20 15.1, 30 15, 20 25))");
    Geometry expected = read("POLYGON ((20 5, 20 15, 20 25, 30 15, 20 5))");
    checkOverlay(a, b, OverlayOp.UNION, 1, expected );
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
    assertTrue(isCorrect);
  }

  
  public static Geometry intersection(Geometry a, Geometry b, double scaleFactor) {
    PrecisionModel pm = new PrecisionModel(scaleFactor);
    Geometry result = OverlayOp.overlayOp(a, b, OverlayOp.INTERSECTION, pm);
    return result;
  }

}
