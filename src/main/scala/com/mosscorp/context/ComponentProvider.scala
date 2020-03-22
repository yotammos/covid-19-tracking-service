package com.mosscorp.context

import com.mosscorp.clients.JohnsHopkinsClientComponent
import com.mosscorp.services.Covid19TrackingServiceComponent

class ComponentProvider extends JohnsHopkinsClientComponent with Covid19TrackingServiceComponent {
  override lazy val johnsHopkinsClient: JohnsHopkinsClient = new JohnsHopkinsClient
  override lazy val covid19TrackingService: Covid19TrackingService = new Covid19TrackingService(this);
}
