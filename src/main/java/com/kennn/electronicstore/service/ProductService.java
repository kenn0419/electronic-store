package com.kennn.electronicstore.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kennn.electronicstore.domain.Cart;
import com.kennn.electronicstore.domain.CartDetail;
import com.kennn.electronicstore.domain.Order;
import com.kennn.electronicstore.domain.OrderDetail;
import com.kennn.electronicstore.domain.OrderStatusEnum;
import com.kennn.electronicstore.domain.Product;
import com.kennn.electronicstore.domain.User;
import com.kennn.electronicstore.domain.dto.OrderDTO;
import com.kennn.electronicstore.repository.CartDetailRepository;
import com.kennn.electronicstore.repository.CartRepository;
import com.kennn.electronicstore.repository.OrderDetailRepository;
import com.kennn.electronicstore.repository.OrderRepository;
import com.kennn.electronicstore.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class ProductService {
    private UserService userService;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private CartDetailRepository cartDetailRepository;
    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;

    public ProductService(UserService userService, ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Product> findAll() {
        return this.productRepository.findAll();
    }

    public List<Product> findPublishProducts() {
        return this.productRepository.findByStatusTrue();
    }

    public Product save(Product product) {
        return this.productRepository.save(product);
    }

    public Optional<Product> findById(long id) {
        return this.productRepository.findById(id);
    }

    public void remove(Product product) {
        this.productRepository.delete(product);
    }

    public void handleAddToCart(String email, long productId, HttpSession session, long quantity) {
        User user = this.userService.findByEmail(email);

        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);
                cart = this.cartRepository.save(newCart);
            }
            Optional<Product> optionalProduct = this.productRepository.findById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                CartDetail currenCartDetail = this.cartDetailRepository.findByCartAndProduct(cart, product);
                if (currenCartDetail == null) {
                    CartDetail newCartDetail = new CartDetail();
                    newCartDetail.setCart(cart);
                    newCartDetail.setProduct(product);
                    newCartDetail.setPrice(product.getPrice());
                    newCartDetail.setQuantity(quantity);
                    this.cartDetailRepository.save(newCartDetail);

                    cart.setSum(cart.getSum() + 1);
                    this.cartRepository.save(cart);

                    session.setAttribute("sum", cart.getSum());
                } else {
                    currenCartDetail.setQuantity(currenCartDetail.getQuantity() + quantity);
                    this.cartDetailRepository.save(currenCartDetail);
                }
            }
        }
    }

    public int handleRemoveProductFromCart(long cartDetailId, Cart cart, HttpSession session) {
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            if (cartDetails != null) {
                for (CartDetail cartDetail : cartDetails) {
                    if (cartDetail.getId() == cartDetailId) {
                        this.cartDetailRepository.delete(cartDetail);
                        if (cart.getSum() > 1) {
                            int newSum = cart.getSum() - 1;
                            cart.setSum(newSum);
                            this.cartRepository.save(cart);
                            session.setAttribute("sum", newSum);
                            return newSum;
                        } else {
                            session.setAttribute("sum", 0);
                            this.cartRepository.delete(cart);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public void handleOrderProducts(User user, Cart cart, HttpSession session, OrderDTO orderDTO) {
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            double totalPrice = cartDetails.stream()
                    .mapToDouble(cartDetail -> cartDetail.getProduct().getPrice() * cartDetail.getQuantity())
                    .sum();
            if (cartDetails != null) {
                Order order = new Order();

                order.setUser(user);
                order.setReceiverName(orderDTO.getFirstname() + " " + orderDTO.getLastname());
                order.setNote(orderDTO.getNote());
                order.setReceiverEmail(orderDTO.getReceiverEmail());
                order.setReceiverPhone(orderDTO.getReceiverPhone());
                order.setReceiverAddress(orderDTO.getReceiverAddress());
                order.setStatus(OrderStatusEnum.PENDING);
                order.setTotalPrice(totalPrice);
                order.setOrderDate(new Date());

                order = this.orderRepository.save(order);

                for (CartDetail cartDetail : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();

                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cartDetail.getProduct());
                    orderDetail.setPrice(cartDetail.getPrice());
                    orderDetail.setQuantity(cartDetail.getQuantity());

                    this.orderDetailRepository.save(orderDetail);

                    this.cartDetailRepository.delete(cartDetail);
                }
                this.cartRepository.delete(cart);
                session.setAttribute("sum", 0);
            }
        }
    }
}
