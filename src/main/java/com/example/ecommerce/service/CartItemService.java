package com.example.ecommerce.service;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class contains methods that service cart operations.
 */
@Service
public class CartItemService {

    /** The CartItem Repository. */
    private CartItemRepository cartItemRepository;

    /** The Product Repository. */
    private ProductRepository productRepository;

    /** The EcommUser Repository. */
    private EcommUserRepository ecommUserRepository;


    /**
     * CartItemService Constructor.
     * @param cartItemRepository The CartItemRepository object.
     * @param productRepository The ProductRepository object.
     * @param ecommUserRepository The EcommUserRepository object.
     */
    public CartItemService(CartItemRepository cartItemRepository, ProductRepository productRepository,
                           EcommUserRepository ecommUserRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.ecommUserRepository = ecommUserRepository;
    }

    /**
     * This method enables to add an item (product) to the cart.
     * @param cartItemToAdd The CartItem object to add to the user's cart.
     * @return Returns the CartItem that added to the cart.
     */
    public CartItem addItemToCart(CartItem cartItemToAdd, Long productId, Long userId) throws
            ProductIdNotExistsException, ProductOutOfStockException,
            QuantityOfSelectedItemNotAvailableException {

        // Finds the Product from the repository by the product id.
        Optional<Product> opProduct = productRepository.findById(productId);

        // If the selected item to add to the cart doesn't exist.
        if(opProduct.isEmpty()){
            throw new ProductIdNotExistsException();
        }

        // Gets the selected item (product) to add to the cart.
        Product product = opProduct.get();

        // If the quantity of the product is out of stock.
        if(product.getProductQuantity() <= 0){
            throw new ProductOutOfStockException();
        }


        // Finds the user entity by the given user id to get the user's cart.

        // Because it is known from CartItemController (line 54) that the user id param is match with
        // the id of the authenticated user, so it is certain that the user id exists in the user repository.
        EcommUser user = ecommUserRepository.findById(userId).get();

        // Gets the user's cart
        List<CartItem> userCart = user.getCartItems();

        // Gets the cart item from user's cart with the same product id to add to the cart.
        CartItem existsProdCartItem = getCartItemWithProductIdThatAlreadyExistsInUserCart(userCart, productId);

        if(existsProdCartItem != null){
            // The total quantity of the cart item with the product id.
            Integer totalQuantity = cartItemToAdd.getCartItemQuantity() + existsProdCartItem.getCartItemQuantity();
            // Sets the total quantity to the requested cart item to add.
            cartItemToAdd.setCartItemQuantity(totalQuantity);
            // Sets the id of the requested cart item to add with the same cart item id that has the same product.
            cartItemToAdd.setCartItemId(existsProdCartItem.getCartItemId());
        }

        // If the quantity of the requested item to add to the cart is greater than the quantity of the product in the inventory.
        if(product.getProductQuantity() - cartItemToAdd.getCartItemQuantity() < 0){
            throw new QuantityOfSelectedItemNotAvailableException();
        }

        // Associates the selected item (product) to the cartItem.
        cartItemToAdd.setProduct(product);

        CartItem savedCartItem = cartItemRepository.save(cartItemToAdd);

        if(existsProdCartItem == null){
            userCart.add(savedCartItem);
        }

        return savedCartItem;
    }

    /**
     * This method performs to edit the quantity of the cart item.
     * @param userId The user id that requested to edit the quantity of the cart item.
     * @param cartItemToEdit The cart item to edit.
     * @param cartItemId The id of the cart item to edit.
     * @return Returns the edited cart item.
     * @throws CartItemIdNotExistsException
     * @throws QuantityOfSelectedItemNotAvailableException
     * @throws UserNotMatchedException
     * @throws SelectedCartItemNotMatchWithCartItemIdException
     */
    public CartItem editCartItemQuantity(Long userId, CartItem cartItemToEdit, Long cartItemId) throws
            CartItemIdNotExistsException, QuantityOfSelectedItemNotAvailableException,
            UserNotMatchedException, SelectedCartItemNotMatchWithCartItemIdException {

        // If the selected cart item id doesn't match with the requested cartItem id
        if(cartItemToEdit.getCartItemId() != cartItemId){
            throw new SelectedCartItemNotMatchWithCartItemIdException();
        }

        Optional<CartItem> opCartItem = cartItemRepository.findById(cartItemId);

          // If the cart item is not exists.
        if(opCartItem.isEmpty()){
            throw new CartItemIdNotExistsException();
        }

        // If user id that associate with the cart item doesn't equal to user id that requested to edit this cart item.
        if(opCartItem.get().getUser().getId() != userId){
            throw new UserNotMatchedException();
        }

        // Sets the product's quantity.
        Integer productQuantity = opCartItem.get().getProduct().getProductQuantity();

        // If the new cart item quantity to set is greater than the quantity of the product in the inventory.
        if(productQuantity < cartItemToEdit.getCartItemQuantity()){
            throw new QuantityOfSelectedItemNotAvailableException();
        }

        // Sets the association of the edited cart item with the user and the product.
        cartItemToEdit.setUser(opCartItem.get().getUser());
        cartItemToEdit.setProduct(opCartItem.get().getProduct());

        return cartItemRepository.save(cartItemToEdit);
    }

    /**
     * Deletes a cart item from the user's cart.
     * @param userId The id of the user that requested to delete an item from his cart.
     * @param cartItemId The cart item id to delete from the cart.
     * @throws CartItemIdNotExistsException
     * @throws UserNotMatchedException
     */
    public void deleteCartItem(Long userId, Long cartItemId) throws
            CartItemIdNotExistsException, UserNotMatchedException {

        Optional<CartItem> opCartItem = cartItemRepository.findById(cartItemId);

        if(opCartItem.isEmpty()){
            throw new CartItemIdNotExistsException();
        }

        CartItem cartItemToDelete = opCartItem.get();

        // If user id that associate with the cart item doesn't equal to user id that requested to edit this cart item.
        if(cartItemToDelete.getUser().getId() != userId){
            throw new UserNotMatchedException();
        }

        cartItemRepository.delete(cartItemToDelete);

    }

    /**
     * This method deletes all the items from the given cart.
     * @param cart The cart that contains list of cart items.
     */
    public void clearCartFromAllItems(List<CartItem> cart) {
        if (!cart.isEmpty()){
            for (CartItem cartItem : cart) {
                cartItemRepository.delete(cartItem);
            }
        }
    }

    /**
     * Gets the cart item from the user's cart that has the same product id of the requested cart item to add.
     * @param cartItems The user's cart.
     * @param productIdOfCartItemToAdd The product id to search if exists
     *                                 in one of the cart item in the user's cart.
     * @return Returns the cart item from the user's cart if the cart item contains the product id
     *         from the requested cart item to add else returns null.
     */
    private CartItem getCartItemWithProductIdThatAlreadyExistsInUserCart(List<CartItem> cartItems, Long productIdOfCartItemToAdd){
        if(!cartItems.isEmpty()){
            for(CartItem cartItem : cartItems){
                if(cartItem.getProduct().getProductId() == productIdOfCartItemToAdd){
                    return cartItem;
                }
            }
        }
        return null;
    }

}
