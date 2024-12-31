//package com.example.ecommerce.security;
//
//import com.example.ecommerce.entity.EcommUser;
//import com.example.ecommerce.service.UserService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.SimpMessageType;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.security.authorization.AuthorizationEventPublisher;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
//import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//import java.util.Map;
//
///**
// * Configuration class of spring websocket with STOMP (Simple Text Oriented Messaging Protocol).
// */
//@Configuration
//@EnableWebSocket
//@EnableWebSocketMessageBroker
//public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
//
//    /** Instance of ApplicationContext. */
//    private ApplicationContext context;
//
//    /** Instance of JWTRequestFilter. */
//    private JWTRequestFilter jwtRequestFilter;
//
//    /** Instance of UserService. */
//    private UserService userService;
//
//    /** Instance of AntPathMatcher. */
//    private static final AntPathMatcher MATCHER = new AntPathMatcher();
//
//    /**
//     * WebSocketConfiguration Constructor.
//     * @param context Object of ApplicationContext.
//     * @param jwtRequestFilter Object of JWTRequestFilter.
//     * @param userService Object of UserService.
//     */
//    public WebSocketConfiguration(ApplicationContext context, JWTRequestFilter jwtRequestFilter,
//                                  UserService userService) {
//        this.context = context;
//        this.jwtRequestFilter = jwtRequestFilter;
//        this.userService = userService;
//    }
//
//    /**
//     * Inherited method that allows to register STOMP (Simple Text Oriented Messaging Protocol)
//     * endpoints mapping each to a specific URL and (optionally)
//     * enabling and configuring SockJS fallback options.
//     * @param registry Object of StompEndpointRegistry.
//     */
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Assumptions: front-end uses sockJS.
//        // Adds a STOMP endpoint of path to connect websocket.
//        registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();
//    }
//
//    /**
//     * Inherited method of configure message broker options.
//     * @param registry Object of StompEndpointRegistry.
//     */
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//
//        // Enable a simple message broker and configure one or more prefixes
//        // to filter destinations targeting the broker.
//        registry.enableSimpleBroker("/topic");
//
//        // Configure one or more prefixes to filter destinations targeting application annotated methods.
//        // When messages are processed, the matching prefix is removed
//        // from the destination in order to form the lookup path.
//        // This means annotations should not contain the destination prefix.
//        registry.setApplicationDestinationPrefixes("/app");
//    }
//
//    /**
//     * Configuration of managing authorization for clients inbound chanel.
//     * @param registration The ChannelRegistration object.
//     */
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//
//        // Generates authorization manager messages.
//        AuthorizationManager<Message<?>> authorizationManager = makeMessageAuthorizationManager();
//
//        // An interceptor of clients calls (like a filter between clients calls).
//        AuthorizationChannelInterceptor authInterceptor =
//                new AuthorizationChannelInterceptor(authorizationManager);
//
//        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
//
//        authInterceptor.setAuthorizationEventPublisher(publisher);
//
//        registration.interceptors(jwtRequestFilter, authInterceptor,
//               new DeclineClientMessagesOnChannelsChannelInterceptor());
//
//    }
//
//    /**
//     * Provides an authorization manager to modify the access of a certain object from the authenticated request.
//     * @return Returns object of AuthorizationManager type of MessageMatcherDelegatingAuthorizationManager.
//     */
//    private AuthorizationManager<Message<?>> makeMessageAuthorizationManager(){
//        MessageMatcherDelegatingAuthorizationManager.Builder messages =
//                new MessageMatcherDelegatingAuthorizationManager.Builder();
//
//        // Permits any clients call destination except the path "/topic/user/**" and "/topic/admin/**".
//        // that need to be from an authenticated client.
//        messages.simpDestMatchers("/topic/user/**").authenticated().anyMessage().permitAll();
//
//        return messages.build();
//    }
//
//    /**
//     *  Inner class that implements ChannelInterceptor that allows to view or modify
//     *  Messages that sent or received from a MessageChannel.
//     *  The purpose of this inner class is for decline clients to send messages on specifies paths destinations.
//     */
//    private class DeclineClientMessagesOnChannelsChannelInterceptor implements ChannelInterceptor {
//
//        /** Exclusive paths that declines clients messages. */
//        private String[] paths = new String[]{"topic/user/*/address"};
//
//        /**
//         * This method invoke before sending message and
//         * checks the message destination is allowed for clients messages.
//         * @param message The Message object.
//         * @param channel The MessageChannel object.
//         * @return The message that request to send.
//         */
//        @Override
//        public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//            // Condition for only messages of type MESSAGE.
//            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)){
//
//                // Gets the destination of the given message.
//                String destination = (String) message.getHeaders().get("simpDestination");
//
//                // For every path from the exclusive paths that don't allow clients messages.
//                for(String path: paths){
//
//                    // If the given message destination is equal to one of the exclusive paths.
//                    if(MATCHER.match(path, destination)){
//
//                        //throw new AccessDeniedException();
//                        // Throw exception in Websocket it can cause drop connection to the entire Websocket.
//
//                        // Sets the given message to null, because it is not allowed for clients messages.
//                        message = null;
//                    }
//                }
//            }
//            return message;
//        }
//    }
//
//    /**
//     *  Inner class that implements ChannelInterceptor that allows to view or modify
//     *  Messages that sent or received from a MessageChannel.
//     *  The purpose of this inner class is for configure the authorization level of the message destination
//     *  and prevent the client message if the client doesn't have an authorization of his message destination.
//     */
//    private class DestinationAuthorizationLevelChannelInterceptor implements ChannelInterceptor{
//
//        /**
//         * This method invoke before sending message and
//         * checks the message has a valid destination with authenticated user with match user's id.
//         * @param message The Message object.
//         * @param channel The MessageChannel object.
//         * @return The message that request to send.
//         */
//        @Override
//        public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//            // Condition for only messages of type SUBSCRIBE.
//            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)){
//
//                // Gets the destination of the given message.
//                String destination = (String) message.getHeaders().get("simpDestination");
//
//                // Checks if the destination match to the "/topic/user/{userId}/**".
//                if(MATCHER.match("/topic/user/{userId}/**", destination)) {
//
//                    // Extracts the userId from the destination.
//                    Map<String, String> params = MATCHER.extractUriTemplateVariables(
//                            "/topic/user/{userId}/**", destination);
//                    try {
//                        // Gets the userId from the destination of the given message.
//                        Long userId = Long.valueOf(params.get("userId"));
//
//                        // Gets the authentication from the Security Context.
//                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//                        // If the authentication exists.
//                        if (authentication != null) {
//                            // Gets the user entity.
//                            EcommUser user = (EcommUser) authentication.getPrincipal();
//
//                            // If the authenticated user and the user's id doesn't match.
//                            if (!userService.isAuthUserHasAMatchOfUserID(user, userId)) {
//                                // Then sets the message to null to not allow a SUBSCRIBE message to
//                                // authenticated user with different user id.
//                                message = null;
//                            }
//                        } else { // When the authentication is null.
//                            // Sets message to null to not allow a SUBSCRIBE message to a user that not authenticated.
//                            message = null;
//                        }
//                    } catch (NumberFormatException e) {
//                        // Sets the message to null if there is NumberFormatException.
//                        message = null;
//                    }
//                }
//            }
//            return message;
//        }
//    }
//}
