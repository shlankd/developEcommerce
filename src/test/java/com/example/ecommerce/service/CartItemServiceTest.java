package com.example.ecommerce.service;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class tests methods from the CartItemService class.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class CartItemServiceTest {

    /** The CartItem Repository. */
    @Autowired
    private CartItemService cartItemService;

    /** The Product Repository. */
    @Autowired
    private EcommUserRepository ecommUserRepository;

    private CartItem testCartItem;
    @Autowired
    private ProductRepository productRepository;

    private final Long SIZE_OF_CART_ITEMS_IN_DATA_SQL = 5L;

    @BeforeEach
    void setUp() {
        testCartItem = new CartItem();
        // Sets the cart item for tests.
        testCartItem.setCartItemQuantity(5);
    }

    @Test
    @Transactional
    public void testAddCartItemWithNotExistsProductId(){

        Optional<Product> opProduct5 = productRepository.findProductByName("Product Test #5");
        Assertions.assertTrue(opProduct5.isPresent(), "The Product Test #5 should be present.");
        Long productIdNotExists = opProduct5.get().getProductId()+1;

        // Gets the user id for test.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        Long testUser1Id = opTestUser1.get().getId();

        // Test the cartItem add function throws exception ProductIdNotExistsException
        // with not exists product id of the selected cartItem.
        Assertions.assertThrows(ProductIdNotExistsException.class,
                () -> cartItemService.addItemToCart(testCartItem, productIdNotExists, testUser1Id),
                "The product of the selected cartItem should not exist.");
    }

    @Test
    @Transactional
    public void testAddCartItemWithProductThatOutOfStock(){

        // From data.sql the product with the product name of Product Test #4 is with 0 quantity.
        Optional<Product> opProduct4 = productRepository.findProductByName("Product Test #4");
        Assertions.assertTrue(opProduct4.isPresent(), "The Product Test #4 should be present.");
        Long productIdThatOutOfStock = opProduct4.get().getProductId();

        // Gets the user id for test.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        Long testUser1Id = opTestUser1.get().getId();

        // Test the cartItem add function throws exception ProductOutOfStockException
        // with product that has 0 quantity in the inventory.
        Assertions.assertThrows(ProductOutOfStockException.class,
                () -> cartItemService.addItemToCart(testCartItem, productIdThatOutOfStock, testUser1Id),
                "The product of the selected cartItem should be out of stock.");
    }

    @Test
    @Transactional
    public void testAddCartItemWithNotAvailableQuantity(){

        // From data.sql the product with the product name of Product Test #1 is with 20 quantity.
        Optional<Product> opProduct1 = productRepository.findProductByName("Product Test #1");
        Assertions.assertTrue(opProduct1.isPresent(), "The Product Test #1 should be present.");
        Long productIdWith20InStock = opProduct1.get().getProductId();

        // Gets the user id for test.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        Long testUser1Id = opTestUser1.get().getId();

        // Sets CartItem quantity with 21.
        testCartItem.setCartItemQuantity(21);

        // Test the cartItem add function throws exception QuantityOfSelectedItemNotAvailableException
        // with cartItem quantity of 21 and product quantity of 20.
        Assertions.assertThrows(QuantityOfSelectedItemNotAvailableException.class,
                () -> cartItemService.addItemToCart(testCartItem, productIdWith20InStock, testUser1Id),
                "The product of the selected cartItem should not exist.");

        // From data.sql the product with the product name of Product Test #2 is with 30 quantity.
        Optional<Product> opProduct2 = productRepository.findProductByName("Product Test #2");
        Assertions.assertTrue(opProduct2.isPresent(), "The Product Test #2 should be present.");
        Long productIdWith30InStock = opProduct2.get().getProductId();

        // From data.sql the product with id=2L has product quantity of 30.
        //Long productIdWith30InStock = 2L;

        // Sets CartItem quantity with 26.
        testCartItem.setCartItemQuantity(26);

        // Test the cartItem add function throws exception QuantityOfSelectedItemNotAvailableException
        // with add cartItem quantity of 26 with product that exists in the cart of testUser1 so the total
        // quantity in the cart is 31 and the stock of the product is 30.
        Assertions.assertThrows(QuantityOfSelectedItemNotAvailableException.class,
                () -> cartItemService.addItemToCart(testCartItem, productIdWith30InStock, testUser1Id),
                "The product of the selected cartItem should not exist.");
    }

    @Test
    @Transactional
    public void testAddCartItemWithProductIdThatAlreadyExistsInTheCart(){

        // Sets the testCartItem with quantity with 5.
        Integer testCartItemQuantity = 5;
        testCartItem.setCartItemQuantity(testCartItemQuantity);

        EcommUser testUser1 = null;

        // Gets The entity of testUser1 from data.sql file.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        testUser1 = opTestUser1.get();

        // Gets the user id of testUser1 for the test.
        Long testUser1Id = testUser1.getId();

        // Gets the cart of testUser1
        List<CartItem> testUser1Cart = testUser1.getCartItems();

        // Checks if the cart of testUser1 is not empty.
        Assertions.assertFalse(testUser1Cart.isEmpty(), "The cart of testUser1 should not be empty.");

        // Gets a cart item from the cart of testUser1.
        CartItem itemFromCart = testUser1Cart.get(0);

        // Gets the product from the testUser1 cart.
        Product productExistsInCart = itemFromCart.getProduct();

        // Gets the size cart, cart item quantity before the addition item to the cart.
        int sizeCartBefore = testUser1Cart.size();
        Integer cartItemQuantityBefore = itemFromCart.getCartItemQuantity();
        Long itemFromCartId = itemFromCart.getCartItemId();

        // Add the test cart item to the cart of testUser1.
        Assertions.assertDoesNotThrow(
                () -> cartItemService.addItemToCart(testCartItem, productExistsInCart.getProductId(),
                        testUser1Id),
                "The added cart item should not Throw an error.");

        // Gets the cart of testUser1 after the addition item.
        testUser1Cart = testUser1.getCartItems();

        // Gets a cart items from the cart of testUser1 with the product id.
        List<CartItem> itemsFromCartWithSameProduct = getCartWithSameProduct(testUser1Cart, productExistsInCart.getProductId());

        // Checks if the cart items of testUser1 with the same product is not empty.
        Assertions.assertFalse(itemsFromCartWithSameProduct.isEmpty(),
                "The cart items of testUser1 with the same product should not be empty.");

        // Checks if the amount of cart items with the same product is only one.
        Assertions.assertTrue(itemsFromCartWithSameProduct.size() == 1,
                "The amount of cart items with the same product should be 1.");

        // Gets the cart item that has found.
        CartItem itemFromCartAfter = itemsFromCartWithSameProduct.get(0);
        boolean isMatchCartId = itemFromCart.getCartItemId() == itemFromCartAfter.getCartItemId();
        Assertions.assertTrue(isMatchCartId, "The cart item id should be match.");

        // Checks if the cart size is the same after the addition cart item.
        int sizeCartAfter = testUser1Cart.size();
        boolean isCorrectSize = sizeCartAfter == sizeCartBefore;
        Assertions.assertTrue(isCorrectSize, "The cart size should be the same.");

        // Checks if the quantity of the cart item is not the same the addition cart item.
        Integer cartItemQuantityAfter = itemFromCart.getCartItemQuantity();
        boolean isQuantitySame = cartItemQuantityAfter == cartItemQuantityBefore;
        Assertions.assertFalse(isQuantitySame, "The cart item quantity should not be the same " +
                "after the addition of cart item.");

        // Checks if the quantity of the cart item is updated correctly after the addition cart item.
        boolean isCorrectQuantity = cartItemQuantityAfter == cartItemQuantityBefore + testCartItemQuantity;
        Assertions.assertTrue(isCorrectQuantity, "The cart item quantity should be correct.");

    }

    @Test
    @Transactional
    public void testEditCartItemQuantityWithUnMatchedCartItemId(){

        // Gets the testUser1 to get his cart.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        EcommUser testUser1 = opTestUser1.get();
        Long testUser1Id = opTestUser1.get().getId();

        // Gets CartItem to set wrongCartItemId from the cart of testUser1.
        List<CartItem> testUser1Cart = testUser1.getCartItems();
        Assertions.assertTrue(testUser1Cart.size() >= 2,
                "The size of the cart of testUser1 should be at least with 2 cart items.");
        // Sets the cartItem for edit with id that does not match with the requested cartItemId.
        Long wrongCartItemId = testUser1Cart.get(0).getCartItemId();
        testCartItem.setCartItemId(wrongCartItemId+1);
        // Sets cartItem with different quantity.
        testCartItem.setCartItemQuantity(6);

        // Test the cartItem edit function throws exception SelectedCartItemNotMatchWithCartItemException
        // with cartItem id that unmatched with the requested cartItem for edit.
        Assertions.assertThrows(SelectedCartItemNotMatchWithCartItemIdException.class,
                () -> cartItemService.editCartItemQuantity(testUser1Id, testCartItem, wrongCartItemId),
                "The cartItem id should not match with the requested cartItem for edit.");
    }

    @Test
    @Transactional
    public void testEditCartItemQuantityWithCartItemIdThatNotExists(){

        // Sets the cartItem for edit with id that not exists with the same id of the requested cartItemId.
        Long cartItemIdNotExists = SIZE_OF_CART_ITEMS_IN_DATA_SQL+1L;
        testCartItem.setCartItemId(cartItemIdNotExists);
        // Sets cartItem with different quantity
        testCartItem.setCartItemQuantity(6);

        // Gets the user id that the cartItem is belonged to.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        Long testUser1Id = opTestUser1.get().getId();

        // Test the cartItem edit function throws exception CartItemIdNotExistsException
        // with cartItem id for edit that not exists.
        Assertions.assertThrows(CartItemIdNotExistsException.class,
                () -> cartItemService.editCartItemQuantity(testUser1Id, testCartItem, cartItemIdNotExists),
                "The cartItem id for edit should not exists.");
    }

    @Test
    @Transactional
    public void testEditCartItemQuantityWithUserIdThatNotMatchWithTheUserThatTheCartItemIsBelong(){

        // Gets the user testUser1.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        EcommUser testUser1 = opTestUser1.get();

        // Gets the cart of testUser1.
        List<CartItem> testUser1Cart = testUser1.getCartItems();
        Assertions.assertFalse(testUser1Cart.isEmpty(),
                "The cart of testUser1 should not be empty.");

        // Sets the cart item to edit with id of cart item that belong to testUser1.
        Long cartItemIdToEdit = testUser1Cart.get(0).getCartItemId();
        testCartItem.setCartItemId(cartItemIdToEdit);
        // Sets cartItem with different quantity
        testCartItem.setCartItemQuantity(6);

        // Gets the user id of testUser3 instead the id of the testUser1 that the cartItem is belonged to.
        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");
        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present.");
        Long testUser3Id = opTestUser3.get().getId();

        // Test the cartItem edit function throws exception UserNotMachedException
        // with cartItem that belongs to other user that his id doesn't match
        // with the user id that requested to edit the cartItem.
        Assertions.assertThrows(UserNotMatchedException.class,
                () -> cartItemService.editCartItemQuantity(testUser3Id, testCartItem, cartItemIdToEdit),
                "The user id that requested to edit the cartItem should not match " +
                        "with the user that the cartItem is belong to.");
    }

    @Test
    @Transactional
    public void testEditCartItemQuantityWithNotAvailableQuantity(){

        // Gets the user testUser1.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        EcommUser testUser1 = opTestUser1.get();

        // Gets a cart of testUser1.
        List<CartItem> testUser1Cart = testUser1.getCartItems();
        Assertions.assertTrue(testUser1Cart.size() >= 2,
                "The cart of testUser1 should be at least with 2 items.");

        // Sets the cartItem id from the cart of testUser1 for edit.
        Long cartItemIdToEdit = testUser1Cart.get(1).getCartItemId();
        testCartItem.setCartItemId(cartItemIdToEdit);

        // Sets the id of testUser1.
        Long testUser1Id = testUser1.getId();

        // Sets cartItem with quantity that not available (the product quantity is 40).
        testCartItem.setCartItemQuantity(50);

        // Test the cartItem edit function throws exception QuantityOfSelectedItemNotAvailableException
        // with cartItem quantity for edit that not available due to lack of product quantity.
        Assertions.assertThrows(QuantityOfSelectedItemNotAvailableException.class,
                () -> cartItemService.editCartItemQuantity(testUser1Id, testCartItem, cartItemIdToEdit),
                "The cartItem quantity for edit should not be available.");
    }

    @Test
    @Transactional
    public void testEditItemQuantitySuccessfully(){

        // Gets the user testUser1.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        EcommUser testUser1 = opTestUser1.get();

        //Sets the id of testUser1.
        Long testUser1Id = opTestUser1.get().getId();

        // Gets testUser1 cart.
        List<CartItem> testUser1Cart = testUser1.getCartItems();
        Assertions.assertTrue(testUser1Cart.size() >= 2,
                "The cart of testUser1 should be at least with 2 items.");

        // Sets the cartItem id for edit with id of the same id of the requested cartItemId(=cartItemIdToEdit).
        Long cartItemIdToEdit = testUser1Cart.get(1).getCartItemId();
        testCartItem.setCartItemId(cartItemIdToEdit);

        // Sets cartItem with update quantity cartItem.
        testCartItem.setCartItemQuantity(6);

        // Test the cartItem edit function with no exception throws.
        Assertions.assertDoesNotThrow(
                () -> cartItemService.editCartItemQuantity(testUser1Id, testCartItem, cartItemIdToEdit),
                "The cartItem edit quantity should be done successfully.");
    }

    @Test
    @Transactional
    public void testDeleteCartItemWithNotExistsCartItemId(){

        // Sets cartItem id that not exists.
        Long cartItemIdNotExists = SIZE_OF_CART_ITEMS_IN_DATA_SQL + 1L;

        // Gets a user id.
        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");
        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present.");
        Long testUser3Id = opTestUser3.get().getId();

        // Test the cartItem delete function throws exception CartItemIdNotExistsException
        // with cartItem id that not exists.
        Assertions.assertThrows(CartItemIdNotExistsException.class,
                () -> cartItemService.deleteCartItem(testUser3Id, cartItemIdNotExists),
                "The cartItem id for delete should not exists.");
    }

    @Test
    @Transactional
    public void testDeleteCartItemWithUserIdThatUnMatchedWithTheUserThatTheCartItemIsBelong(){

        // Gets the testUser3.
        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");
        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present.");
        EcommUser testUser3 = opTestUser3.get();

        List<CartItem> testUser3Cart = testUser3.getCartItems();
        Assertions.assertFalse(testUser3Cart.isEmpty(), "The cart of testUser3 should not be empty.");

        // Sets the cartItem id for delete with the cart item that belongs to testUser3.
        Long cartItemIdToDelete = testUser3Cart.get(0).getCartItemId();

        // Gets the user id of testUser1 instead the id of the testUser3 that the cartItem is belonged to.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");
        Assertions.assertTrue(opTestUser1.isPresent(), "The testUser1 should be present.");
        Long testUser1Id = opTestUser1.get().getId();

        // Test the cartItem delete function throws exception UserNotMatchedException
        // with cartItem id that not belongs to the user that requested to delete this cartItem.
        Assertions.assertThrows(UserNotMatchedException.class,
                () -> cartItemService.deleteCartItem(testUser1Id, cartItemIdToDelete),
                "The cartItem to delete should not belong to the user that requested to delete it.");
    }

    @Test
    @Transactional
    public void testDeleteCartItemSuccessfully(){

        // Gets the user id of testUser3 that the cartItem is belonged to.
        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");
        Assertions.assertTrue(opTestUser3.isPresent(), "The testUser3 should be present.");
        EcommUser testUser3 = opTestUser3.get();

        // Gets the cart of testUser3.
        List<CartItem> testUser3Cart = testUser3.getCartItems();
        Assertions.assertFalse(testUser3Cart.isEmpty(), "The cart of testUser3 should not be empty.");

        // Sets cartItem id to delete from the cart of testUser3.
        Long cartItemIdToDelete = testUser3Cart.get(0).getCartItemId();

        // Sets the id of testUser3.
        Long testUser3Id = testUser3.getId();

        // Test the cartItem delete function with no exception throws.
        Assertions.assertDoesNotThrow(
                () -> cartItemService.deleteCartItem(testUser3Id, cartItemIdToDelete),
                "The cartItem should be deleted successfully.");
    }

    /**
     * This private method returns list of cart items from the given cart that includes one product.
     * @param cart The cart that contains cart items.
     * @param productId The product id to search from the given cart
     * @return Returns list of cart items from the given cart that includes
     *         only the product from the product id.
     */
    private List<CartItem> getCartWithSameProduct(List<CartItem> cart, Long productId){
        List<CartItem> cartWithSameProduct = new ArrayList<>();
        if(!cart.isEmpty()){
            for(CartItem cartItem : cart){
                if(cartItem.getProduct().getProductId() == productId){
                    cartWithSameProduct.add(cartItem);
                }
            }
        }
        return cartWithSameProduct;
    }
}
