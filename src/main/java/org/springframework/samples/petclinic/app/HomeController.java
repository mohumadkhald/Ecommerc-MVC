package org.springframework.samples.petclinic.app;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.app.products.Product;
import org.springframework.samples.petclinic.app.products.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);

//        if (authentication != null) {
//            if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
//                String name = oauth2User.getAttribute("name");
//                model.addAttribute("userName", name); // Use 'userName' as a model attribute
//            } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
//                String name = userDetails.getUsername();
//                model.addAttribute("userName", name); // Use 'userName' as a model attribute
//            }
//        }

        return "index";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("newProduct", new Product());

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
                String name = oauth2User.getAttribute("name"); // Assuming 'name' is the attribute representing the user's name
                model.addAttribute("name", name);
            } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                String name = userDetails.getUsername(); // Assuming username represents the user's name in form-based login
                model.addAttribute("name", name);
            }
        }

        return "dashboard";
    }
}