package com.udemy.app.ws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.udemy.app.ws.security.SecurityConstants;
import com.udemy.app.ws.shared.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class AmazonSES {

    // Emails will be send from this address
    // Get from application.properties
    static String FROM;

    final String SUBJECT = "One last step to complete your registration with PetREST ";
    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // Text body in HTML format
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='$origin/verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // Text body without HTML format
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " $origin/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";


    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='$origin/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + " $origin/verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";


    public void verifyEmail(UserDto userDto) {

        // Harcoded ID and Password from Amazon SES
        //System.setProperty("aws.accessKeyId", "XXXXXXXXXXX");
        //System.setProperty("aws.secretKey", "YYYYYYYYYYYYYY");

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1).build();

        String htmlBodyWithToken = HTMLBODY
                .replace("$tokenValue", userDto.getEmailVerificationToken())
                .replace("$origin", SecurityConstants.getOrigin());
        String textBodyWithToken = TEXTBODY
                .replace("$tokenValue", userDto.getEmailVerificationToken())
                .replace("$origin", SecurityConstants.getOrigin());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(SecurityConstants.getEmail());

        client.sendEmail(request);

        System.out.println("Email sent!");

    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token) {
        boolean returnValue = false;

        // Harcoded ID and Password from Amazon SES
        //System.setProperty("aws.accessKeyId", "XXXXXXXXXXX");
        //System.setProperty("aws.secretKey", "YYYYYYYYYYYYYY");

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY
                .replace("$tokenValue", token)
                .replace("$firstName", firstName)
                .replace("$origin", SecurityConstants.getOrigin());

        String textBodyWithToken = PASSWORD_RESET_TEXTBODY
                .replace("$tokenValue", token)
                .replace("$firstName", firstName)
                .replace("$origin", SecurityConstants.getOrigin());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(SecurityConstants.getEmail());

        SendEmailResult result = client.sendEmail(request);
        if (result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty())) {
            returnValue = true;
        }

        return returnValue;
    }

}
