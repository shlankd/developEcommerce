package com.example.ecommerce.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Filter for decoding a JWT in the Authorization header and loading the user
 * object into the authentication context. (change)
 */
@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

    /** Instance of JWTService. */
    private JWTService jwtService;

    /** Instance of EcommUserRepository. */
    private EcommUserRepository ecommUserRepository;

    /**
     * JWTRequestFilter Constructor.
     * @param jwtService The JWTService object.
     * @param ecommUserRepository The EcommUserRepository object.
     */
    public JWTRequestFilter(JWTService jwtService, EcommUserRepository ecommUserRepository) {
        this.jwtService = jwtService;
        this.ecommUserRepository = ecommUserRepository;
    }


    /**
     * Same contract as for doFilter, but guaranteed to be just invoked (change)
     * once per request within a single request thread. (change)
     * The doFilter implementation stores a request attribute for "already filtered", (change)
     * proceeding without filtering again if the attribute is already there. (change)
     *
     * @param request The servlet container creates an HttpServletRequest object and passes it
     *                as an argument to the servlet's service methods (doGet, doPost, etc).
     *
     * @param response Provide HTTP-specific functionality in sending a response.
     *                 For example, it has methods to access HTTP headers and cookies.
     *                 The servlet container creates an HttpServletResponse object and passes it
     *                 as an argument to the servlet's service methods (doGet, doPost, etc).
     *
     * @param filterChain A FilterChain is an object provided by the servlet container to the developer
     *                    giving a view into the invocation chain of a filtered request for a resource.
     *                    Filters use the FilterChain to invoke the next filter in the chain,
     *                    or if the calling filter is the last filter in the chain,
     *                    to invoke the resource at the end of the chain.
     *
     * @throws ServletException Defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /* For every request */

        // Gets the head of the filter chain with "Authorization" from the request.
        String tokenHeader = request.getHeader("Authorization");


        // Gets the authenticated token from processTokenAuthentication method.
        UsernamePasswordAuthenticationToken authenticatedToken = processTokenAuthentication(tokenHeader);

        if (authenticatedToken != null) {
            // Sets the authentication's details so the spring security and the spring MVC knows about it.
            authenticatedToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }

        // Calls the filter chain and do the next filter.
        filterChain.doFilter(request, response);
    }

//    /**
//     * Implements preSend method that allows to check the authentication of the source messages
//     * from the client to the server.
//     * @param message The websocket message.
//     * @param channel The message chanel.
//     * @return Returns
//     */
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        // Condition for only messages of type SUBSCRIBE or MESSAGE.
//        if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)
//            || message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
//
//            // Gets the native headers that contains which is a map of HTTP headers.
//            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
//
//            // If the map HTTP exists.
//            if (nativeHeaders != null) {
//
//                // Gets the list of authorization native header.
//                List authTokenList = (List) nativeHeaders.get("Authorization");
//
//                if (authTokenList != null) {
//
//                    // Gets the authorization token (the first element).
//                    String tokenHeader = (String) authTokenList.get(0);
//
//                    // Gets the tokenHeader through processTokenAuthentication method.
//                    processTokenAuthentication(tokenHeader);
//                }
//            }
//        }
//        return message;
//    }

    /**
     * Authentication process to the given token that returns authenticated token.
     * @param token The token to perform the authentication process.
     * @return Returns token with null authentication if the given token is invalid to authenticate,
     *                 otherwise returns authenticated token.
     */
    private UsernamePasswordAuthenticationToken processTokenAuthentication(String token){
        // Checks if the header of the request called "Authorization" start with "Bearer ".
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7); // take the sub token of the tokenHead after "Bearer ".

            // Tries to decode the token.
            try {
                // Process the given token.
                String username = jwtService.getUsernameClaim(token); // gets the username also throws exceptions.

                //UserDetails user = userDetailServiceConfig.loadUserByUsername(username); // find the user.

                Optional<EcommUser> opUser = ecommUserRepository.findByUsernameIgnoreCase(username); // find the user.

                // If the user isEnable() by verifiedEmail.
                //if(user!=null && user.isEnabled()){
                if(opUser.isPresent()){

                    EcommUser user = opUser.get();

                    if(user.isVerifiedEmail()) {
                        UsernamePasswordAuthenticationToken authenticationToken;

                        // Creates authentication token object.
                        authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        // Sets the authentication and stored it in spring security holder with the security context.
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        return authenticationToken;
                    }

                }

            } catch(JWTDecodeException e){}

        }
        // Sets null authentication and stored it in spring security holder with the security context.
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }
}
