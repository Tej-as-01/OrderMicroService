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

import com.example.demo.Exceptions.ProductStockException;
import com.example.demo.Exceptions.ResourceNotFoundException;
import com.example.demo.repo.Orders;
import com.example.demo.repo.OrdersRepository;

@Service
public class OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private RestTemplate restTemplate;

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

	public List<Orders> getAllOrders() {
		return ordersRepository.findAll();
	}

	public Orders getOrderById(Long id) {
		return ordersRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("The order with ID " + id + " not found"));
	}

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

	public void deleteAllOrders() {
		ordersRepository.deleteAll();
	}

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
