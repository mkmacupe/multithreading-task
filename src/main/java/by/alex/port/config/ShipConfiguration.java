package by.alex.port.config;

import by.alex.port.entity.ShipOperation;

public record ShipConfiguration(long shipId,
                                int capacity,
                                int containersOnBoard,
                                ShipOperation operation,
                                int containersToUnload,
                                int containersToLoad) {

}
