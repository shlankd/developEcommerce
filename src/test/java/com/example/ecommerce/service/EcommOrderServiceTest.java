package com.example.ecommerce.service;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.EcommOrderRepository;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * This class tests methods from the EcommOrderService class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class EcommOrderServiceTest {

    @Autowired
    private EcommUserRepository ecommUserRepository;

    @Autowired
    private EcommOrderService ecommOrderService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EcommOrderRepository ecommOrderRepository;

    @Test
    @Transactional
    public void testCreateOrderWithCheckOutWithEmptyCartException(){

        Optional<EcommUser> opTestUser2 = ecommUserRepository.findByUsernameIgnoreCase("testUser2");

        Assertions.assertTrue(opTestUser2.isPresent(), "The testUser2 should be present");

        EcommUser testUser2 = opTestUser2.get();
        Long testUser2Id = testUser2.getId();
        Long testUser2AddressId = testUser2.getAddresses().get(0).getId();

        // Test the create order function throws exception CheckOutWithEmptyCartException
        // with testUser2 that has empty cart.
        Assertions.assertThrows(CheckOutWithEmptyCartException.class,
                () -> ecommOrderService.createOrder(testUser2Id, testUser2AddressId),
                "The order should not be created with empty cart.");
    }

    @Test
    @Transactional
    public void testCreateOrderWithAddressNotFoundException(){

        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");

        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present");

        EcommUser testUser1 = opTestUser1.get();
        Long testUser1Id = testUser1.getId();
        Long addressIdThatNotExists = 77L;

        // Test the create order function throws exception AddressNotFoundException
        // with address id that not exists.
        Assertions.assertThrows(AddressNotFoundException.class,
                () -> ecommOrderService.createOrder(testUser1Id, addressIdThatNotExists),
                "The order should not be created with address that not exists in the repository.");
    }

    @Test
    @Transactional
    public void testCreateOrderWithAddressNotMatchToUser(){

        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");
        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present.");
        Long testUser3Id = opTestUser3.get().getId();

        Optional<EcommUser> opTestUser2 = ecommUserRepository.findByUsernameIgnoreCase("testUser2");
        Assertions.assertTrue(opTestUser2.isPresent(), "The testUser2 should be present.");
        EcommUser testUser2 = opTestUser2.get();
        Long testUser2AddressId = testUser2.getAddresses().get(0).getId();

        // Test the create order function throws exception AddressNotMatchToUser
        // with the id of testUser3 and with address id that belong to testUser2.
        Assertions.assertThrows(AddressNotMatchToUser.class,
                () -> ecommOrderService.createOrder(testUser3Id, testUser2AddressId),
                "The order should not be created with address that doesn't belong to the testUser3.");
    }

    @Test
    @Transactional
    public void testCreateOrderWithProductOutOfStockException(){

        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");

        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present");

        EcommUser testUser3 = opTestUser3.get();
        Long testUser3Id = testUser3.getId();
        Long testUser3AddressId = testUser3.getAddresses().get(0).getId();

        Optional<Product> opProduct1 = productRepository.findProductByName("Product Test #1");
        Assertions.assertTrue(opProduct1.isPresent(), "The Product Test #1 should be present.");

        // The product 1 quantity before create the order that contains cart item
        // of product 1 with quantity 5 at the cart testUser3.
        Integer product1QuantityBeforeCreateOrder = opProduct1.get().getProductQuantity();

        // Test the create order function throws exception ProductOutOfStockException
        // with testUser3 that has cart item with quantity that bigger than the product quantity.(from data.sql)
        Assertions.assertThrows(ProductOutOfStockException.class,
                () -> ecommOrderService.createOrder(testUser3Id, testUser3AddressId),
                "The order should not be created with cart item that has bigger quantity " +
                        "than the product quantity.");

        // The product 1 quantity after create the order that should be cancel due to the product out of stock.
        Integer product1QuantityAfterCreateOrder = opProduct1.get().getProductQuantity();

        // The product 1 quantity should be the same after the order has been canceled.
        Assertions.assertTrue(product1QuantityBeforeCreateOrder == product1QuantityAfterCreateOrder,
                "The quantity of the Product Test #1 should be the same after the create order of testUser3.");
    }

    @Test
    @Transactional
    public void testCreateOrderWithNoException(){

        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");

        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present");

        EcommUser testUser1 = opTestUser1.get();
        Long testUser1Id = testUser1.getId();
        Long testUser1AddressId = testUser1.getAddresses().get(0).getId();

        Optional<Product> opProduct2 = productRepository.findProductByName("Product Test #2");
        Assertions.assertTrue(opProduct2.isPresent(), "The Product Test #2 should be present.");

        // The product 2 quantity before create the order that contains cart item
        // of product 2 with quantity 5 at the cart testUser1.
        Integer product2QuantityBeforeCreateOrder = opProduct2.get().getProductQuantity();

        // Test the order create function with no exception throws.
        Assertions.assertDoesNotThrow(
                () -> ecommOrderService.createOrder(testUser1Id,testUser1AddressId),
                "The order should be created successfully.");

        // The product 2 quantity after create the order that contains cart item
        // of product 2 with quantity 5 at the cart testUser1.
        Integer product2QuantityAfterCreateOrder = opProduct2.get().getProductQuantity();

        // The product 2 quantity should be smaller after the order has been created.
        Assertions.assertTrue(product2QuantityBeforeCreateOrder > product2QuantityAfterCreateOrder,
                "The quantity of the Product Test #1 should NOT be the same after the create order of testUser1.");


    }

    @Test
    @Transactional
    public void testCancelOrder(){

        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        EcommUser testUser1 = opTestUser1.get();

        List<EcommOrder> testUser1OrderList = ecommOrderRepository.findByUser(testUser1);
        Assertions.assertFalse(testUser1OrderList.isEmpty(), "The order list of testUser1 should not be empty.");
        EcommOrder orderToCancel = testUser1OrderList.get(testUser1OrderList.size()-1);

        /////////////////////////////////////////////////////////////////////////////////////////////
//        List<OrderItem> orderItemList = orderToCancel.getOrderItems();
//        Product product3 = null;
//        for(OrderItem item : orderItemList){
//            if(item.getProduct().getName().equals("Product Test #3")){
//                product3 = item.getProduct();
//            }
//        }
//        Assertions.assertFalse(product3 == null, "product3 should be not null.");
        /////////////////////////////////////////////////////////////////////////////////////////////

        Long orderIdToCancel = orderToCancel.getId();
        ecommOrderService.cancelOrder(orderToCancel);

        testUser1OrderList = ecommOrderRepository.findByUser(testUser1);
        Assertions.assertFalse(testUser1OrderList.isEmpty(), "The order list of testUser1 should not be empty even after the order cancellation.");
        EcommOrder order = testUser1OrderList.get(testUser1OrderList.size()-1);

        Long orderId = order.getId();

        Assertions.assertTrue(orderId != orderIdToCancel,
                "The last order from the list should not be the same as the canceled order id.");
    }
}
