package com.kennn.electronicstore.controller.admin;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kennn.electronicstore.domain.Order;
import com.kennn.electronicstore.domain.OrderStatusEnum;
import com.kennn.electronicstore.domain.dto.OrderDTO;
import com.kennn.electronicstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/order")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping({ "/", "" })
    public String getManageOrderPage(Model model) {
        List<Order> orders = this.orderService.findAll();

        model.addAttribute("orders", orders);
        return "admin/page/order/index";
    }

    @GetMapping("/info/{id}")
    public String getOrderInfo(@PathVariable long id, Model model) {

        Optional<Order> optionalOrder = this.orderService.findById(id);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            model.addAttribute("order", order);
        }
        return "admin/page/order/info";
    }

    @GetMapping("/update/{id}")
    public String getUpdateOrderPage(@PathVariable long id, Model model) {
        Optional<Order> optionalOrder = this.orderService.findById(id);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            OrderDTO orderDTO = new OrderDTO();

            int lastSpaceIndex = order.getReceiverName().lastIndexOf(" ");
            String firstName = order.getReceiverName().substring(0, lastSpaceIndex);
            String lastName = order.getReceiverName().substring(lastSpaceIndex + 1);

            orderDTO.setFirstname(firstName);
            orderDTO.setLastname(lastName);
            orderDTO.setReceiverAddress(order.getReceiverAddress());
            orderDTO.setReceiverEmail(order.getReceiverEmail());
            orderDTO.setReceiverPhone(order.getReceiverPhone());
            orderDTO.setNote(order.getNote());
            orderDTO.setStatus(order.getStatus().name());

            model.addAttribute("order", order);
            model.addAttribute("orderDTO", orderDTO);
        }
        return "admin/page/order/update";
    }

    @PostMapping("/update")
    public String updateOrderInfo(@RequestParam("id") long id, @Valid @ModelAttribute("orderDTO") OrderDTO orderDTO,
            BindingResult result, Model model) {
        Optional<Order> optionalOrder = this.orderService.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (result.hasErrors()) {
                model.addAttribute("order", order);
                return "admin/page/order/update";
            }

            order.setReceiverName(orderDTO.getFirstname() + " " + orderDTO.getLastname());
            order.setReceiverAddress(orderDTO.getReceiverAddress());
            order.setReceiverEmail(orderDTO.getReceiverEmail());
            order.setReceiverPhone(orderDTO.getReceiverPhone());
            order.setNote(orderDTO.getNote());
            order.setStatus(OrderStatusEnum.valueOf(orderDTO.getStatus()));

            this.orderService.save(order);
        }

        return "redirect:/admin/order";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteOrderPage(@PathVariable long id, Model model) {
        Optional<Order> optionalOrder = this.orderService.findById(id);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            model.addAttribute("deleteOrder", order);
        }
        return "admin/page/order/delete";
    }

    @PostMapping("/delete")
    public String deleteOrder(@ModelAttribute("deleteOrder") Order order) {
        Optional<Order> optionalOrder = this.orderService.findById(order.getId());

        if (optionalOrder.isPresent()) {
            Order deleteOrder = optionalOrder.get();
            this.orderService.remove(deleteOrder);
        }

        return "redirect:/admin/order";
    }

}
