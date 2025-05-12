package vn.java.service;


import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j


public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String emailFrom;

    public String sendMail(String recipients, String subject, String content , MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        log.info("Sending .....");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom(emailFrom,"hieudv");

        if (recipients.contains(",")) {
            helper.setTo(InternetAddress.parse(recipients));
        }else {
            helper.setTo(recipients);
        }
        if(files !=null){
            for(MultipartFile file :files){
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()),file);
            }
        }
        helper.setSubject(subject);
        helper.setText(content,true);

        mailSender.send(message);
        log.info("Email has been send successfully, recipients= {}",recipients);
        return "sent";

    }
}
