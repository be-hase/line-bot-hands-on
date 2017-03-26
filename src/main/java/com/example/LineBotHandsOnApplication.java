package com.example;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@RestController
public class LineBotHandsOnApplication {
    private final LineMessagingClient messagingClient;
    private final LineSignatureValidator signatureValidator;
    private final ObjectMapper objectMapper;

    public LineBotHandsOnApplication(
            final ObjectMapper objectMapper
    ) {
        final String channelAccessToken = "YOUR_channelAccessToken";
        final String channelSecret = "YOUR_channelSecret";

        messagingClient = new LineMessagingClientImpl(
                LineMessagingServiceBuilder.create(channelAccessToken).build());
        signatureValidator = new LineSignatureValidator(channelSecret.getBytes(StandardCharsets.UTF_8));
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestHeader("X-Line-Signature") final String headerSignature,
            @RequestBody final byte[] body
    ) throws Exception {
        if (!signatureValidator.validateSignature(body, headerSignature)) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        final CallbackRequest callbackRequest = objectMapper.readValue(body, CallbackRequest.class);
        for (final Event event : callbackRequest.getEvents()) {
            if (event instanceof MessageEvent) {
                final MessageEvent messageEvent = (MessageEvent) event;
                final MessageContent messageContent = messageEvent.getMessage();
                if (messageContent instanceof TextMessageContent) {
                    final TextMessageContent textMessageContent = (TextMessageContent) messageContent;
                    final String text = textMessageContent.getText();
                    messagingClient.replyMessage(
                            new ReplyMessage(messageEvent.getReplyToken(), new TextMessage(text)));
                }
            }
        }

        return ResponseEntity.ok("");
    }

    public static void main(String[] args) {
        SpringApplication.run(LineBotHandsOnApplication.class, args);
    }
}
