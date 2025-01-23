package com.github.zigcat.greenhub.user_provider.adapters;

public interface MessageQueryAdapter {
    void processRegisterMessage();
    void processAuthorizeMessage();
    void processLoginMessage();
}
