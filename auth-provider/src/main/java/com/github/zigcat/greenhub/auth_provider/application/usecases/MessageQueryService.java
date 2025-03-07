package com.github.zigcat.greenhub.auth_provider.application.usecases;

import com.github.zigcat.greenhub.auth_provider.domain.interfaces.MessageQueryAdapter;
import org.springframework.stereotype.Service;

@Service
public class MessageQueryService {
    private final MessageQueryAdapter adapter;

    public MessageQueryService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public void startProcessing(){
        adapter.processMessage();
    }
}
