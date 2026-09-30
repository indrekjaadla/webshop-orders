package com.indrekjaadla.webshoporders.order;

import com.indrekjaadla.webshoporders.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OrderApiIntegrationTest {

    private final UUID FIRST_ORDER_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    private final UUID SECOND_ORDER_ID = UUID.fromString("00000000-0000-4000-8000-000000000002");
    private final String VALID_ORDER_JSON = """
            {
              "items": [
                { "name": "First item", "quantity": 1, "unitPrice": 10.50 },
                { "name": "Second item", "quantity": 2, "unitPrice": 20.00 }
              ]
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void cleanDatabase() {
        jdbcClient.sql("DELETE FROM orders").update();
    }

    @Test
    void returnsEmptyListWhenNoOrdersExist() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void returnsNewestOrdersFirst() throws Exception {
        insertOrder(FIRST_ORDER_ID, "DELIVERED", "2026-09-08T09:14:00Z");
        insertOrder(SECOND_ORDER_ID, "NEW", "2026-09-29T19:48:00Z");

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(SECOND_ORDER_ID.toString()))
                .andExpect(jsonPath("$.data[1].id").value(FIRST_ORDER_ID.toString()));
    }

    @Test
    void sortsOrdersWithSameCreationTimeById() throws Exception {
        insertOrder(SECOND_ORDER_ID, "NEW", "2026-09-29T19:48:00Z");
        insertOrder(FIRST_ORDER_ID, "NEW", "2026-09-29T19:48:00Z");

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(FIRST_ORDER_ID.toString()))
                .andExpect(jsonPath("$.data[1].id").value(SECOND_ORDER_ID.toString()));
    }

    @Test
    void returnsItemsInLineNumberOrder() throws Exception {
        insertOrder(FIRST_ORDER_ID, "NEW", "2026-09-29T19:48:00Z");
        insertItem(FIRST_ORDER_ID, 2, "Second line", 1, new BigDecimal("20.00"));
        insertItem(FIRST_ORDER_ID, 1, "First line", 1, new BigDecimal("10.00"));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].items[0].name").value("First line"))
                .andExpect(jsonPath("$.data[0].items[1].name").value("Second line"));
    }

    @Test
    void returnsOrderInAgreedFormat() throws Exception {
        insertOrder(FIRST_ORDER_ID, "PAID", "2026-09-29T19:48:00Z");
        insertItem(FIRST_ORDER_ID, 1, "Sülearvuti kott", 2, new BigDecimal("39.99"));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(FIRST_ORDER_ID.toString()))
                .andExpect(jsonPath("$.data[0].status").value("PAID"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-09-29T19:48:00Z"))
                .andExpect(jsonPath("$.data[0].items[0].name").value("Sülearvuti kott"))
                .andExpect(jsonPath("$.data[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$.data[0].items[0].unitPrice").value(39.99))
                .andExpect(jsonPath("$.data[0].items[0].id").doesNotExist())
                .andExpect(jsonPath("$.data[0].items[0].lineNumber").doesNotExist());
    }

    @Test
    void createsOrderWithStatusNew() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.items[0].name").value("First item"))
                .andExpect(jsonPath("$.items[1].name").value("Second item"));
    }

    @Test
    void createdOrderAppearsInList() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].items.length()").value(2));
    }

    @Test
    void trimsItemNames() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [ { "name": "  Kontoritool  ", "quantity": 1, "unitPrice": 249.00 } ] }
                                """))
                .andExpect(status().isCreated());

        String storedName = jdbcClient.sql("SELECT name FROM order_items")
                .query(String.class)
                .single();

        assertThat(storedName).isEqualTo("Kontoritool");
    }

    @Test
    void rejectsEmptyItems() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [] }
                                """))
                .andExpect(status().isBadRequest());

        assertThat(countOrders()).isZero();
    }

    @Test
    void rejectsPriceWithMoreThanTwoDecimals() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "items": [ { "name": "Item", "quantity": 1, "unitPrice": 10.505 } ] }
                                """))
                .andExpect(status().isBadRequest());

        assertThat(countOrders()).isZero();
    }

    @Test
    void rejectsUnknownField() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "PAID", "items": [ { "name": "Item", "quantity": 1, "unitPrice": 10.00 } ] }
                                """))
                .andExpect(status().isBadRequest());

        assertThat(countOrders()).isZero();
    }

    private void insertOrder(UUID id, String status, String createdAt) {
        jdbcClient.sql("INSERT INTO orders (id, status, created_at) VALUES (?, ?, CAST(? AS timestamptz))")
                .params(id, status, createdAt)
                .update();
    }

    private void insertItem(UUID orderId, int lineNumber, String name, int quantity, BigDecimal unitPrice) {
        jdbcClient.sql("INSERT INTO order_items (order_id, line_number, name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)")
                .params(orderId, lineNumber, name, quantity, unitPrice)
                .update();
    }

    private long countOrders() {
        return jdbcClient.sql("SELECT count(*) FROM orders")
                .query(Long.class)
                .single();
    }
}
