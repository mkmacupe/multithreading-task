package by.alex.port.state;

import by.alex.port.exception.PortException;

import java.util.concurrent.TimeUnit;

public abstract class AbstractShipState implements ShipState {

  protected void pause(long millis) throws PortException {
    try {
      TimeUnit.MILLISECONDS.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread()
            .interrupt();
      throw new PortException("Ship handling was interrupted in state " + stateName(), e);
    }
  }
}
