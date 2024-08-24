package com.phcworld.userservice.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("oauth service");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registration id : {}", registrationId);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oauth user : {}", oAuth2User);

        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        log.info("oauth user attributes : {}", oAuth2UserAttributes);
        String email = (String) oAuth2UserAttributes.get("email");
        // 회원 정보 저장
        return null;
    }
}
