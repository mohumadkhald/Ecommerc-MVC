
package org.springframework.samples.petclinic.app.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerOrderService {
    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    public CustomerOrder createOrder(CustomerOrder order) {
        order.setOrderDate(new Date());
        return customerOrderRepository.save(order);
    }
}