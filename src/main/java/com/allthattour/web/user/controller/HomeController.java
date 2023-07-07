package com.allthattour.web.user.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.springframework.web.bind.annotation.RequestParam;

import com.allthattour.web.user.model.Agoda;
import com.allthattour.web.user.model.AgodaHotel;
import com.allthattour.web.user.model.AgodaHotelAddress;
import com.allthattour.web.user.model.AgodaHotelDescription;
import com.allthattour.web.user.model.AgodaPicture;
import com.allthattour.web.user.model.Cart;
import com.allthattour.web.user.model.ERole;
import com.allthattour.web.user.model.Hotel;
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.model.Room;
import com.allthattour.web.user.model.User;
import com.allthattour.web.user.repository.AgodaFacilityRepository;
import com.allthattour.web.user.repository.AgodaHotelAddressRepository;
import com.allthattour.web.user.repository.AgodaHotelDescriptionRepository;
import com.allthattour.web.user.repository.AgodaHotelRepository;
import com.allthattour.web.user.repository.AgodaPictureRepository;
import com.allthattour.web.user.repository.AgodaRepository;
import com.allthattour.web.user.repository.AgodaRoomTypeRepository;
import com.allthattour.web.user.repository.CartRepository;
import com.allthattour.web.user.repository.UserRepository;
import com.allthattour.web.user.service.PaymentService;
import com.google.gson.JsonObject;
import com.allthattour.web.tool.Utils;;

@Controller
public class HomeController {
    @Autowired
    AgodaRepository agodaRepository;
    @Autowired
    AgodaHotelRepository agodaHotelRepository;
    @Autowired
    AgodaFacilityRepository agodaFacilityRepository;
    @Autowired
    AgodaRoomTypeRepository agodaRoomTypeRepository;
    @Autowired
    AgodaHotelDescriptionRepository agodaHotelDescriptionRepository;
    @Autowired
    AgodaPictureRepository agodaPictureRepository;
    @Autowired
    AgodaHotelAddressRepository agodaHotelAddressRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentService paymentService;

    @GetMapping(value = "index")
    public String index(Model model) {
        System.out.println("index");
        ERole role = Utils.getRole();
        System.out.println(role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
                return "index";
            default:
                return "index";
        }
    }

    @GetMapping(value = "login")
    public String loginView() {
        System.out.println("login");
        ERole role = Utils.getRole();
        switch (role) {
            case ROLE_USER:
                return "redirect:/index";
            default:
                return "login";
        }
    }

    @GetMapping(value = "signup")
    public String register() {
        ERole role = Utils.getRole();
        switch (role) {
            case ROLE_USER:
                return "redirect:/index";
            default:
                return "signup";
        }
    }

    @GetMapping(value = "help")
    public String help(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "help";
        }
    }

    @GetMapping(value = "search")
    public String search(Model model, @RequestParam(value = "k", defaultValue = "") String place,
            @RequestParam(value = "s", defaultValue = "") String s,
            @RequestParam(value = "e", defaultValue = "") String e,
            @RequestParam(value = "r", defaultValue = "1") Integer room,
            @RequestParam(value = "a", defaultValue = "0") Integer adult,
            @RequestParam(value = "c", defaultValue = "0") Integer child,
            @RequestParam(value = "x", defaultValue = "") String rooms,
            @RequestParam(value = "y", defaultValue = "") String types,
            @RequestParam(value = "z", defaultValue = "") String rates,
            @RequestParam(value = "u", defaultValue = "") String minPrice,
            @RequestParam(value = "v", defaultValue = "") String maxPrice) // Feed를 기준으로 필터링 내용 다시 작성 필요 (수정 중)
            throws URISyntaxException, ParseException, URISyntaxException {
        ERole role = Utils.getRole();
        LocalDate startDate, endDate;
        String rates2 = rates;
        // String types2 = "";
        List<String> typeList = new ArrayList<>();
        List<String> roomList = new ArrayList<>();
        int min = minPrice.equals("") ? 10000 : Integer.valueOf(minPrice);
        int max = maxPrice.equals("") ? 2500000 : Integer.valueOf(maxPrice);
        try {
            startDate = LocalDate.parse(s);
        } catch (Exception err) {
            startDate = LocalDate.now();
        }
        if (e.equals("")) {
            try {
                endDate = LocalDate.parse(s);
                endDate = endDate.plusDays(1);
            } catch (Exception err) {
                endDate = LocalDate.now();
            }
        } else {
            try {
                endDate = LocalDate.parse(e);
            } catch (Exception err) {
                endDate = LocalDate.now();
            }
        }
        if (types.equals("")) {
            types = "1,2,3,4,5";
            typeList = Arrays.asList(
                    "모텔,호텔,서비스 아파트먼트,게스트하우스 / 비앤비,아파트먼트,리조트 빌라,호스텔,리조트,Hotel,Hostel,Motel,Serviced apartment,Guesthouse/bed and breakfast,Entire apartment"
                            .split(","));
        } else {
            String[] tmpStr = types.split(",");
            for (int i = 0; i < tmpStr.length; i++) {
                if (tmpStr[i].equals("1")) {
                    typeList.add("호텔");
                    typeList.add("Hotel");
                } else if (tmpStr[i].equals("2")) {
                    typeList.add("리조트 빌라");
                } else if (tmpStr[i].equals("3")) {
                    typeList.add("게스트하우스 / 비앤비");
                    typeList.add("호스텔");
                    typeList.add("Hostel");
                    typeList.add("Guesthouse/bed and breakfast");
                } else if (tmpStr[i].equals("4")) {
                    typeList.add("모텔");
                    typeList.add("Motel");
                } else if (tmpStr[i].equals("5")) {
                    typeList.add("아파트먼트");
                    typeList.add("서비스 아파트먼트");
                    typeList.add("Serviced apartment");
                    typeList.add("Entire apartment");
                }
            }
        }
        if (rates.equals("")) {
            rates = "0,0.5,1,1.5,2,2.5,3,3.5,4,4.5,5";
            rates2 = "1,2,3,4,5";
        } else {
            String[] tmpStr = rates.split(",");
            for (int i = 0; i < tmpStr.length; i++) {
                if (tmpStr[i].equals("1")) {
                    rates += ",0.5";
                } else if (tmpStr[i].equals("2")) {
                    rates += ",1.5";
                } else if (tmpStr[i].equals("3")) {
                    rates += ",2.5";
                } else if (tmpStr[i].equals("4")) {
                    rates += ",3.5";
                } else if (tmpStr[i].equals("5")) {
                    rates += ",4.5";
                }
            }
        }

        // if (rooms.equals("")) {
        // rooms = "1,2,3,4";
        // roomList = Arrays.asList(
        // "모텔,호텔,서비스 아파트먼트,게스트하우스 / 비앤비,아파트먼트,리조트
        // 빌라,호스텔,리조트,Hotel,Hostel,Motel,Serviced apartment,Guesthouse/bed and
        // breakfast,Entire apartment"
        // .split(","));
        // } else {
        // String[] tmpStr = rooms.split(",");
        // for (int i = 0; i < tmpStr.length; i++) {
        // if (tmpStr[i].equals("1")) {
        // typeList.add("호텔");
        // typeList.add("Hotel");
        // } else if (tmpStr[i].equals("2")) {
        // typeList.add("리조트 빌라");
        // } else if (tmpStr[i].equals("3")) {
        // typeList.add("게스트하우스 / 비앤비");
        // typeList.add("호스텔");
        // typeList.add("Hostel");
        // typeList.add("Guesthouse/bed and breakfast");
        // } else if (tmpStr[i].equals("4")) {
        // typeList.add("모텔");
        // typeList.add("Motel");
        // }
        // }
        // }
        double[] rateList = Arrays.asList(rates.split(",")).stream().mapToDouble(a -> Double.valueOf(a)).toArray();
        // System.out.println(rateList.length);
        // for (int i = 0; i < rateList.length; i++) {
        // System.out.println(rateList[i]);
        // }
        // System.out.println(place);
        List<Agoda> agodas = agodaRepository.findByConditions(place, rateList);
        // List<AgodaHotel> agodas = agodaHotelRepository.findByConditions(place,
        // rateList, typeList);
        System.out.println("agodas size: " + agodas.size());
        for (Agoda agoda : agodas) {
            System.out.print(agoda.getHotelId() + ", ");
        }
        // System.out.println();
        List<Hotel> hotels = new ArrayList<>();
        int page = 0;
        while (agodas.stream().skip(page * 100).limit(100).toList().size() > 0) {
            try {
                String url = agodas.size() <= 20 ? "https://affiliateapi5861.agoda.com/api/v4/property/availability"
                        : agodas.size() <= 100 ? "https://affiliateapi6792.agoda.com/api/v4/property/availability"
                                : "https://affiliateapi7643.agoda.com/api/v4/property/availability";

                RestTemplate restTemplate = new RestTemplate();

                Map<String, Object> map = new HashMap<>();
                map.put("waitTime", 60);
                Map<String, Object> criteria = new HashMap<>();

                List<Long> propertyIds = agodas.stream().skip(page * 100).limit(100)
                        .map(a -> Long.valueOf(a.getHotelId())).toList();
                map.put("waitTime", 60);
                criteria.put("propertyIds", propertyIds);
                criteria.put("checkIn", startDate.toString());
                criteria.put("checkOut", endDate.toString());
                criteria.put("rooms", room);
                criteria.put("adults", adult);
                criteria.put("children", child);
                criteria.put("language", "ko-kr");
                criteria.put("currency", "KRW");
                criteria.put("userCountry", "KR");
                map.put("criteria", criteria);
                Map<String, Object> features = new HashMap<>();
                // features.put("ratesPerProperty", 1);
                List<String> extra = new ArrayList<>();
                extra.add("content");
                extra.add("surchargeDetail");
                extra.add("CancellationDetail");
                extra.add("BenefitDetail");
                extra.add("dailyRate");
                extra.add("taxDetail");
                extra.add("rateDetail");
                extra.add("promotionDetail");
                features.put("extra", extra);
                map.put("features", features);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.set("Authorization", "1913931:415DC3B2-3FD5-469D-9333-040D7C5C2603");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
                try {
                    for (int i = 0; i < ((JSONArray) jsonObject.get("properties")).size(); i++) {
                        JSONObject property = (JSONObject) ((JSONArray) jsonObject.get("properties")).get(i);
                        long propertyId = (Long) property.get("propertyId");
                        String name = (String) property.get("translatedPropertyName");
                        int price = (int) ((Double) ((JSONObject) ((JSONObject) ((JSONArray) property.get("rooms"))
                                .get(0))
                                .get("totalPayment")).get("inclusive")).doubleValue();

                        if (price < min || price > max) {
                            continue;
                        }

                        int aminityCount = ((JSONArray) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                                .get("benefits"))
                                .size();
                        // if (!agodaHotelDescriptionRepository.findByHotelId(propertyId).isPresent()) {
                        // continue;
                        // }
                        // if (!agodaHotelAddressRepository
                        // .findByHotelIdAndAddressType(propertyId, "Local language").isPresent()) {
                        // continue;
                        // }
                        // if (agodaPictureRepository.findByHotelId(propertyId).isEmpty()) {
                        // continue;
                        // }
                        // AgodaHotelDescription description = agodaHotelDescriptionRepository
                        // .findByHotelId(propertyId).orElseThrow();
                        // AgodaHotelAddress address = agodaHotelAddressRepository
                        // .findByHotelIdAndAddressType(propertyId, "Local language").orElseThrow();
                        // if (address.getAddressLine1() == null) {
                        // address = agodaHotelAddressRepository
                        // .findByHotelIdAndAddressType(propertyId, "English address").orElseThrow();
                        // }
                        // List<AgodaPicture> pictures =
                        // agodaPictureRepository.findByHotelId(propertyId);
                        // hotels.add(new Hotel(name,
                        // (address.getAddressLine1() + " "
                        // + (address.getAddressLine2() == null ? "" :
                        // address.getAddressLine2())).trim(),
                        // description.getOverview(),
                        // pictures.get(0).getUrl().split("\\?")[0] + "?s=312x",
                        // "우수", "w" + NumberFormat.getNumberInstance().format(price), propertyId,
                        // price,
                        // (long)
                        // Math.ceil(agodas.get(propertyIds.indexOf(propertyId)).getStarRating()),
                        // aminityCount,
                        // agodas.get(propertyIds.indexOf(propertyId)).getNumberOfReviews(),
                        // agodas.get(propertyIds.indexOf(propertyId)).getRatingAverage()));
                        hotels.add(new Hotel(name,
                                (agodas.get(propertyIds.indexOf(propertyId)).getAddressLine1() + " "
                                        + (agodas.get(propertyIds.indexOf(propertyId)).getAddressLine2() == null ? ""
                                                : agodas.get(propertyIds.indexOf(propertyId)).getAddressLine2()))
                                        .trim(),
                                agodas.get(propertyIds.indexOf(propertyId)).getOverviews(),
                                agodas.get(propertyIds.indexOf(propertyId)).getPhoto1().split("\\?")[0] + "?s=312x",
                                "우수", "w" + NumberFormat.getNumberInstance().format(price), propertyId, price,
                                (long) Math.ceil(agodas.get(propertyIds.indexOf(propertyId)).getStarRating()),
                                aminityCount,
                                agodas.get(propertyIds.indexOf(propertyId)).getNumberOfReviews(),
                                agodas.get(propertyIds.indexOf(propertyId)).getRatingAverage()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
            page++;
        }
        System.out.println(hotels.size());
        model.addAttribute("hotels", hotels);
        model.addAttribute("place", place);
        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());
        model.addAttribute("room", room);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("rates", rates2);
        model.addAttribute("types", types);
        model.addAttribute("rooms", rooms);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        switch (role) {
            case ROLE_USER:
            default:
                return "search";
        }
    }

    @GetMapping(value = "/hotel-detail/{pid}")
    public String detail(Model model, @PathVariable("pid") Long pid,
            @RequestParam(value = "s", defaultValue = "") String s,
            @RequestParam(value = "e", defaultValue = "") String e,
            @RequestParam(value = "r", defaultValue = "1") Integer room,
            @RequestParam(value = "a", defaultValue = "0") Integer adult,
            @RequestParam(value = "c", defaultValue = "0") Integer child)
            throws URISyntaxException, ParseException, URISyntaxException {
        ERole role = Utils.getRole();
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(s);
        } catch (Exception err) {
            startDate = LocalDate.now();
        }

        if (e.equals("")) {
            try {
                endDate = LocalDate.parse(s);
                endDate = endDate.plusDays(1);
            } catch (Exception err) {
                endDate = LocalDate.now();
            }
        } else {
            try {
                endDate = LocalDate.parse(e);
            } catch (Exception err) {
                endDate = LocalDate.now();
            }
        }

        String url = "https://affiliateapi6792.agoda.com/api/v4/property/availability";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> map = new HashMap<>();
        map.put("waitTime", 60);
        Map<String, Object> criteria = new HashMap<>();

        List<Long> propertyIds = new ArrayList<>();
        propertyIds.add(pid);
        map.put("waitTime", 60);
        criteria.put("propertyIds", propertyIds);
        criteria.put("checkIn", startDate.toString());
        criteria.put("checkOut", endDate.toString());
        criteria.put("rooms", room);
        criteria.put("adults", adult);
        criteria.put("children", child);
        criteria.put("language", "ko-kr");
        criteria.put("currency", "KRW");
        criteria.put("userCountry", "KR");
        map.put("criteria", criteria);
        Map<String, Object> features = new HashMap<>();
        features.put("ratesPerProperty", 1);
        List<String> extra = new ArrayList<>();
        extra.add("content");
        extra.add("surchargeDetail");
        extra.add("CancellationDetail");
        extra.add("BenefitDetail");
        extra.add("dailyRate");
        extra.add("taxDetail");
        extra.add("rateDetail");
        extra.add("promotionDetail");
        features.put("extra", extra);
        map.put("features", features);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "1913931:415DC3B2-3FD5-469D-9333-040D7C5C2603");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        List<Room> rooms = new ArrayList<>();
        int aminityCount = 0;
        long price = 0, rate = 0;
        String surcharges = "";
        String cancellationPolicy = "";
        String searchId = "";
        try {
            searchId = String.valueOf((Long) jsonObject.get("searchId"));
            List<String> benefits = new ArrayList<>();
            JSONObject property = (JSONObject) ((JSONArray) jsonObject.get("properties")).get(0);
            for (int i = 0; i < ((JSONArray) property.get("rooms")).size(); i++) {
                JSONObject jsonRoom = (JSONObject) ((JSONArray) property.get("rooms")).get(i);
                JSONArray benefitList = (JSONArray) jsonRoom.get("benefits");
                Room tmpRoom = new Room();

                aminityCount = ((JSONArray) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("benefits"))
                        .size();
                tmpRoom.setId((Long) jsonRoom.get("roomId"));
                tmpRoom.setBlockId((String) jsonRoom.get("blockId"));
                tmpRoom.setName((String) jsonRoom.get("translatedRoomName"));
                List<String> tmpBenefits = new ArrayList<>();
                for (int j = 0; j < benefitList.size(); j++) {
                    if (!benefits.contains((String) ((JSONObject) benefitList.get(j)).get("translatedBenefitName"))) {
                        benefits.add((String) ((JSONObject) benefitList.get(j)).get("translatedBenefitName"));
                        tmpBenefits.add((String) ((JSONObject) benefitList.get(j)).get("translatedBenefitName"));
                    }
                }
                tmpRoom.setTags(tmpBenefits);
                price = (long) ((Double) ((JSONObject) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("totalPayment")).get("inclusive")).doubleValue();
                tmpRoom.setPrice(price);
                rate = (long) ((Double) ((JSONObject) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("rate")).get("inclusive")).doubleValue();
                tmpRoom.setRate(rate);
                rooms.add(tmpRoom);
                cancellationPolicy = (String) ((JSONObject) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("cancellationPolicy")).get("translatedCancellationText");
                // surcharges = (String) ((JSONObject) ((JSONObject) ((JSONArray)
                // property.get("rooms")).get(0))
                // .get("cancellationPolicy")).get("translatedCancellationText");
            }
            model.addAttribute("benefits", benefits);
            model.addAttribute("rooms", rooms);
            model.addAttribute("searchId", searchId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // AgodaHotel agoda = agodaHotelRepository.findByHotelId(pid).orElseThrow();
        Agoda agoda = agodaRepository.findByHotelId(pid).orElseThrow();
        // AgodaHotelDescription description = agodaHotelDescriptionRepository
        // .findByHotelId(pid).orElseThrow();
        // AgodaHotelAddress address = agodaHotelAddressRepository
        // .findByHotelIdAndAddressType(pid, "Local language").orElseThrow();
        // if (address.getAddressLine1() == null) {
        // address = agodaHotelAddressRepository
        // .findByHotelIdAndAddressType(pid, "English address").orElseThrow();
        // }
        // List<AgodaPicture> pictures = agodaPictureRepository.findByHotelId(pid);
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());
        model.addAttribute("room", room);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        model.addAttribute("hotel", agodaHotelRepository.findByHotelId(pid).orElse(new AgodaHotel()));
        // model.addAttribute("pictures", pictures);
        // model.addAttribute("description", description);
        model.addAttribute("cancellationPolicy", cancellationPolicy);
        model.addAttribute("surcharges", surcharges);
        model.addAttribute("agoda2", agoda);
        // model.addAttribute("agoda", new Hotel(agoda.getTranslatedName(),
        // (address.getAddressLine1() + " "
        // + (address.getAddressLine2() == null ? "" :
        // address.getAddressLine2())).trim(),
        // description.getOverview(),
        // pictures.get(0).getUrl().split("\\?")[0] + "?s=312x",
        // "우수", "w" + NumberFormat.getNumberInstance().format(price), pid, price,
        // (long) Math.ceil(agoda.getStarRating()),
        // aminityCount, agoda.getNumberOfReviews(), agoda.getRatingAverage()));
        model.addAttribute("agoda", new Hotel(agoda.getHotelTranslatedName(),
                (agoda.getAddressLine1() + " "
                        + (agoda.getAddressLine2() == null ? "" : agoda.getAddressLine2())).trim(),
                agoda.getOverviews(),
                agoda.getPhoto1().split("\\?")[0] + "?s=312x",
                "우수", "w" + NumberFormat.getNumberInstance().format(price), pid, price,
                (long) Math.ceil(agoda.getStarRating()),
                aminityCount, agoda.getNumberOfReviews(), agoda.getRatingAverage()));
        switch (role) {
            case ROLE_USER:
            default:
                return "detail";
        }
    }

    @GetMapping(value = "/cart")
    public String cart(Model model) {
        ERole role = Utils.getRole();
        List<Cart> carts = cartRepository.findByUser(userRepository.findById(Utils.getPid()).orElseThrow());
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("carts", carts);
        model.addAttribute("total", carts.stream().mapToLong(c -> c.getPrice()).sum());
        switch (role) {
            case ROLE_USER:
            default:
                return "cart";
        }
    }

    @GetMapping(value = "/reserve")
    public String reserve(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "reserve";
        }
    }

    @GetMapping(value = "/mypage")
    public String mypage(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
                User user = userRepository.findById(Utils.getPid()).orElseThrow();
                model.addAttribute("firstName", user.getFirstName());
                model.addAttribute("lastName", user.getLastName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("phone", user.getPhone());
                model.addAttribute("type", user.getSnsType() == null ? "email" : "google");
                return "mypage";
            default:
                return "redirect:/index";
        }
    }

    @GetMapping(value = "/success")
    public String success(@RequestParam("paymentKey") String paymentKey, @RequestParam("amount") Long amount,
            @RequestParam("orderId") String orderId) {

        try {
            if (paymentService.createEntity(Utils.getPid(), paymentKey, amount, orderId)) {
                return "success";
            } else {
                return "redirect:/index";
            }
        } catch (Exception e) {
            return "redirect:/index";
        }
    }

    @GetMapping(value = "/failed")
    public String failed() {
        return "failed";
    }
}