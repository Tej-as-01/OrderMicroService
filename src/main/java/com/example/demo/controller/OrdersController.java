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

import com.example.demo.repo.Orders;

import com.example.demo.service.OrdersService;

@RestController
@RequestMapping("/orders")
public class OrdersController {
	
	@Autowired
	private OrdersService ordersService;
	
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
	public Orders createOrder(@RequestBody Orders order)
	{
		return ordersService.createOrder(order);
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
