package com.email.interfaces.services;

import com.email.interfaces.configuration.PubSubConfig;
import com.email.interfaces.dtos.Email;
import com.email.interfaces.dtos.EmailListResponse;
import com.email.interfaces.dtos.EmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    @Autowired
    PubSubConfig.NuevoEmailPubsubOutboundGateway nuevoEmailPubsubOutboundGateway;


    @Autowired
    PubSubConfig.consultaTodosLosMailsPubsubOutboundGateway consultaTodosLosMailsPubsubOutboundGateway;



    private EmailListResponse emailListResponse = null;

    public EmailListResponse getAllEmails(String email) throws JsonProcessingException, InterruptedException {

        EmailRequest emailRequest = new EmailRequest(email);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emailRequest);

        consultaTodosLosMailsPubsubOutboundGateway.sendToPubsub(json);
        Integer count = 0;

        while (emailListResponse == null && count != 2 ){
            TimeUnit.SECONDS.sleep(5);
            count++;
        }

        if (emailListResponse == null){
            System.out.println("Fallo la busqueda de emails");
            return EmailListResponse.builder().build();
        }else {

            return this.emailListResponse;
        }
    }

    @Bean
    @ServiceActivator(inputChannel = "respuestaTodosLosMailsChannel")
    public  synchronized MessageHandler recivirNuevosEmail() {
        return message -> {

            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();

            String messagePayload = new String((byte[]) message.getPayload());
            Gson g = new Gson();
            EmailListResponse emailListResponse = g.fromJson(messagePayload,EmailListResponse.class);

            System.out.println("Se consultaron los emails" + emailListResponse.toString());

            this.emailListResponse = emailListResponse;
        };
    }


    public String saveNewEmail(Email email, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(email);

        nuevoEmailPubsubOutboundGateway.sendToPubsub(json);

        TimeUnit.SECONDS.sleep(5);

        EmailListResponse emailListResponse = getAllEmails(email.getSender());

        model.addAttribute("emails",emailListResponse);

        return "home";
    }
}
