package com.example.ecommerce.controller;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.service.CartItemService;
import com.example.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This Rest Controller class handles the cart item requests that needs
 * the user role and user authentication to get access.
 */
@RestController
public class CartController {

    /** Instance of CartItemRepository. */
    private CartItemRepository cartItemRepository;

    /** Instance of CartService. */
    private CartItemService cartItemService;

    /** Instance of UserService. */
    private UserService userService;

    /**
     * CartController Constructor.
     * @param cartItemService The CartService object.
     * @param userService The UserService object.
     * @param cartItemRepository The CartItemRepository object.
     */
    public CartController(CartItemService cartItemService, UserService userService,
                          CartItemRepository cartItemRepository) {
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Gets the user's cart items by given user id.
     * @param user The authenticated user.
     * @param userId The given user id.
     * @return Returns list of cart items that belong to the user id that match with
     *         the id of the authenticated user else returns response entity forbidden
     *         if the user id not match with the id of the authenticated user.
     */
    @GetMapping("/user/{userId}/getCartItems")
    public ResponseEntity<List<CartItem>> getUserCart(@AuthenticationPrincipal EcommUser user,
                                                      @PathVariable Long userId){
        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(cartItemRepository.findCartItemsByUserId(userId));
    }

    /**
     * Handles the user's request to add a product to his cart.
     * @param user The authenticated user.
     * @param userId The given user id.
     * @param itemToAddCart The cart item to add to the cart.
     * @param productId The product id to set the added cart item.
     * @return Returns response entity of cart item.
     */
    @PutMapping("/user/{userId}/addItemToCart/{productId}")
    public ResponseEntity<CartItem> addItemToCart(@AuthenticationPrincipal EcommUser user,
                                                  @PathVariable Long userId,
                                                  @RequestBody CartItem itemToAddCart,
                                                  @PathVariable Long productId){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        itemToAddCart.setCartItemId(null);

        // Creates temporary user to set the user field of the CartItem to associate the cart item to the user.
        EcommUser tmpUser = new EcommUser();
        tmpUser.setId(userId);

        // Associates the cart item with the user id.
        itemToAddCart.setUser(tmpUser);

        try{
            CartItem addedItem = cartItemService.addItemToCart(itemToAddCart, productId, userId);
            return ResponseEntity.status(HttpStatus.OK).body(addedItem);
        }
        catch(ProductIdNotExistsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        catch(ProductOutOfStockException | QuantityOfSelectedItemNotAvailableException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    /**
     * Allows the user to edit the item quantity that exists from his cart.
     * @param user The authenticated user.
     * @param userId The given user id.
     * @param cartItemToEdit The cart item with the updated data (the updated item quantity).
     * @param cartItemId The cart item id from the user's cart to edit.
     * @return Returns response entity of cart item.
     */
    @PatchMapping("/user/{userId}/editCartItemQuantity/{cartItemId}")
    public ResponseEntity<CartItem> editCartItemQuantity(@AuthenticationPrincipal EcommUser user, @PathVariable Long userId,
                                                         @RequestBody CartItem cartItemToEdit, @PathVariable Long cartItemId){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try{
            CartItem cartItemEdited = cartItemService.editCartItemQuantity(userId, cartItemToEdit, cartItemId);
            return ResponseEntity.status(HttpStatus.OK).body(cartItemEdited);
        }
        catch(UserNotMatchedException | CartItemIdNotExistsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        catch(QuantityOfSelectedItemNotAvailableException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        catch(SelectedCartItemNotMatchWithCartItemIdException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Allows the user to delete an item his cart.
     * @param user The authenticated user.
     * @param userId The given user id.
     * @param cartItemId The cart item to delete from the user's cart.
     * @return Returns response entity of cart item.
     */
    @DeleteMapping("/user/{userId}/deleteCartItem/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@AuthenticationPrincipal EcommUser user,
                                               @PathVariable Long userId, @PathVariable Long cartItemId){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try{
            cartItemService.deleteCartItem(userId, cartItemId);
        }
        catch(CartItemIdNotExistsException | UserNotMatchedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }
}
