	package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Exceptions.ResourceNotFoundException;
import com.example.demo.repo.Orders;
import com.example.demo.repo.OrdersRepository;


@Service
public class OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;
	
	public Orders createOrder(Orders order)
	{
		return ordersRepository.save(order);
	}
	
	public List<Orders> getAllOrders()
	{
		return ordersRepository.findAll();
	}
	
	public Orders getOrderById(Long id)
	{
		return ordersRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("The order with ID "+id+" not found"));
	}
	
	public Orders updateOrders(Long id, Orders order)
	{
		 Orders existingOrder = ordersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("The order with ID " + id + " not found"));

		   
		    existingOrder.setCustomerName(order.getCustomerName());
		    existingOrder.setProductId(order.getProductId());
		    existingOrder.setQuantity(order.getQuantity());
		   

		    return ordersRepository.save(existingOrder);
	}
	
	
	public void deleteAllOrders()
	{
		ordersRepository.deleteAll();
	}
	
	public void deleteOrderById(Long id)
	{
		if(!ordersRepository.existsById(id))
		{
			throw new  ResourceNotFoundException("The order with ID " + id + " not found");
		}
		
		ordersRepository.deleteById(id);
	}
	
	
}
