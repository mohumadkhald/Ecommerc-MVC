package org.springframework.samples.petclinic.app.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping("/checkout")
    public String showCheckoutForm(Model model) {
        model.addAttribute("checkoutForm", new CheckoutForm());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute CheckoutForm checkoutForm, Model model) {
        // Create an order based on the checkout form data
        CustomerOrder order = new CustomerOrder();
        order.setCardNumber(checkoutForm.getCardNumber());
        order.setCardHolderName(checkoutForm.getCardHolderName());
        order.setExpiryDate(checkoutForm.getExpiryDate());
        order.setCvv(checkoutForm.getCvv());
        order.setTotalPrice(100.00); // Replace with actual total price calculation

         customerOrderService.createOrder(order);

        model.addAttribute("order", order);
        return "orderConfirmation";
    }
}