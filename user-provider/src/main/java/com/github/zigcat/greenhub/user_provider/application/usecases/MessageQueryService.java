package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.domain.interfaces.MessageQueryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageQueryService {
    private final MessageQueryAdapter adapter;

    @Autowired
    public MessageQueryService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public void startProcessing(){
        adapter.processAuthorizeMessage();
        adapter.processLoginMessage();
        adapter.processRegisterMessage();
    }
}
