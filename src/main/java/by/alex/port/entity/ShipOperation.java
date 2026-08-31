package by.alex.port.entity;

public enum ShipOperation {

  UNLOAD,
  LOAD,
  UNLOAD_AND_LOAD;

  public boolean requiresUnloading() {
    return this == UNLOAD || this == UNLOAD_AND_LOAD;
  }

  public boolean requiresLoading() {
    return this == LOAD || this == UNLOAD_AND_LOAD;
  }
}
