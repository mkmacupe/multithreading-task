package by.alex.port.state;

import by.alex.port.entity.Ship;
import by.alex.port.exception.PortException;

public interface ShipState {

  String STATE_SUFFIX = "State";

  void proceed(Ship ship) throws PortException;

  default boolean isTerminal() {
    return false;
  }

  default String stateName() {
    String simpleName = getClass().getSimpleName();
    return simpleName.endsWith(STATE_SUFFIX)
        ? simpleName.substring(0, simpleName.length() - STATE_SUFFIX.length())
        : simpleName;
  }
}
