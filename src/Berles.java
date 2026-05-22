import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class Berles {
    private final int uid;
    private final int chefId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int dailyRate;
    private final String name;
    private final String cuisine;

    public Berles(int uid, int chefId, LocalDate startDate, LocalDate endDate, int dailyRate, String name, String cuisine) {
        this.uid = uid;
        this.chefId = chefId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.name = name;
        this.cuisine = cuisine;
    }

    public int getUid() {
        return uid;
    }

    public int getChefId() {
        return chefId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDailyRate() {
        return dailyRate;
    }

    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public long getTotalPrice() {
        return getDays() * dailyRate;
    }

    public boolean overlapsMonth(int month) {
        YearMonth current = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        while (!current.isAfter(endMonth)) {
            if (current.getMonthValue() == month) {
                return true;
            }
            current = current.plusMonths(1);
        }
        return false;
    }

    public boolean overlapsYear(int year) {
        return !startDate.isAfter(LocalDate.of(year, 12, 31)) && !endDate.isBefore(LocalDate.of(year, 1, 1));
    }
}
