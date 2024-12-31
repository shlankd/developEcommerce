//package com.example.ecommerce.service;
//
//import com.example.ecommerce.entity.EcommUser;
//import com.example.ecommerce.entity.EcommRole;
//import com.example.ecommerce.repository.EcommUserRepository;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class UserDetailServiceConfig implements UserDetailsService {
//
//    private EcommUserRepository ecommUserRepository;
//
//    public UserDetailServiceConfig(EcommUserRepository ecommUserRepository){
//        this.ecommUserRepository = ecommUserRepository;
//    }
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<EcommUser> opUser = ecommUserRepository.findByUsernameIgnoreCase(username);
//        if(opUser.isPresent()) {
//            EcommUser ecommUser = opUser.get();
//            return new User(ecommUser.getUsername(), ecommUser.getPassword(), ecommUser.getAuthorities());
//        }
//        else{
//            throw new UsernameNotFoundException("The user with the username "+username+" has not been found.");
//        }
//    }
//
//
//    /**
//     * Converts the roles into mapped granted authorities.
//     * @param roles The roles list to convert into authorities.
//     * @return Returns a list of mapped authorities.
//     */
//
//    /*public Collection<? extends GrantedAuthority> getRolesToAuthorities(Set<EcommRole> roles) {
//        // For each role converts into authorities and insert into the authorities list and return.
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//    }*/
//
//    /*
//    @Override
//    public String getPassword() {
//        return "";
//    }
//
//    @Override
//    public String getUsername() {
//        return "";
//    }
//
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
//     */
//}