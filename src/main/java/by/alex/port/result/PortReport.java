package by.alex.port.result;

import java.util.List;

public record PortReport(int servedShips,
                         int fullyServedShips,
                         int totalUnloadedContainers,
                         int totalLoadedContainers,
                         long longestBerthWaitingMillis) {

  public static PortReport from(List<TransferResult> results) {
    int fullyServedShips = 0;
    int totalUnloadedContainers = 0;
    int totalLoadedContainers = 0;
    long longestBerthWaitingMillis = 0;
    for (TransferResult result : results) {
      totalUnloadedContainers += result.unloadedContainers();
      totalLoadedContainers += result.loadedContainers();
      longestBerthWaitingMillis = Math.max(longestBerthWaitingMillis,
          result.waitingForBerthMillis());
      if (result.isFullyServed()) {
        fullyServedShips++;
      }
    }
    return new PortReport(results.size(), fullyServedShips,
        totalUnloadedContainers, totalLoadedContainers, longestBerthWaitingMillis);
  }

  public boolean isEveryShipServed(int expectedShipCount) {
    return servedShips == expectedShipCount;
  }
}
