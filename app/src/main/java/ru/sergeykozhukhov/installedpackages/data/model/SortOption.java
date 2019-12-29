package ru.sergeykozhukhov.installedpackages.data.model;


/**
 * Модель возможных вариантов сортировки спика моделей данных приложения
 *
 * NONE - отсутствие сортировки
 * BY_APP_NAME - сортировка по имени приложения
 * BY_APP_PACKAGE_NAME - сортировка по имени пакета
 */
public enum SortOption{
    NONE,
    BY_APP_NAME,
    BY_APP_PACKAGE_NAME
}
