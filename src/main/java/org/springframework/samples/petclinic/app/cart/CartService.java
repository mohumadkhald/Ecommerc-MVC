package org.springframework.samples.petclinic.app.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.app.products.*;
import org.springframework.samples.petclinic.app.users.CustomUserDetail;
import org.springframework.samples.petclinic.app.users.User;
import org.springframework.samples.petclinic.app.users.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductVariationService productVariationService;

    public Cart createCart(Long userId) {
        Cart newCart = new Cart();
        Cart existCart = cartRepository.findByUserId(userId);
        if (existCart != null) {
            return existCart;
        }
        newCart.setUserId(userId);
        return cartRepository.save(newCart);
    }

    public List<CartItem> getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = createCart(userId); // Create a new cart if it doesn't exist
        }
        return cart.getCartItems();
    }

    public CartItem addToCart(Cart cart, Long productVariationId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setId(productVariationId);
        cartItem.setProductVariation(productVariation);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartItem;
    }

    public List<CartItem> getCartItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                // Assuming 'sub' is the unique identifier from the OIDC provider (Google)
                String oidcUserId = oidcUser.getAttribute("email");
                // Here you should map the oidcUserId to your internal userId
                User user = userService.findByEmail(oidcUserId);
                userId = user.getId();
            } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                // Assuming UserDetails implementation has a method to get userId
                userId = ((CustomUserDetail) userDetails).getUser().getId();
            }
        }
        if (!Objects.equals(cart.getUserId(), userId))
            return null;
        return cartItemRepository.findByCartId(cartId);
    }



    public boolean deleteCartItemByUserId(Long userId, Long cartItemId) {
        // Retrieve the cart for the given userId
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            // Handle scenario where cart is not found for the user
            return false;
        }

        // Retrieve the list of cart items from the cart
        List<CartItem> cartItems = cart.getCartItems();

        // Find the cart item to delete
        CartItem cartItemToDelete = cartItems.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElse(null);

        if (cartItemToDelete == null) {
            // Handle scenario where cart item to delete is not found
            return false;
        }

        // Remove the cart item from the list
        cartItems.remove(cartItemToDelete);

        // Update the cart with the modified cart items list
        cart.setCartItems(cartItems);

        // Save the updated cart back to the database
        cartRepository.save(cart);

        return true;
    }

    public boolean updateCartItemQuantity(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem != null) {
            if (newQuantity > 0) {
                cartItem.setQuantity(newQuantity);
                cartItemRepository.save(cartItem);
                return true;
            }
        }
        return false;
    }


    public void saveAllItems(List<CartItem> cartItems, Long userId) {
        for (CartItem cartItem : cartItems) {
            Cart cart = cartRepository.findByUserId(userId);
            CartItem cartItemToSave = new CartItem();
            cartItemToSave.setCart(cart);
            cartItemToSave.setQuantity(cartItem.getQuantity());
            Long productId = cartItem.getProductVariation().getProduct().getId();
            Color color = cartItem.getProductVariation().getColor();
            Size size = cartItem.getProductVariation().getSize();
            ProductVariation productVariation = productVariationService.findBySizeAndColor(productId, size, String.valueOf(color));
            if (productVariation != null) {
                cartItemToSave.setProductVariation(productVariation);
            }
            assert productVariation != null;
            CartItem cartItemBefore = cartItemRepository.findByProductVariationIdAndCartId(productVariation.getId(), cart.getId());
            if (cartItemBefore != null) {
                cartItemBefore.setQuantity(cartItemBefore.getQuantity() + cartItem.getQuantity());
                cartItemRepository.save(cartItemBefore);
            } else {
                cartItemRepository.save(cartItemToSave);
            }
        }
    }


    public BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProductVariation().getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem findCartItemByProductVariationIdAndUserId(Long id, Long cartId) {
        return cartItemRepository.findByProductVariationIdAndCartId(id, cartId);
    }

    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
}
