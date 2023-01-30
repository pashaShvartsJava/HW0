package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 11, 0), "Завтрак", 1300),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 28, 8, 0), "Завтрак", 730),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 10, 0), "Завтрак", 850)
        );
        meals.sort((o1, o2) -> {
            if (o1.getDateTime().getYear() > o2.getDateTime().getYear()) return 1;
            else if (o1.getDateTime().getYear() < o2.getDateTime().getYear()) return -1;
            else if (o1.getDateTime().getMonth().getValue() > o2.getDateTime().getMonth().getValue()) return 1;
            else if (o1.getDateTime().getMonth().getValue() < o2.getDateTime().getMonth().getValue()) return -1;
            else if (o1.getDateTime().getDayOfMonth() > o1.getDateTime().getDayOfMonth()) return 1;
            else if (o1.getDateTime().getDayOfMonth() < o2.getDateTime().getDayOfMonth()) return -1;
            else return 0;
        });

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0),
               LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0),
               LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime,
                                                            int caloriesPerDay) {
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        int realCaloriesPerDay = 0;
        Map<LocalDate, Integer> map = new TreeMap<>();
        int count = 0;
        for (int i = count; i < meals.size(); i++) {
            for (int j = i; j < meals.size(); j++) {
                if (meals.get(i).getDateTime().toLocalDate().equals(meals.get(j).getDateTime().toLocalDate())) {
                    realCaloriesPerDay += meals.get(j).getCalories();
                    count++;
                } else {
                    break;
                }
            }
            map.put(meals.get(i).getDateTime().toLocalDate(), realCaloriesPerDay);
            realCaloriesPerDay = 0;
            i = count - 1;
        }
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(
                    LocalTime.of(meal.getDateTime().getHour(), meal.getDateTime().getMinute()), startTime, endTime)
            ) {
                filteredMeals.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        excess(map.get(meal.getDateTime().toLocalDate()), caloriesPerDay)));
            }
        }
        return filteredMeals;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        Map<LocalDate, Integer> map = new TreeMap<>();
        meals.forEach(meal -> map.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));
        meals.forEach(meal -> {
            if (TimeUtil.isBetweenHalfOpen(
                    LocalTime.of(meal.getDateTime().getHour(), meal.getDateTime().getMinute()), startTime, endTime)
            ) {
                filteredMeals.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        excess(map.get(meal.getDateTime().toLocalDate()), caloriesPerDay)));
            }
        });
        return filteredMeals;
    }

    public static boolean excess(int realCaloriesPerDay, int caloriesPerDay) {
        return realCaloriesPerDay > caloriesPerDay;
    }
}
