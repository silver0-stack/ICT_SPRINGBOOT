package org.myweb.first;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class TEST {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "ADMIN";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
