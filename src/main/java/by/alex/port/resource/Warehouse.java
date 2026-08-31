package by.alex.port.resource;

import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {

  private final int capacity;
  private final AtomicInteger containerCount;

  public Warehouse(int capacity, int initialContainers) {
    this.capacity = capacity;
    this.containerCount = new AtomicInteger(initialContainers);
  }

  public int store(int amount) {
    while (true) {
      int current = containerCount.get();
      int accepted = Math.min(amount, capacity - current);
      if (accepted <= 0) {
        return 0;
      }
      if (containerCount.compareAndSet(current, current + accepted)) {
        return accepted;
      }
    }
  }

  public int retrieve(int amount) {
    while (true) {
      int current = containerCount.get();
      int issued = Math.min(amount, current);
      if (issued <= 0) {
        return 0;
      }
      if (containerCount.compareAndSet(current, current - issued)) {
        return issued;
      }
    }
  }

  public int getContainerCount() {
    return containerCount.get();
  }

  public int getCapacity() {
    return capacity;
  }
}
