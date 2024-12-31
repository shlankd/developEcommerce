package com.example.ecommerce.service;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.AddressNotFoundException;
import com.example.ecommerce.exception.AddressNotMatchToUser;
import com.example.ecommerce.exception.CheckOutWithEmptyCartException;
import com.example.ecommerce.exception.ProductOutOfStockException;
import com.example.ecommerce.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EcommOrderService {

    /** The Order Repository. */
    private EcommOrderRepository ecommOrderRepository;

    /** The CartItem Repository. */
    private CartItemRepository cartItemRepository;

    /** The CartItem Repository. */
    private OrderItemRepository orderItemRepository;

    /** The Address Repository. */
    private AddressRepository addressRepository;

    /** The Product Repository. */
    private ProductRepository productRepository;

    /**
     * EcommOrderService Constructor.
     * @param ecommOrderRepository The EcommOrderRepository object.
     * @param cartItemRepository The CartItemRepository object.
     * @param orderItemRepository The OrderItemRepository object.
     * @param addressRepository The AddressRepository object.
     * @param productRepository The ProductRepository object.
     */
    public EcommOrderService(EcommOrderRepository ecommOrderRepository,
                             CartItemRepository cartItemRepository,
                             OrderItemRepository orderItemRepository,
                             AddressRepository addressRepository,
                             ProductRepository productRepository) {
        this.ecommOrderRepository = ecommOrderRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    /**
     * Gets the order list by a given user.
     * @param user The user for search the orders.
     * @return The list of orders.
     */
    public List<EcommOrder> getOrdersOfUser(EcommUser user){
        return ecommOrderRepository.findByUser(user);
    }

    /**
     * Creates an order for the user from his list of cart items.
     * @param userId The user id that requested to check out with his list of cart items.
     * @param addressId The address id to associate the order with.
     * @return Returns the created order.
     * @throws CheckOutWithEmptyCartException
     * @throws AddressNotFoundException
     */
    public EcommOrder createOrder(Long userId, Long addressId) throws
            CheckOutWithEmptyCartException, AddressNotFoundException,
            ProductOutOfStockException, AddressNotMatchToUser {

        // The method createOrder called in PayPalController createPayment method after the confirmation
        // of the user id match with the authenticated user that requests to make the payment.
        // So there is no need to check if the user id exists.

        // Finds the user's cart by the given user id.
        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(userId);

        // Condition for check out procedure with empty cart exception.
        if(cartItems.isEmpty()){
            throw new CheckOutWithEmptyCartException();
        }

        // Creates a new order for the user.
        EcommOrder newOrder = new EcommOrder();

        // Creates temporary user to set the user field of the EcommOrder to associate the new order with the user.
        EcommUser tmpUser = new EcommUser();
        tmpUser.setId(userId);

        // Sets the new order with the user entity that contains the user id.
        newOrder.setUser(tmpUser);

        // Finds the given address (that the user gave) by address id to set the order address.
        Optional<Address> opAddress = addressRepository.findById(addressId);

        // Throws exception of invalid addressId if the address not found by the addressId.
        if(opAddress.isEmpty()){
            throw new AddressNotFoundException();
        }

        // Throws exception if the user that associated with the given address
        // does not match with the user that requested to create an order.
        if(opAddress.get().getUser().getId() != userId){
            throw new AddressNotMatchToUser();
        }

        // Sets the order address.
        newOrder.setAddress(opAddress.get());

        // Saves the new order.
        EcommOrder savedNewOrder = ecommOrderRepository.save(newOrder);

        List<OrderItem> orderItemList = savedNewOrder.getOrderItems();

        // Loop that converts each CartItem to OrderItem
        // and adds it to the list of order items pf the created order.
        for(CartItem cartItem : cartItems){

            // Gets the product entity from the ordered item.
            Product productOrdered = cartItem.getProduct();

            Integer productStock = productOrdered.getProductQuantity();
            Double productPrice = productOrdered.getPrice();
            Integer cartItemQuantity = cartItem.getCartItemQuantity();

            // Checks if there is enough items in the stock for the ordered item.
            if(productStock < cartItemQuantity){
                // Cancel The created order.
                cancelOrder(savedNewOrder);
                // Throws product out of stock exception.
                throw new ProductOutOfStockException();
            }

            // Creates a new order item.
            OrderItem orderItem = new OrderItem();

            // Sets the order item according to the current cart item
            orderItem.setId(null); // Sets null for generates id according to the created order.
            orderItem.setOrder(savedNewOrder); // Associates the order item to the created order.
            orderItem.setProduct(productOrdered); // Sets the product of the order item.
            orderItem.setQuantity(cartItemQuantity); // The cartItem quantity with minimum 1.

            // Updates the order total payment with the current order item.
            savedNewOrder.setOrderTotalPayment(
                    savedNewOrder.getOrderTotalPayment()+productPrice*cartItemQuantity);

            // Saves the created order item.
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            // Adds the ordered item to the list of the order.
            orderItemList.add(savedOrderItem);

            // Updates the stock of the product.
            productOrdered.setProductQuantity(productStock - cartItemQuantity);
            // Saves the product with the updated product quantity in the Product Repository.
            productRepository.save(productOrdered);

            // Deletes the cart item after the order item was added in to the list of the created order.
            cartItemRepository.delete(cartItem);
        }
        return savedNewOrder;
    }

    /**
     * Cancel the given oder by delete each ordered item and updates the product quantity
     * by the quantity of the canceled ordered item.
     * @param order The order to cancel (to delete).
     */
    public void cancelOrder(EcommOrder order) {
        // Gets from the order the ordered item list
        List<OrderItem> itemsToCancel = order.getOrderItems();

        if (!itemsToCancel.isEmpty()){
            for (OrderItem itemToCancel : itemsToCancel) {
                // Gets the product from the ordered item.
                Product productOrdered = itemToCancel.getProduct();

                Integer productStock = productOrdered.getProductQuantity();
                Integer quantityItemCanceled = itemToCancel.getQuantity();

                // Updates the stock of the product by the canceled ordered item.
                productOrdered.setProductQuantity(productStock + quantityItemCanceled);

                // Deletes the ordered item.
                orderItemRepository.delete(itemToCancel);

                // Saves the updated product in the product repository.
                productRepository.save(productOrdered);
            }
        }
        ecommOrderRepository.delete(order);
    }

}
