package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.exceptions.ProductStockException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Orders;
import com.example.demo.repository.OrdersRepository;

/**
 * Service class for managing orders.
 * Handles business logic for creating, retrieving, updating, and deleting orders,
 * including communication with the Products microservice for updating the quantity in products.
 */
@Service
public class OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private RestTemplate restTemplate;

	 /**
     * Creates a new order after validating product availability via the Products microservice.
     *
     * @param order the order to be created
     * @return the saved order entity
     * @throws ResourceNotFoundException if the product ID does not exist
     * @throws ProductStockException if there is insufficient stock
     */
	public Orders createOrder(Orders order) {
		Long id = order.getProductId();
		int quantity = order.getQuantity();

		String url = "http://ProductsMicroService/products/" + id + "/" + quantity;

		try {
			restTemplate.put(url, null);
		} catch (HttpClientErrorException e) {
			
			if (e.getStatusCode()==HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException("Order failed: Product ID "+id+" not found");
			} else {
				throw new ProductStockException("Order failed: Not enough stock");
			}
		}

		return ordersRepository.save(order);
	}
	
	 /**
     * Retrieves all orders from the database.
     *
     * @return list of all orders
     */
	public List<Orders> getAllOrders() {
		return ordersRepository.findAll();
	}

	 /**
     * Retrieves a specific order by its ID.
     * @param id the ID of the order
     * @return the order entity
     * @throws ResourceNotFoundException if the order is not found
     */
	public Orders getOrderById(Long id) {
		return ordersRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("The order with ID " + id + " not found"));
	}

	 /**
     * Updates the quantity of an existing order and adjusts stock accordingly.
     *
     * @param id the ID of the order to update
     * @param newQuantity the new quantity to set
     * @return the updated order entity
     * @throws ResourceNotFoundException if the order is not found
     * @throws ProductStockException if stock is not available
     */
	public Orders updateOrderQuantity(Long id, int newQuantity) {
		Orders existingOrder = ordersRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("The order with ID " + id + " not found"));

		Long productId = existingOrder.getProductId();
		int oldQuantity = existingOrder.getQuantity();
		int diff = newQuantity - oldQuantity;

		try {
			if (diff > 0) {
				String reserveUrl = "http://ProductsMicroService/products/" + productId + "/" + diff;
				restTemplate.put(reserveUrl, null);
			} else if (diff < 0) {
				String restoreUrl = "http://ProductsMicroService/products/restore/" + productId + "/" + Math.abs(diff);
				restTemplate.put(restoreUrl, null);
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				throw new ProductStockException("Not enough stock for Product ID " + productId);
			} else {
				throw new ProductStockException("Unexpected error while updating stock for Product ID " + productId);
			}
		}

		existingOrder.setQuantity(newQuantity);
		return ordersRepository.save(existingOrder);
	}
	
	/**
     * Deletes all orders from the database.
     */
	public void deleteAllOrders() {
		ordersRepository.deleteAll();
	}

	 /**
     * Deletes a specific order by its ID and restores the product stock.
     *
     * @param id the ID of the order to delete
     * @throws ResourceNotFoundException if the order is not found
     * @throws ProductStockException if stock restoration fails
     */
	public void deleteOrderById(Long id) {
		if (!ordersRepository.existsById(id)) {
			throw new ResourceNotFoundException("The order with ID " + id + " not found");
		}

		Optional<Orders> orders = ordersRepository.findById(id);
		Orders order = orders.get();
		int quantity = order.getQuantity();
		Long productId = order.getProductId();

		String url = "http://ProductsMicroService/products/restore/" + productId + "/" + quantity;
		try {
			restTemplate.put(url, null);
		} catch (HttpClientErrorException e) {
			throw new ProductStockException("Failed to restore stock for Product ID " + productId);
		}

		ordersRepository.deleteById(id);
	}

}
