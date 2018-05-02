package org.locationtech.jts.geomgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateList;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.noding.NodedSegmentString;
import org.locationtech.jts.noding.Noder;
import org.locationtech.jts.noding.snapround.MCIndexSnapRounder;

public class GeometrySRNoder {

  private PrecisionModel precisionModel;
  private List edges;
  private NoderEdgeVisitor edgeVisitor;
  
  public GeometrySRNoder(PrecisionModel precisionModel) {
    this.precisionModel = precisionModel;
    edges = new ArrayList();
    edgeVisitor = new NoderEdgeVisitor(edges, precisionModel);
  }
  
  public void add(Geometry g, int index) {
    edgeVisitor.applyTo(g, index);
  }
  
  public Collection node() {
    //Noder sr = new SimpleSnapRounder(pm);
    Noder sr = new MCIndexSnapRounder(precisionModel);
    sr.computeNodes(edges);
    return sr.getNodedSubstrings();
  }
}

class NoderEdgeVisitor extends EdgeVisitor {

  private List edges;
  private PrecisionModel pm;
  
  public NoderEdgeVisitor(List edges, PrecisionModel pm) {
    this.edges = edges;
    this.pm = pm;
  }

  public List getEdges() {
    return edges;
  }
  
  protected void visit(LineString edge, Label label) {
    Coordinate[] roundPts = round( ((LineString)edge).getCoordinateSequence(), pm);
    //TODO: what if seq is too short?
    edges.add(new NodedSegmentString(roundPts, label));
  }
  
  private static Coordinate[] round(CoordinateSequence seq, PrecisionModel pm) {
    if (seq.size() == 0) return new Coordinate[0];

    CoordinateList coordList = new CoordinateList();  
    // copy coordinates and reduce
    for (int i = 0; i < seq.size(); i++) {
      Coordinate coord = new Coordinate(
          seq.getOrdinate(i,  Coordinate.X),
          seq.getOrdinate(i,  Coordinate.Y) );
      pm.makePrecise(coord);
      coordList.add(coord, false);
    }
    Coordinate[] coord = coordList.toCoordinateArray();
    return coord;
  }
}
