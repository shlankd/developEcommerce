package com.example.ecommerce.controller;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.EcommOrder;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.AddressNotFoundException;
import com.example.ecommerce.exception.AddressNotMatchToUser;
import com.example.ecommerce.exception.CheckOutWithEmptyCartException;
import com.example.ecommerce.exception.ProductOutOfStockException;
import com.example.ecommerce.repository.EcommOrderRepository;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.CartItemService;
import com.example.ecommerce.service.EcommOrderService;
import com.example.ecommerce.service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

import static com.example.ecommerce.util.EcommConstants.PAYMENT_CANCEL_URL;
import static com.example.ecommerce.util.EcommConstants.PAYMENT_SUCCESS_URL;

@Controller
public class PayPalController {


    private PayPalService paypalService;
    private EcommOrderService ecommOrderService;

    private EcommOrderRepository ecommOrderRepository;
    private CartItemService cartItemService;

    private EcommUserRepository ecommUserRepository;

    /**
     * Constructor of PayPalController.
     * @param paypalService The PaypalService object.
     * @param ecommOrderService The EcommOrderService object.
     * @param ecommOrderRepository The EcommOrderRepository object.
     * @param cartItemService The CartItemService object.
     * @param ecommUserRepository The EcommUserRepository object.
     */
    public PayPalController(PayPalService paypalService,
                            EcommOrderService ecommOrderService,
                            EcommOrderRepository ecommOrderRepository,
                            CartItemService cartItemService,
                            EcommUserRepository ecommUserRepository){
        this.paypalService = paypalService;
        this.ecommOrderService = ecommOrderService;
        this.ecommOrderRepository = ecommOrderRepository;
        this.cartItemService = cartItemService;
        this.ecommUserRepository = ecommUserRepository;
    }

    /**
     * Gets The home page of PayPal payment.
     * @return Returns the index view of PayPal index.
     */
    @GetMapping("/payment")
    public String home(){
        return "paypalIndex";
    }

    /**
     * Perform the creation order and PayPal payment from the user cart.
     * If the user confirmed the payment and there is no error on the payment than returns the
     * RedirectView of payment success, if an error occurred during the creation order or payment
     * than returns RedirectView of payment error and if the user cancelled the payment
     * than returns the redirectView of payment cancel.
     * @param userIdParam The userId to get the user entity.
     * @param addressIdParam Contains the addressId to set the order address.
     * @param currency The currency of the payment to set.
     * @param description The payment description.
     * @return Returns RedirectView that depends on the payment status.
     */
    @PostMapping("/payment/create")
    public RedirectView createPayment(@RequestParam("userId") String userIdParam,
                                      @RequestParam("addressId") String addressIdParam,
                                      @RequestParam("currency") String currency,
                                      @RequestParam("description") String description) {

        Long userId = Long.valueOf(userIdParam);
        Long addressId = Long.valueOf(addressIdParam);

        Optional<EcommUser> opUser = ecommUserRepository.findById(userId);

        // Creates a payment only if the user id match with the id of the authenticate user.
        if (opUser.isPresent() && !opUser.get().getAddresses().isEmpty()) {

            try {
                // Creates the order by the user's cart.
                EcommOrder order = ecommOrderService.createOrder(userId, addressId);

                // Gets the total price of the order.
                Double totalPay = order.getOrderTotalPayment();

                String addPathVarUrl = "/"+userId+"/"+order.getId();
                String cancel_url = PAYMENT_CANCEL_URL + addPathVarUrl;

                // Creates the payment of the created order.
                Payment payment = paypalService.createPayment(
                        totalPay,
                        currency,
                        "paypal",
                        "sale",
                        description,
                        cancel_url,
                        PAYMENT_SUCCESS_URL
                );
                // Loop that runs the links of the created payment to extract the approval link.
                for (Links link : payment.getLinks()) {
                    if (link.getRel().equals("approval_url")) { // The approval link.

                        // Returns the redirect view from the approval link.
                        return new RedirectView(link.getHref()); // Could be successUrl or cancelUrl
                    }
                }
            }

            catch(AddressNotFoundException e){
                System.out.print("Error of The input address was not found.");
            }
            catch(AddressNotMatchToUser e){
                System.out.print("Error of The input address is not related to the user.");
            }
            catch(CheckOutWithEmptyCartException e){
                System.out.println("Error to make payment with empty cart.");
            }
            catch(ProductOutOfStockException e){
                // Gets the cart of the user.
                List<CartItem> cart = opUser.get().getCartItems();
                // Clear the cart from all items.
                cartItemService.clearCartFromAllItems(cart);

                System.out.println("Error one of the items is out of stock.");
            }
            catch (PayPalRESTException e) {
                e.printStackTrace();
            }
        }
        return new RedirectView("/payment/error");
    }

    /**
     * This method executes the payment after the user filled the necessary payment information
     * and confirmed it.
     * @param paymentId The payment id of the created payment from PayPal restAPI.
     * @param payerId The payer id of the created payment from PayPal restAPI.
     * @return Returns the view of payment success if the payment executed successfully.
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId){
        try{
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                return "paymentSuccess";
            }
        }
        catch(PayPalRESTException e){
            e.printStackTrace();
        }
        return "paymentSuccess";
    }

    /**
     * This method executes the payment and order cancellation after the user chose to cancel the payment.
     * @param userId The user id that cancelled his payment.
     * @param orderId The order id of the payment that has been cancelled.
     * @return Returns the payment cancel view.
     */
    @GetMapping("/payment/cancel/{userId}/{orderId}")
    public String paymentCancel(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId){

        Optional<EcommOrder> opOrder = ecommOrderRepository.findById(orderId);
        if(opOrder.isPresent()){
            EcommOrder orderToCancel = opOrder.get();
            if(orderToCancel.getUser().getId() == userId){
                ecommOrderService.cancelOrder(orderToCancel);
            }
        }

        return "paymentCancel";
    }

    /**
     * This method returns payment error view if an error occurred during the creation of payment and order.
     * @return Returns the payment error view.
     */
    @GetMapping("/payment/error")
    public String paymentError(){
        return "paymentError";
    }
}
