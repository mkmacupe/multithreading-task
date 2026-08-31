package by.alex.port.config;

public record PortConfiguration(int berthCount,
                                int warehouseCapacity,
                                int initialContainers,
                                long mooringTimeMillis,
                                long containerHandlingTimeMillis) {

}
