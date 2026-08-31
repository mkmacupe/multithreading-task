package by.alex.port.result;

import by.alex.port.entity.ShipOperation;

public record TransferResult(long shipId,
                             ShipOperation operation,
                             int plannedUnload,
                             int unloadedContainers,
                             int plannedLoad,
                             int loadedContainers,
                             long waitingForBerthMillis) {

  public boolean isFullyServed() {
    return unloadedContainers == plannedUnload && loadedContainers == plannedLoad;
  }
}
