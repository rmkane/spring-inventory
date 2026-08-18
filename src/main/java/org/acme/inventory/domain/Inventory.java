package org.acme.inventory.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    private UUID productId;
    private int quantityOnHand;
    private int quantityReserved;
    private OffsetDateTime updatedAt;
}
