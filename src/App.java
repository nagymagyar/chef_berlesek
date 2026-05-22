import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class App {
    public static void main(String[] args) throws Exception {
        List<Berles> rentals = loadRentals(Paths.get("chef_berlesek_2025.csv"));

        if (rentals.isEmpty()) {
            System.out.println("Nincs beolvasott bérlés a CSV fájlból.");
            return;
        }

        int month = askMonth();
        long yearRevenue = calculateRevenueForYear(rentals, 2025);
        Berles mostExpensive = findMostExpensiveRental(rentals);
        int distinctChefCount = countDistinctChefIds(rentals);
        MostFrequentChef mostFrequentChef = findMostFrequentChef(rentals);
        Map<String, Integer> cuisineCounts = countBookingsByCuisine(rentals);
        double averageDuration = calculateAverageDuration(rentals);

        System.out.printf("Összesen %d különböző séfet béreltek ki.%n", distinctChefCount);
        System.out.printf("A legtöbbször bérelt séf: %s (%d bérlés)%n", mostFrequentChef.name, mostFrequentChef.count);
        System.out.println("Bérlések száma konyhatípusonként:");
        for (Map.Entry<String, Integer> entry : cuisineCounts.entrySet()) {
            System.out.printf("%s: %d bérlés%n", entry.getKey(), entry.getValue());
        }
        System.out.printf("Átlagos bérlési időtartam: %.2f nap%n", averageDuration);
    }

    private static List<Berles> loadRentals(Path csvPath) throws Exception {
        List<Berles> rentals = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // fejléc
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 7) {
                    continue;
                }

                try {
                    int uid = Integer.parseInt(parts[0].trim());
                    int chefId = Integer.parseInt(parts[1].trim());
                    LocalDate startDate = LocalDate.parse(parts[2].trim());
                    LocalDate endDate = LocalDate.parse(parts[3].trim());
                    int dailyRate = Integer.parseInt(parts[4].trim());
                    String name = parts[5].trim();
                    String cuisine = parts[6].trim();

                    rentals.add(new Berles(uid, chefId, startDate, endDate, dailyRate, name, cuisine));
                } catch (NumberFormatException | DateTimeParseException e) {
                  
                }
            }
        }

        return rentals;
    }

    private static int askMonth() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Adjon meg egy hónapot (1-12): ");
                if (!scanner.hasNextInt()) {
                    scanner.nextLine();
                    System.out.println("Érvénytelen érték. Kérjük, adjon meg egy számot 1 és 12 között.");
                    continue;
                }

                int month = scanner.nextInt();
                if (month >= 1 && month <= 12) {
                    return month;
                }
                System.out.println("Érvénytelen hónap. Kérjük, 1 és 12 közötti számot adjon meg.");
            }
        }
    }

    private static long calculateRevenueForMonth(List<Berles> rentals, int month) {
        long total = 0;
        for (Berles rental : rentals) {
            if (rental.overlapsMonth(month)) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    private static long calculateRevenueForYear(List<Berles> rentals, int year) {
        long total = 0;
        for (Berles rental : rentals) {
            if (rental.overlapsYear(year)) {
                total += rental.getTotalPrice();
            }
        }
        return total;
    }

    private static MostFrequentChef findMostFrequentChef(List<Berles> rentals) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String mostFrequent = null;
        int maxCount = 0;

        for (Berles rental : rentals) {
            String name = rental.getName();
            int count = counts.getOrDefault(name, 0) + 1;
            counts.put(name, count);
            if (count > maxCount) {
                maxCount = count;
                mostFrequent = name;
            }
        }

        return new MostFrequentChef(mostFrequent, maxCount);
    }

    private static Berles findMostExpensiveRental(List<Berles> rentals) {
        Berles mostExpensive = rentals.get(0);
        for (Berles rental : rentals) {
            if (rental.getTotalPrice() > mostExpensive.getTotalPrice()) {
                mostExpensive = rental;
            }
        }
        return mostExpensive;
    }

    private static int countDistinctChefIds(List<Berles> rentals) {
        Set<Integer> chefIds = new HashSet<>();
        for (Berles rental : rentals) {
            chefIds.add(rental.getChefId());
        }
        return chefIds.size();
    }

    private static Map<String, Integer> countBookingsByCuisine(List<Berles> rentals) {
        Map<String, Integer> cuisineCounts = new TreeMap<>();
        for (Berles rental : rentals) {
            String cuisine = rental.getCuisine();
            cuisineCounts.put(cuisine, cuisineCounts.getOrDefault(cuisine, 0) + 1);
        }
        return cuisineCounts;
    }

    private static double calculateAverageDuration(List<Berles> rentals) {
        long totalDays = 0;
        for (Berles rental : rentals) {
            totalDays += rental.getDays();
        }
        return rentals.isEmpty() ? 0 : ((double) totalDays) / rentals.size();
    }

    private static class MostFrequentChef {
        private final String name;
        private final int count;

        MostFrequentChef(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
}
