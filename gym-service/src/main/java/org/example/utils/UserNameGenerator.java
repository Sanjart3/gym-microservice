package org.example.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserNameGenerator {
    private static List<String> usernames = new ArrayList<>();
    public String generate(String firstName, String lastName) {
        String fullName = firstName+"."+lastName;
        long id = numberOfUsernames(fullName);
        String username;
        if (id==0) username = fullName;
        else username = fullName + "_" + (id+1);
        usernames.add(username);
        return username;
    }

    public long numberOfUsernames(String fullName) {
        return usernames.stream()
                .filter(object->object.startsWith(fullName))
                .count();
    }
}
