package com.allthattour.web.user.service;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.allthattour.web.common.EmailUtil;
import com.allthattour.web.tool.Utils;
import com.allthattour.web.user.model.Cart;
import com.allthattour.web.user.model.Payment;
import com.allthattour.web.user.model.User;
import com.allthattour.web.user.repository.CartRepository;
import com.allthattour.web.user.repository.PaymentRepository;
import com.allthattour.web.user.repository.RoleRepository;
import com.allthattour.web.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;

    @Transactional
    public boolean createEntity(Long userPid, String paymentKey, Long amount, String orderId) {
        User user = userRepository.findById(userPid).orElseThrow();

        try {
            RestTemplate restTemplate = new RestTemplate();

            String uri = "https://api.tosspayments.com/v1/payments/confirm";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic dGVzdF9za19PNkJZcTdHV1BWdjJxMXdleE1tVk5FNXZibzFkOg==");
            headers.add("Content-Type", "application/json");

            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);
            map.put("orderId", orderId);
            map.put("paymentKey", paymentKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            String result = restTemplate.postForObject(uri, entity, String.class);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
            System.out.println("prepare save");
            Payment payment = new Payment((String) jsonObject.get("lastTransactionKey"),
                    (String) jsonObject.get("paymentKey"), (String) jsonObject.get("requestedAt"),
                    (String) jsonObject.get("approvedAt"), amount, (String) jsonObject.get("orderId"),
                    (String) jsonObject.get("orderName"), (String) ((JSONObject) jsonObject.get("receipt")).get("url"),
                    book(user),
                    user);
            paymentRepository.save(payment);
            System.out.println("save done");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Transactional
    public void cancelPurchase(Long userPid, Long paymentPid) {
        User user = userRepository.findById(userPid).orElseThrow();

        Payment payment = paymentRepository.findById(paymentPid).orElseThrow();
        String reason, reason2;
        RestTemplate restTemplate = new RestTemplate();

        String uri = "https://api.tosspayments.com/v1/payments/" +
                payment.getPaymentKey() + "/cancel ";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic ==");
        headers.add("Content-Type", "application/json");

        Map<String, Object> map = new HashMap<>();
        map.put("cancelReason", "");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        String ret = restTemplate.postForObject(uri, entity, String.class);
        System.out.println(ret);
    }

    @Transactional
    @Modifying
    private String book(User user) {
        List<String> books = new ArrayList<>();
        String url = "https://affiliateapisecure.agoda.com/api/v4/book";
        List<Cart> carts = cartRepository.findByUser(user);
        for (Cart cart : carts) {
            System.out.println(cart.getSearchId());
            try {
                RestTemplate restTemplate = new RestTemplate();

                Map<String, Object> map = new HashMap<>();
                map.put("waitTime", 120);
                Map<String, Object> bookingDetails = new HashMap<>();
                bookingDetails.put("searchId", Long.valueOf(cart.getSearchId()));
                bookingDetails.put("tag", "00000000-0000-0000-0000-000000000000");
                bookingDetails.put("allowDuplication", false);
                bookingDetails.put("checkIn", cart.getStart());
                bookingDetails.put("checkOut", cart.getEnd());
                bookingDetails.put("language", "ko-kr");
                Map<String, Object> property = new HashMap<>();
                property.put("propertyId", cart.getHotelId());
                Map<String, Object> rate = new HashMap<>();
                rate.put("inclusive", cart.getPrice());
                Map<String, Object> guestDetail = new HashMap<>();
                guestDetail.put("title", "Mr.");
                guestDetail.put("firstName", user.getFirstName());
                guestDetail.put("lastName", user.getLastName());
                guestDetail.put("countryOfResidence", "KR");
                guestDetail.put("gender", "Male");
                guestDetail.put("age", 30);
                guestDetail.put("primary", true);
                List<Object> guestDetails = new ArrayList<>();
                guestDetails.add(guestDetail);
                Map<String, Object> rooms = new HashMap<>();
                rooms.put("blockId", cart.getBlockId());
                rooms.put("count", 1);
                rooms.put("adults", cart.getAdult());
                rooms.put("children", cart.getChild());
                rooms.put("rate", rate);
                rooms.put("guestDetails", guestDetails);
                rooms.put("currency", "KRW");
                rooms.put("paymentModel", "Merchant");
                List<Object> roomList = new ArrayList<>();
                roomList.add(rooms);
                property.put("rooms", roomList);
                bookingDetails.put("property", property);
                map.put("bookingDetails", bookingDetails);

                Map<String, Object> phone = new HashMap<>();
                phone.put("countryCode", "82");
                phone.put("areaCode", "");
                phone.put("number", user.getPhone());
                Map<String, Object> customerDetail = new HashMap<>();
                customerDetail.put("language", "ko-kr");
                customerDetail.put("title", "Mr.");
                customerDetail.put("firstName", user.getFirstName());
                customerDetail.put("lastName", user.getLastName());
                customerDetail.put("email", user.getEmail());
                customerDetail.put("phone", phone);
                customerDetail.put("newsletter", false);
                map.put("customerDetail", customerDetail);
                Map<String, Object> creditCardInfo = new HashMap<>();
                Map<String, Object> paymentDetails = new HashMap<>();
                creditCardInfo.put("cardType", "MasterCard");
                creditCardInfo.put("number", "5532240015056623");
                creditCardInfo.put("expiryDate", "052028");
                creditCardInfo.put("cvc", "022");
                creditCardInfo.put("holderName", "ALL THAT MEDI CO.,LTD");
                creditCardInfo.put("countryOfIssue", "KR");
                creditCardInfo.put("issuingBank", "WOORI");
                paymentDetails.put("creditCardInfo", creditCardInfo);
                map.put("paymentDetails", paymentDetails);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.set("Authorization", "1913931:415DC3B2-3FD5-469D-9333-040D7C5C2603");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
                if (((Long) jsonObject.get("status")) == 200) {
                    Long id = (Long) ((JSONObject) ((JSONArray) jsonObject.get("bookingDetails")).get(0)).get("id");
                    books.add(String.valueOf(id));
                    cartRepository.deleteById(cart.getPid());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.join(",", books);
    }

    public void sendMail(String to, String title, String body) {
        final String fromEmail = "allthatmedi0214@gmail.com"; // requires valid gmail id
        final String password = "meayqaprgsqjouzn"; // correct password for gmail id

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
        props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); // SMTP Port

        // create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            // override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        EmailUtil.sendEmail(session, to, title, body);
    }
}
