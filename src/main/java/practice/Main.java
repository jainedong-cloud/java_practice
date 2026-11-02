package practice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Ann", "Bob", "Ada", "Chris"));
        List<String> newNames = names.stream().filter(name -> name.length() > 3).toList();
        System.out.println(newNames);
        List names2 = new ArrayList();
        names2.add("Ada");
        names2.add(42);
        String first = (String) names2.get(0);


    }

/*    public String cityName(Optional<User> user) {
*//*        if (user.isPresent()) {
            Address address = user.get().getAddress();
            if (address != null) {
                return address.getCity();
            }
        }
        return "Unknown";*//*
        user.map(a - > a.getAddress).map(a -> a.getCity()).orElse("Unknown")
    }*/
}
