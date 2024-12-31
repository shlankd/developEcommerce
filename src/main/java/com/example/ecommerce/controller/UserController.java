package com.example.ecommerce.controller;

//import com.example.ecommerce.api_model.DataOperation;
import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.exception.EmailFailureException;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * This class handles the user's information. (like update address, password etc...)
 * The access of the requests in the user controller is for authenticated users with role of USER.
 */
@RestController
public class UserController {

    /** Instance of AddressRepository */
    private AddressRepository addressRepository;

    /** Instance of SimpMessagingTemplate */
    //private SimpMessagingTemplate messagingTemplate;

    /** Instance of UserService */
    private UserService userService;

    /**
     * User Controller Constructor.
     * @param addressRepository The AddressRepository object.
     //* @param messagingTemplate The SimpMessagingTemplate object.
     * @param userService The UserService object.
     */
    public UserController(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        //this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    /**
     * Gets the list of addresses of the user by the given authenticated user entity and the user's id.
     * @param user The authenticated user entity.
     * @param userId The authenticated user's id.
     * @return The list of addresses that belong to the authenticated user.
     */
    @GetMapping("/user/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal EcommUser user, @PathVariable Long userId) {
        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressRepository.findByUser_Id(userId));
    }

    /**
     * Enables for the user to put a new address.
     * @param user The authenticated user.
     * @param userId User's id that request to put a new address.
     * @param address The new address to put.
     * @return Save of the new address.
     */
    @PutMapping("/user/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal EcommUser user,
                                              @PathVariable Long userId, @RequestBody Address address){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        address.setId(null);

        // Creates temporary user to set the user field of the address to associate the address to the user. (delete?)
        EcommUser tmpUser = new EcommUser();
        tmpUser.setId(userId);

        // In the Address entity json ignore the user field and only associate the address with the user id. (delete?)
        address.setUser(tmpUser);

        // Saves the address in address repository.
        Address storedAddress = addressRepository.save(address);

        // Sends the data object of address with type operation INSERT
        // and sends it to the destination "/topic/user/userId/address".
//        messagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
//                new DataOperation<>(address, DataOperation.DataOperatesType.INSERT));

        return ResponseEntity.ok(storedAddress);
    }

    /**
     * Enables for the user to update his exists address.
     * @param user The authenticated user.
     * @param userId The user's id that associated with the address.
     * @param address The updated address.
     * @param addressId The requested address id to update.
     * @return The save updated address with the addressId.
     */
    @PatchMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal EcommUser user, @PathVariable Long userId,
            @RequestBody Address address, @PathVariable Long addressId){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Checks if the id of the updated address is equal to the requested address id to update.
        if(address.getId() == addressId){

            Optional<Address> opOriginalAddress = addressRepository.findById(addressId);
            if(opOriginalAddress.isPresent()){

                // Checks if the address's user id is equal to the user's id.
                if(opOriginalAddress.get().getUser().getId() == userId){
                    address.setUser(opOriginalAddress.get().getUser());

                    // Saves the updated address in address repository.
                    Address storedAddress = addressRepository.save(address);

                    // Sends a message with the data object od address with type operation UPDATE
                    // and sends it to the destination "/topic/user/userId/address".
//                    messagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
//                            new DataOperation<>(address, DataOperation.DataOperatesType.UPDATE));

                    return ResponseEntity.ok(storedAddress);
                }
            }
        }
        // The id of the address is wrong.
        return ResponseEntity.badRequest().build();
    }

    /**
     * Enables for the user to delete address by address id.
     * @param user The user that request to delete his address.
     * @param userId The user's id.
     * @param addressId The address id that the user want to delete.
     * @return Returns response FORBIDDEN if the user entity does not match with the user's id,
     *         returns response bad request if the requested address id to delete doesn't exist,
     *         returns response ok if the requested address was successfully deleted by the user.
     */
    @DeleteMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal EcommUser user, @PathVariable Long userId,
            @PathVariable Long addressId){

        if(!userService.isAuthUserHasAMatchOfUserID(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Address> opAddress = addressRepository.findById(addressId);

        if(opAddress.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        addressRepository.delete(opAddress.get());

        return ResponseEntity.ok().build();
    }

}
