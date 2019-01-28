package com.example.test.spring;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Route
@Push
public class MainView extends VerticalLayout {
    private final UnicastProcessor<ChatMessage> publisher;
    private final Flux<ChatMessage> messages;
    private String username;

    public MainView(UnicastProcessor<ChatMessage> publisher,
                    Flux<ChatMessage> messages) {
        this.publisher = publisher;
        this.messages = messages;
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addClassName("main-view");

        H1 header = new H1("Ghost-Chat-Mode");
        header.getElement().getThemeList().add("dark");

        add(header);

        askUsername();

    }



    private void askUsername() {
        HorizontalLayout layout = new HorizontalLayout();
        TextField usernameField = new TextField();
        Button startButton = new Button("Your username");
        layout.add(usernameField, startButton);
        add(layout);
        startButton.addClickListener(click -> {
            username = usernameField.getValue();
            remove(layout);
            showChat();
        });
    }

    private void showChat() {
        MessageList messageList = new MessageList();
        add(messageList, createInputLayout());
        messages.subscribe(message -> {
            getUI().ifPresent(ui ->
                    ui.access(() ->
                            messageList.add(
                                    new Paragraph(message.getFrom() + ": " +
                                            message.getMessage())
                            )
                    ));
        });
    }

    private Component createInputLayout() {
        HorizontalLayout layout = new HorizontalLayout();

        TextField messageField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(messageField, sendButton);
        layout.setWidth("100%");
        layout.expand(messageField);
        sendButton.addClickListener(click -> {
            publisher.onNext(new ChatMessage(username, messageField.getValue()));
            messageField.clear();
            messageField.focus();
        });
        messageField.focus();
        return layout;


    }

}
