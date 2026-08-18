package db.migration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import net.datafaker.Faker;

/**
 * Flyway Java migration that loads demo rows with Datafaker. Versioned
 * migrations in {@code db.migration} are picked up from
 * {@code classpath:db/migration}.
 */
public class V2__Seed extends BaseJavaMigration {

    private static final int CUSTOMER_COUNT = 12;
    private static final int PRODUCT_COUNT = 20;
    private static final int CART_COUNT = 8;
    private static final int ORDER_COUNT = 15;

    private static final String[] ORDER_STATUSES = {
            "pending",
            "paid",
            "processing",
            "shipped",
            "completed",
            "cancelled"
    };

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        Faker faker = new Faker(Locale.US, new Random(42));

        List<UUID> customerIds = insertCustomers(connection, faker);
        List<ProductSeed> products = insertProducts(connection, faker);
        insertInventory(connection, faker, products);
        insertCarts(connection, faker, customerIds, products);
        insertOrders(connection, faker, customerIds, products);
    }

    private static List<UUID> insertCustomers(Connection connection, Faker faker) throws SQLException {
        List<UUID> ids = new ArrayList<>(CUSTOMER_COUNT);
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO customers (id, name, email) VALUES (?, ?, ?)")) {
            for (int i = 0; i < CUSTOMER_COUNT; i++) {
                UUID id = UUID.randomUUID();
                ids.add(id);
                statement.setObject(1, id);
                statement.setString(2, faker.name().fullName());
                statement.setString(3, faker.internet().username() + "." + i + "@example.com");
                statement.addBatch();
            }
            statement.executeBatch();
        }
        return ids;
    }

    private static List<ProductSeed> insertProducts(Connection connection, Faker faker) throws SQLException {
        List<ProductSeed> products = new ArrayList<>(PRODUCT_COUNT);
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO products (id, name, description, price) VALUES (?, ?, ?, ?)")) {
            for (int i = 0; i < PRODUCT_COUNT; i++) {
                UUID id = UUID.randomUUID();
                BigDecimal price = new BigDecimal(faker.commerce().price(4.99, 399.99))
                        .setScale(2, RoundingMode.HALF_UP);
                products.add(new ProductSeed(id, price));
                statement.setObject(1, id);
                statement.setString(2, faker.commerce().productName());
                statement.setString(3, faker.lorem().sentence());
                statement.setBigDecimal(4, price);
                statement.addBatch();
            }
            statement.executeBatch();
        }
        return products;
    }

    private static void insertInventory(Connection connection, Faker faker, List<ProductSeed> products)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        INSERT INTO inventory (product_id, quantity_on_hand, quantity_reserved)
                        VALUES (?, ?, ?)
                        """)) {
            for (ProductSeed product : products) {
                int onHand = faker.number().numberBetween(15, 250);
                int reserved = faker.number().numberBetween(0, Math.min(25, onHand));
                statement.setObject(1, product.id());
                statement.setInt(2, onHand);
                statement.setInt(3, reserved);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void insertCarts(
            Connection connection,
            Faker faker,
            List<UUID> customerIds,
            List<ProductSeed> products) throws SQLException {
        try (PreparedStatement cartStatement = connection.prepareStatement(
                "INSERT INTO carts (id, customer_id) VALUES (?, ?)");
                PreparedStatement itemStatement = connection.prepareStatement(
                        "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)")) {
            for (int i = 0; i < CART_COUNT; i++) {
                UUID cartId = UUID.randomUUID();
                cartStatement.setObject(1, cartId);
                cartStatement.setObject(2, customerIds.get(i));
                cartStatement.addBatch();

                for (UUID productId : pickProductIds(faker, products, faker.number().numberBetween(1, 4))) {
                    itemStatement.setObject(1, cartId);
                    itemStatement.setObject(2, productId);
                    itemStatement.setInt(3, faker.number().numberBetween(1, 5));
                    itemStatement.addBatch();
                }
            }
            cartStatement.executeBatch();
            itemStatement.executeBatch();
        }
    }

    private static void insertOrders(
            Connection connection,
            Faker faker,
            List<UUID> customerIds,
            List<ProductSeed> products) throws SQLException {
        try (PreparedStatement orderStatement = connection.prepareStatement(
                """
                        INSERT INTO orders (
                            id, customer_id, status, paid_at, shipped_at, completed_at, cancelled_at)
                        VALUES (?, ?, CAST(? AS order_status), ?, ?, ?, ?)
                        """);
                PreparedStatement itemStatement = connection.prepareStatement(
                        """
                                INSERT INTO order_items (order_id, product_id, quantity, unit_price)
                                VALUES (?, ?, ?, ?)
                                """)) {
            Instant now = Instant.now();
            for (int i = 0; i < ORDER_COUNT; i++) {
                UUID orderId = UUID.randomUUID();
                String status = ORDER_STATUSES[i % ORDER_STATUSES.length];
                Instant created = now.minus(faker.number().numberBetween(1, 40), ChronoUnit.DAYS);

                orderStatement.setObject(1, orderId);
                orderStatement.setObject(2, customerIds.get(i % customerIds.size()));
                orderStatement.setString(3, status);
                setTimestamp(orderStatement, 4,
                        timestampIf(status, created, 2, "paid", "processing", "shipped", "completed"));
                setTimestamp(orderStatement, 5, timestampIf(status, created, 5, "shipped", "completed"));
                setTimestamp(orderStatement, 6, timestampIf(status, created, 8, "completed"));
                setTimestamp(orderStatement, 7, timestampIf(status, created, 1, "cancelled"));
                orderStatement.addBatch();

                for (ProductSeed product : pickProducts(faker, products, faker.number().numberBetween(1, 4))) {
                    itemStatement.setObject(1, orderId);
                    itemStatement.setObject(2, product.id());
                    itemStatement.setInt(3, faker.number().numberBetween(1, 4));
                    itemStatement.setBigDecimal(4, product.price());
                    itemStatement.addBatch();
                }
            }
            orderStatement.executeBatch();
            itemStatement.executeBatch();
        }
    }

    private static Timestamp timestampIf(String status, Instant created, int daysLater, String... matching) {
        for (String candidate : matching) {
            if (candidate.equals(status)) {
                return Timestamp.from(created.plus(daysLater, ChronoUnit.DAYS));
            }
        }
        return null;
    }

    private static void setTimestamp(PreparedStatement statement, int index, Timestamp value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, value);
        }
    }

    private static List<UUID> pickProductIds(Faker faker, List<ProductSeed> products, int count) {
        return pickProducts(faker, products, count).stream().map(ProductSeed::id).toList();
    }

    private static List<ProductSeed> pickProducts(Faker faker, List<ProductSeed> products, int count) {
        Set<Integer> indexes = new HashSet<>();
        int size = Math.min(count, products.size());
        while (indexes.size() < size) {
            indexes.add(faker.number().numberBetween(0, products.size()));
        }
        return indexes.stream().map(products::get).toList();
    }

    private record ProductSeed(UUID id, BigDecimal price) {
    }
}
