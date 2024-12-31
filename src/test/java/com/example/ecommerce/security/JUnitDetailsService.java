package com.example.ecommerce.security;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class implements the interface class UserDetailsService
 * which is an interface that allows spring to loads user-specific data.
 */
@Service
@Primary
public class JUnitDetailsService implements UserDetailsService {

    /** A repository of users un ecommerce instance. */
    @Autowired
    private EcommUserRepository ecommUserRepository;

    /**
     * Inherited method from the interface class UserDetailsService
     * @param username The username of the user whose data is required.
     * @return Returns user type of EcommUser that implements UserDetails.
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<EcommUser> opUser = ecommUserRepository.findByUsernameIgnoreCase(username);

        if(opUser.isPresent()){
            return opUser.get();
        }
        return null;
    }
}
