package org.locationtech.jts.geomgraph;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public abstract class EdgeVisitor {

  public void applyTo(Geometry geom, int index) {
    if (geom instanceof Polygon)
      applyToPolygon((Polygon) geom, index);
    // LineString also handles LinearRings
    else if (geom instanceof LineString)
      applyToLineString((LineString) geom, index);
    else if (geom instanceof Point) {
      // do nothing with points for now
    } 
    else if (geom instanceof GeometryCollection)
      applyToCollection((GeometryCollection) geom, index);
    else
      throw new UnsupportedOperationException(geom.getClass().getName());
  }

  private void applyToCollection(GeometryCollection geom, int index) {
    for (int i = 0; i < geom.getNumGeometries(); i++) {
      Geometry element = geom.getGeometryN(i);
      applyTo(element, index);
    }
  }
  
  private void applyToPolygon(Polygon p, int index) {
    applyToPolygonRing((LinearRing) p.getExteriorRing(), index, Location.EXTERIOR, Location.INTERIOR);

    for (int i = 0; i < p.getNumInteriorRing(); i++) {
      LinearRing hole = (LinearRing) p.getInteriorRingN(i);

      // Holes are topologically labelled opposite to the shell, since
      // the interior of the polygon lies on their opposite side
      // (on the left, if the hole is oriented CW)
      applyToPolygonRing(hole, index, Location.INTERIOR, Location.EXTERIOR);
    }
  }

  private void applyToPolygonRing(LinearRing ring, int index, int cwLeft, int cwRight) {
    // don't visit empty rings
    if (ring.isEmpty()) return;

    int left  = cwLeft;
    int right = cwRight;
    // TODO: use coordinate sequence instead
    if (Orientation.isCCW(ring.getCoordinates())) {
      left = cwRight;
      right = cwLeft;
    }
    visit(ring, new Label(index, Location.BOUNDARY, left, right));
  }

  private void applyToLineString(LineString g, int index) {
    // don't visit empty rings
    if (g.isEmpty()) return;
    visit(g, new Label(index, Location.INTERIOR));
  }


  protected abstract void visit(LineString edge, Label label);
}
