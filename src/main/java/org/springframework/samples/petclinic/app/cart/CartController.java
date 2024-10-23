package org.springframework.samples.petclinic.app.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.app.products.*;
import org.springframework.samples.petclinic.app.users.CustomUserDetail;
import org.springframework.samples.petclinic.app.users.User;
import org.springframework.samples.petclinic.app.users.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductVariationService productVariationService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestBody CartRequest cartRequest) {
        Long productId = cartRequest.getProductId();
        String size = cartRequest.getSize();
        String color = cartRequest.getColor();
        int quantity = cartRequest.getQuantity();

        // Assuming ProductVariationService has a method to find product variation by size and color
        ProductVariation productVariation = productVariationService.findBySizeAndColor(productId, Size.valueOf(size), color);

        // Obtain the userId from the authenticated user's details
        Long userId = getUserIdFromSessionOrAuthentication();


        if (userId == null) {
            // Handle the scenario where userId is not found
            return "User not authenticated";
        }

        Cart cart = cartService.createCart(userId);
        CartItem cartItem = cartService.findCartItemByProductVariationIdAndUserId(productVariation.getId(), cart.getId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartService.save(cartItem);
        } else {
            cartService.addToCart(cart, productVariation.getId(), quantity);
        }
        return "Product added to cart successfully";
    }

    @GetMapping("/{cartId}")
    public String viewCartItems(@PathVariable Long cartId, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(cartId);
        model.addAttribute("cartItems", cartItems);
        return "cart/view"; // Adjust to your view name
    }

    @GetMapping("/user/cart")
    public String viewCart(HttpServletRequest request, Model model) {
        Long userId = getUserIdFromSessionOrAuthentication();


        List<CartItem> cartItems = new ArrayList<>();
        if (userId != null) {
            HttpSession session = request.getSession();
            Object cartItemsAttr = session.getAttribute("cartItems");
            if (cartItemsAttr != null) {
                cartItems = (List<CartItem>) cartItemsAttr;
                cartService.saveAllItems(cartItems, userId);
                session.removeAttribute("cartItems");
            }
            cartItems = cartService.getCartByUserId(userId);

        } else {
            // If the user is not authenticated, get cartItems from session
            HttpSession session = request.getSession();
            Object cartItemsAttr = session.getAttribute("cartItems");
            if (cartItemsAttr != null) {
                cartItems = (List<CartItem>) cartItemsAttr;
            }
        }

        if (cartItems.isEmpty()) {
//            model.addAttribute("message", "Your cart is empty.");
        } else {
            model.addAttribute("cartItems", cartItems);
        }


        BigDecimal totalPrice = cartService.calculateTotalPrice(cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "cart/viewCart";
    }

    @PostMapping("/localstorage")
    public ResponseEntity<Void> handleLocalStorageCart(HttpServletRequest request, @RequestBody List<CartRequest> cartRequests) {

        List<CartItem> cartItems = new ArrayList<>();

        // Generate unique IDs for cart items
        long cartItemId = 1; // Starting ID

        for (CartRequest cartRequest : cartRequests) {
            CartItem cartItem = new CartItem();

            // Assign unique ID to cart item
            cartItem.setId(cartItemId++);

            // Set quantity from CartRequest
            cartItem.setQuantity(cartRequest.getQuantity());

            // Create ProductVariation and set size, color, and product
            ProductVariation productVariation = new ProductVariation();
            productVariation.setSize(Size.valueOf(cartRequest.getSize()));
            productVariation.setColor(Color.valueOf(cartRequest.getColor()));

            Product product = productService.findById(cartRequest.getProductId());
            productVariation.setProduct(product);

            cartItem.setProductVariation(productVariation);

            // Add cartItem to cartItems list
            cartItems.add(cartItem);
        }

        // Store cartItems in session
        HttpSession session = request.getSession();
        session.setAttribute("cartItems", cartItems);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteCartItem(HttpServletRequest request, @RequestParam Long cartItemId) {
        // Check if user is authenticated
        Long userId = getUserIdFromSessionOrAuthentication();

        // If userId is not null, user is authenticated, delete from database
        if (userId != null) {
            boolean deleted = cartService.deleteCartItemByUserId(userId, cartItemId);
            if (deleted) {
                return ResponseEntity.ok().body("Item deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete item");
            }
        } else {
            // If user is not authenticated, get cartItems from session and delete
            HttpSession session = request.getSession();
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

            if (cartItems != null) {
                cartItems.removeIf(cartItem -> cartItem.getId().equals(cartItemId));
                session.setAttribute("cartItems", cartItems);
                return ResponseEntity.ok().body("Item deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete item");
            }
        }
    }

    @PostMapping("/delete/{cartItemId}")
    public String deleteCartItem(@PathVariable Long cartItemId, HttpServletRequest request) {
        // Get userId from authenticated user or session
        Long userId = getUserIdFromSessionOrAuthentication();

        if (userId != null) {
            // If user is authenticated, delete cart item from database
            cartService.deleteCartItemByUserId(userId, cartItemId);
        } else {
            // If user is not authenticated, delete cart item from session
            HttpSession session = request.getSession();
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

            if (cartItems != null) {
                cartItems.removeIf(cartItem -> cartItem.getId().equals(cartItemId));
                session.setAttribute("cartItems", cartItems);
            }
        }

        // Redirect back to cart view after deletion
        return "redirect:/cart/user/cart";
    }

    // Utility method to get userId from session or authentication
    private Long getUserIdFromSessionOrAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                String oidcUserId = oidcUser.getAttribute("email");
                User user = userService.findByEmail(oidcUserId);
                userId = user.getId();
            } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                userId = ((CustomUserDetail) userDetails).getUser().getId();
            }
        }

        return userId;
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody UpdateQuantityRequest request) {
        boolean updated = cartService.updateCartItemQuantity(request.getCartItemId(), request.getNewQuantity());
        log.info(String.valueOf(request.getCartItemId()));
        log.info(String.valueOf(request.getNewQuantity()));
        if (updated) {
            return ResponseEntity.ok("Quantity updated successfully");
        } else {
            return ResponseEntity.status(400).body("Failed to update quantity");
        }
    }

    @GetMapping("/user/isLoggedIn")
    public ResponseEntity<Boolean> isLoggedIn(HttpServletRequest request) {
        Long userId = getUserIdFromSessionOrAuthentication();
        return ResponseEntity.ok(userId != null);
    }
}

