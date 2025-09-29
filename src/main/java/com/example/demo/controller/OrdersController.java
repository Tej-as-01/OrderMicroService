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


import com.example.demo.repo.Orders;
import com.example.demo.service.OrdersService;

@RestController
@RequestMapping("/orders")
public class OrdersController {
	
	@Autowired
	private OrdersService ordersService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping
	public List<Orders> getAllOrders()
	{
		return ordersService.getAllOrders();
	}
	
	@GetMapping("/{id}")
	public Orders getOrdersById(@PathVariable("id") Long id)
	{
		return ordersService.getOrderById(id);
	}
	
	@PostMapping()
	public ResponseEntity<?> createOrder(@RequestBody Orders order) {
	    Long id = order.getProductId();
	    int quantity = order.getQuantity();

	    String url = "http://ProductsMicroService/products/" + id + "/" + quantity;

	    try {
	        restTemplate.put(url, null);
	        Orders savedOrder = ordersService.createOrder(order);
	        return ResponseEntity.ok(savedOrder);
	    } catch (Exception e) {
	        String message = e.getMessage();
	        if (message.contains("404")) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order failed: Product ID not found");
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order failed: Not enough stock");
	        }
	    }
	}

	
	@PutMapping("/{id}")
	public Orders updateOrder(@PathVariable("id") Long id, @RequestBody Orders order)
	{
		return ordersService.updateOrders(id, order);
		
	}
	
	@DeleteMapping()
	public ResponseEntity<String> deleteAllOrders()
	{
		ordersService.deleteAllOrders();
		return ResponseEntity.status(HttpStatus.OK).body("All orders are deleted");
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteOrderById(@PathVariable("id") Long id)
	{
		ordersService.deleteOrderById(id);
		return ResponseEntity.status(HttpStatus.OK).body("Order with ID "+id+" is deleted");
	}
	

}
