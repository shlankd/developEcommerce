package com.example.ecommerce.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    /**
     * This method creates the PayPal payment.
     * @param totalPay The total amount of payment.
     * @param currency The currency of payment.
     * @param method The payment method like PayPal, credit card etc.
     * @param intent The payment intent with Enum values of: "sale", "authorize" and "order".
     *               "sale": Makes an immediate payment.
     *               "authorize": Authorizes a payment for capture later.
     *               "order": Creates an order.
     * @param description The description of the payment.
     * @param cancelUrl The url direction for redirect url if the user requests to cancel the payment.
     * @param successUrl The url direction for redirect url if the payment process has done successfully.
     * @return Returns the created Payment type of com.paypal.api.payments
     * @throws PayPalRESTException
     */
    public Payment createPayment(Double totalPay, String currency, String method, String intent,
                                 String description, String cancelUrl, String successUrl)
            throws PayPalRESTException {

        // Sets the amount of payment with the currency.
        Amount amount = new Amount();
        amount.setCurrency(currency);
        // Make sure that the total payment will be written correctly with the currency.
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", totalPay));

        // Sets the payment transaction with the amount of payment.
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        // Sets The list of transactions of the payment.
        List<Transaction> listTransactions = new ArrayList<>();
        listTransactions.add(transaction);

        // Sets the payment method by the Payer object.
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        // Sets the redirect urls object with the cancel url direction and the success url direction.
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        // Sets the new Payment to be creates.
        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(listTransactions);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext); // DELETE: creates payment with the api context from PayPalConfig.
    }

    /**
     * Executes the created PayPal payment after the user confirmed it.
     * @param paymentId The payment id DELETE:(from the created payment).
     * @param payerId The payer id DELETE:(from the created payment).
     * @return Returns the executed payment.
     * @throws PayPalRESTException
     */
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        // Sets the Payment object with the payment id.
        Payment payment = new Payment();
        payment.setId(paymentId);

        // Sets the PaymentExecution object with the payer id.
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        // The payment that sets with the payment id, executes the payment
        // with the api context from PayPalConfig and PaymentExecute object.
        return payment.execute(apiContext, paymentExecution);
    }
}
