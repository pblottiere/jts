package org.locationtech.jtstest.function;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.overlay.OverlayOp;

public class OverlaySRFunctions {
  public static Geometry intersection(Geometry a, Geometry b, double scaleFactor) {
    PrecisionModel pm = new PrecisionModel(scaleFactor);
    Geometry result = OverlayOp.overlayOp(a, b, OverlayOp.INTERSECTION, pm);
    return result;
  }

  public static Geometry union(Geometry a, Geometry b, double scaleFactor) {
    PrecisionModel pm = new PrecisionModel(scaleFactor);
    Geometry result = OverlayOp.overlayOp(a, b, OverlayOp.UNION, pm);
    return result;
  }

}
