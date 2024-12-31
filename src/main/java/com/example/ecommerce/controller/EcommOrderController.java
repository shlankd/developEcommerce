//TODO: erange this file.
package com.example.ecommerce.controller;

import com.example.ecommerce.entity.EcommOrder;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.CartItemService;
import com.example.ecommerce.service.EcommOrderService;
import com.example.ecommerce.service.PayPalService;
import com.example.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Order Controller to handle requests to create, update and view orders.
 */
@RestController
public class EcommOrderController {

    /** Instance of OrderService. */
    private EcommOrderService ecommOrderService;

    /** Instance of UserService. */
    private UserService userService;

    /** Instance of PayPalService. */
    private PayPalService payPalService;

    /** Instance of CartItemService. */
    private CartItemService cartItemService;


    private EcommUserRepository ecommUserRepository;

    /**
     * Order Controller Constructor.
     * @param ecommOrderService
     */
    public EcommOrderController(EcommOrderService ecommOrderService, UserService userService,
                                PayPalService payPalService, CartItemService cartItemService,
                                EcommUserRepository ecommUserRepository) {
        this.ecommOrderService = ecommOrderService;
        this.userService = userService;

        this.payPalService = payPalService;
        this.cartItemService = cartItemService;

        this.ecommUserRepository = ecommUserRepository;
    }

    /**
     * An endpoint that gets all the orders of a specific user.
     * @param user The user provided by spring security context.
     * @return The list of all orders that done by the user.
     */
    @GetMapping("/user/{userId}/getOrders")
    public ResponseEntity<List<EcommOrder>> getOrdersOfUser(@AuthenticationPrincipal EcommUser user, @PathVariable Long userId){
        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ecommOrderService.getOrdersOfUser(user));
    }
//
//    /**
//     * Preform the order and payment creation.
//     * @param userId The id of the user that requests to create an order and payment from his cart.
//     * @param orderSetBody The information body to create the order and payment.
//     * @return Returns redirect view depend on the links of the payment object.
//     */
//    @PostMapping("/user/{userId}/createOrderAndPayment")
//    public RedirectView createOrder(@AuthenticationPrincipal EcommUser user,
//                                    @PathVariable Long userId,
//                                    @RequestBody OrderSetBody orderSetBody) {
//
////        Optional<EcommUser> opUser = ecommUserRepository.findById(userId);
////
////        boolean isAuthUserMatchWithUserId = false;
////        boolean isUserAddressesEmpty = true;
////        EcommUser user = null;
////
////        if(opUser.isPresent()){
////            isAuthUserMatchWithUserId = true;
////            isUserAddressesEmpty = false;
////            user = opUser.get();
////        }
//
//        //Long userId = orderSetBody.getUserId();
//
//        boolean isAuthUserMatchWithUserId = userService.isAuthUserHasAMatchOfUserID(user, userId);
//        //boolean isUserAddressesEmpty = user.getAddresses().isEmpty();
//
//        // Creates a payment only if the user id match with the id of the authenticate user
////         // and the user's addresses list is not empty.
//        if (isAuthUserMatchWithUserId ){ // && !isUserAddressesEmpty) {
//            //if (userService.isAuthUserHasAMatchOfUserID(user, userId) && !user.getAddresses().isEmpty()) {
//
//            try {
//
//                // Creates the order by the user's cart.
//                EcommOrder order = ecommOrderService.createOrder(userId, orderSetBody.getAddressId());
//                //EcommOrder order = ecommOrderService.createOrder(userId, addressId);
//
////                List<Address> userAddresses = user.getAddresses();
////                // Sets the address field in the order. (sets the last address the user add)
////                Address addressOfTheUser = userAddresses.getLast();
////
////                // Sets the order address.
////                order.setAddress(addressOfTheUser);
//
//                // Gets the total price of the order.
//                //Double totalPay = ecommOrderService.getTotalPriceOfOrder(order);
//                Double totalPay = order.getOrderTotalPayment();
//
//                // Creates the payment of the created order.
//                Payment payment = payPalService.createPayment(
//                        totalPay,
//                        orderSetBody.getCurrency(),
//                        //currency,
//                        "paypal",
//                        "sale",
//                        orderSetBody.getDescription(),
//                        //description,
//                        PAYMENT_CANCEL_URL,
//                        PAYMENT_SUCCESS_URL
//                );
//                // Loop that runs the links of the created payment to extract the approval link.
//                for (Links link : payment.getLinks()) {
//                    if (link.getRel().equals("approval_url")) { // The approval link.
//                        // Returns the redirect view from the approval link.
//                        return new RedirectView(link.getHref()); // Could be successUrl or cancelUrl
//                    }
//                }
//            }
//            catch(AddressNotFoundException e){
//                System.out.print("Error of The input address was not found.");
//            }
//            catch(AddressNotMatchToUser e){
//                System.out.print("Error of The input address is not related to the user.");
//            }
//            catch(CheckOutWithEmptyCartException e){
//                System.out.println("Error to make payment with empty cart.");
//            }
//            catch(ProductOutOfStockException e){
//                // Gets the cart of the user.
//                List<CartItem> cart = user.getCartItems();
//                // Clear the cart from all items.
//                cartItemService.clearCartFromAllItems(cart);
//
//                System.out.println("Error one of the items is out of stock.");
//            }
//            catch (PayPalRESTException e) {
//                e.printStackTrace();
//            }
//        }
//        if(!isAuthUserMatchWithUserId){
//            System.out.println("Error the user id does not match with the authenticated user id.");
//        }
////        if(isUserAddressesEmpty){
////            System.out.println("Error the user's addresses list is empty.");
////        }
//        return new RedirectView("/user/{userId}/payment/error");
//    }

}
