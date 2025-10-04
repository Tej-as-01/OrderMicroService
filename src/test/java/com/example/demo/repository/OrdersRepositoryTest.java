package com.example.demo.repository;

import com.example.demo.model.Orders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("mysql-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrdersRepositoryTest {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private TestEntityManager entityManager;

    // Verifies that the save operation for an order functions correctly.
    @Test
    @DisplayName("Integration-Test Save Order")
    public void testSaveOrder() {
        Orders order = new Orders();
        order.setCustomerName("Ravi");
        order.setProductId(101L);
        order.setQuantity(3);

        Orders saved = ordersRepository.save(order);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    // Verifies that retrieving an order by its ID returns the correct result.
    @Test
    @DisplayName("Integration-Test Find Order by ID")
    public void testFindById() {
        Orders order = new Orders();
        order.setCustomerName("Meera");
        order.setProductId(202L);
        order.setQuantity(5);

        Orders saved = entityManager.persistAndFlush(order);
        Optional<Orders> found = ordersRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Meera");
    }

    // Verifies that retrieving all orders returns the expected list of entries.
    @Test
    @DisplayName("Integration-Test Find All Orders")
    public void testFindAll() {
        Orders order1 = new Orders();
        order1.setCustomerName("Amit");
        order1.setProductId(303L);
        order1.setQuantity(2);

        Orders order2 = new Orders();
        order2.setCustomerName("Priya");
        order2.setProductId(404L);
        order2.setQuantity(4);

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Orders> allOrders = ordersRepository.findAll();
        assertThat(allOrders).hasSizeGreaterThanOrEqualTo(2);
    }

    // Verifies that deleting an order by its ID removes it from the database.
    @Test
    @DisplayName("Integration-Test Delete Order by ID")
    public void testDeleteById() {
        Orders order = new Orders();
        order.setCustomerName("DeleteTest");
        order.setProductId(505L);
        order.setQuantity(1);

        Orders saved = entityManager.persistAndFlush(order);
        ordersRepository.deleteById(saved.getId());

        Optional<Orders> found = ordersRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    // Verifies that deleting all orders clears the repository.
    @Test
    @DisplayName("Integration-Test Delete All Orders")
    public void testDeleteAll() {
        Orders order1 = new Orders();
        order1.setCustomerName("ClearOne");
        order1.setProductId(606L);
        order1.setQuantity(6);

        Orders order2 = new Orders();
        order2.setCustomerName("ClearTwo");
        order2.setProductId(707L);
        order2.setQuantity(7);

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        ordersRepository.deleteAll();
        List<Orders> allOrders = ordersRepository.findAll();
        assertThat(allOrders).isEmpty();
    }
}
