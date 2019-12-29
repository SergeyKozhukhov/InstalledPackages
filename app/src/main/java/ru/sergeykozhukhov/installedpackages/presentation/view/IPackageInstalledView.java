package ru.sergeykozhukhov.installedpackages.presentation.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;

/**
 * Интерфейс, описывающий возможности View.
 */
public interface IPackageInstalledView {

    /**
     * Показать ProgressBar.
     */
    void showProgress();

    /**
     * Показать progress на ProgressBar.
     *
     * @param percent индикатор progress
     */
    void showPercentProgress(Integer percent);

    /**
     * Скрыть ProgressBar.
     */
    void hideProgress();

    /**
     * Отобразить данные об установленных приложениях.
     *
     * @param modelList список приложений.
     */
    void showData(@NonNull List<InstalledPackageModel> modelList);


    /**
     * Отобразить возможные варианты сортироки данных по приложениям
     *
     * @param sortOptionsList список приложений
     */
    void showSortOptions(@NonNull List<InstalledPackedSortOptionModel> sortOptionsList);
}