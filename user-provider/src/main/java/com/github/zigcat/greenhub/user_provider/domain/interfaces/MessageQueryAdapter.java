package com.github.zigcat.greenhub.user_provider.domain.interfaces;

public interface MessageQueryAdapter {
    void processRegisterMessage();
    void processAuthorizeMessage();
    void processLoginMessage();
}
