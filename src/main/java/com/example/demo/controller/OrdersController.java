package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Orders;
import com.example.demo.service.OrdersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * REST controller for managing Orders.
 * Provides endpoints for creating, retrieving, updating, and deleting orders.
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders Controller", description = "Handles CRUD operations for Orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	 /**
     * Retrieves all orders from the system.
     * @return list of all orders
     */
	@Operation(summary = "Get all orders", description = "Returns a list of all orders")
	@GetMapping
	public List<Orders> getAllOrders() {
		return ordersService.getAllOrders();
	}

	 /**
     * Retrieves a specific order by its ID.
     * @param id the ID of the order
     * @return the order with the given ID
     */
	@Operation(summary = "Get order by ID", description = "Returns a single order by its ID")
	@GetMapping("/{id}")
	public Orders getOrdersById(@PathVariable("id") Long id) {
		return ordersService.getOrderById(id);
	}

	 /**
     * Creates a new order.
     * @param order the order to be created
     * @return the saved order entity
     */
	@Operation(summary = "Create a new order", description = "Creates a new order and returns the saved entity")
	@PostMapping()
	public ResponseEntity<Orders> createOrder(@RequestBody Orders order) {
		Orders savedOrder = ordersService.createOrder(order);
		return ResponseEntity.ok(savedOrder);
	}

	 /**
     * Updates the quantity of an existing order.
     * @param id the ID of the order to update
     * @param quantity the new quantity value
     * @return the updated order
     */
	@Operation(summary = "Update order quantity", description = "Updates the quantity of an existing order")
	@PutMapping("/{id}/{quantity}")
	public Orders updateOrder(@PathVariable("id") Long id, @PathVariable("quantity") int quantity) {
		return ordersService.updateOrderQuantity(id, quantity);
	}

	/**
     * Deletes all orders from the system.
     * @return confirmation message
     */
	@Operation(summary = "Delete all orders", description = "Deletes all orders from the database")
	@DeleteMapping()
	public ResponseEntity<String> deleteAllOrders() {
		ordersService.deleteAllOrders();
		return ResponseEntity.status(HttpStatus.OK).body("All orders are deleted");
	}

	/**
     * Deletes a specific order by its ID.
     * @param id the ID of the order to delete
     * @return confirmation message
     */
	@Operation(summary = "Delete order by ID", description = "Deletes a specific order by its ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id) {
		ordersService.deleteOrderById(id);
		return ResponseEntity.status(HttpStatus.OK).body("Order with ID " + id + " is deleted");
	}

}
