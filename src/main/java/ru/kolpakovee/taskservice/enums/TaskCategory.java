package ru.kolpakovee.taskservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskCategory {
    CLEANING("Уборка"),
    SHOPPING("Покупки"),
    FINANCES("Финансы"),
    HYGIENE("Гигиена"),
    GENERAL_CHORES("Общие обязанности"),
    ENERGY_SAVING("Энергосбережение"),
    APPLIANCES_AND_DEVICES("Техника и устройства");

    private final String displayName;
}
