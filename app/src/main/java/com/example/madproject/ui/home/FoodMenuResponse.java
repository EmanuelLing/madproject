package com.example.madproject.ui.home;

import java.util.List;

public class FoodMenuResponse {
    private List<MenuFoodModel> foodMenu;
    private List<MenuDrinkModel> drinkMenu;

    public List<MenuFoodModel> getFoodMenu() {
        return foodMenu;
    }

    public List<MenuDrinkModel> getDrinkMenu() {
        return drinkMenu;
    }
}