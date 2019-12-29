package ru.sergeykozhukhov.installedpackages.presentation.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;
import ru.sergeykozhukhov.installedpackages.data.model.SortOption;
import ru.sergeykozhukhov.installedpackages.data.repository.PackageInstalledRepository;
import ru.sergeykozhukhov.installedpackages.presentation.view.IPackageInstalledView;

/**
 * Presenter главного экрана приложения.
 */
public class PackageInstalledPresenter {

    // mMainActivityWeakReference - слабая ссылка на интерфейс, описывающий возможности View
    // общение с View через интерфейс
    private final WeakReference<IPackageInstalledView> mMainActivityWeakReference;

    // mPackageInstalledRepository - поставщик данных об установленных приложениях
    private final PackageInstalledRepository mPackageInstalledRepository;

     public PackageInstalledPresenter(@NonNull IPackageInstalledView mainActivity,
                                     @NonNull PackageInstalledRepository packageInstalledRepository) {
        mMainActivityWeakReference = new WeakReference<>(mainActivity);
        mPackageInstalledRepository = packageInstalledRepository;
    }

    /**
     * Метод для получения данных в синхронном режиме.
     *
     * @param isSystem загрузка данных с (@code true) включением/(@code false) исключением информации о системных приложениях.
     * @param  objectSortOption объект модели, описывающей вариант сортировки данных о приложениях.
     */
    // Данный метод нужен исключительно для понимания работы Unit-тестов.
    public void loadDataSync(boolean isSystem,  @Nullable final Object objectSortOption) {

        final SortOption sortOption;

        if (objectSortOption instanceof InstalledPackedSortOptionModel){
            sortOption = ((InstalledPackedSortOptionModel) objectSortOption).getSortOption();
        }
        else return;

        IPackageInstalledView packageInstalledView = mMainActivityWeakReference.get();
        if (packageInstalledView != null) {
            packageInstalledView.showProgress();

            List<InstalledPackageModel> data = mPackageInstalledRepository.getData(isSystem);

            packageInstalledView.hideProgress();

            if (sortOption != null)
                sortData(data, sortOption);

            packageInstalledView.showData(data);
        }
    }

    /**
     * Метод для загрузки данных в ассинхронном режиме.
     *
     * @param isSystem загрузка данных с (@code true) включением/(@code false) исключением информации о системных приложениях.
     * @param  objectSortOption объект модели, описывающей вариант сортировки данных о приложениях.
     */
    public void loadDataAsync(boolean isSystem, @Nullable final Object objectSortOption) {
        final SortOption sortOption;

        if (objectSortOption instanceof InstalledPackedSortOptionModel){
            sortOption = ((InstalledPackedSortOptionModel) objectSortOption).getSortOption();
        }
        else return;

        IPackageInstalledView packageInstalledView = mMainActivityWeakReference.get();
        if (packageInstalledView != null) {
            packageInstalledView.showProgress();
        }

        PackageInstalledRepository.OnLoadingFinishListener onLoadingFinishListener = new PackageInstalledRepository.OnLoadingFinishListener() {
            @Override
            public void onFinish(List<InstalledPackageModel> packageModels) {
                if (sortOption != null)
                    sortData(packageModels, sortOption);
                IPackageInstalledView installedView = mMainActivityWeakReference.get();
                if (installedView != null) {
                    installedView.hideProgress();
                    installedView.showData(packageModels);
                }
            }
        };

        PackageInstalledRepository.OnProgressUpdateListener onProgressUpdateListener = new PackageInstalledRepository.OnProgressUpdateListener() {
            @Override
            public void onUpdate(int progress) {
                IPackageInstalledView installedView = mMainActivityWeakReference.get();
                if (installedView != null) {
                    installedView.showPercentProgress(progress);
                }
            }
        };

        mPackageInstalledRepository.loadDataAsync(isSystem, onProgressUpdateListener, onLoadingFinishListener);
    }

    /**
     * Сортировка данных по приложениям
     *
     * @param data список моделей, содержащих информацию по приложенияи
     * @param sortOption вариант сортировки
     */
    public void sortData(@NonNull List<InstalledPackageModel> data, @Nullable SortOption sortOption){

        if (sortOption == null)
            return;
        switch (sortOption){
            case NONE:
                break;
            case BY_APP_NAME:
                Collections.sort(data, InstalledPackageModel.BY_APP_NAME);
                break;
            case BY_APP_PACKAGE_NAME:
                Collections.sort(data, InstalledPackageModel.BY_APP_PACKAGE_NAME);
                break;
        }
    }


    /**
     * Загрузка данных о возможных вариантах сортировки данных по приложениям
     */
    public void loadSortOptions(){
        IPackageInstalledView packageInstalledView = mMainActivityWeakReference.get();
        if (packageInstalledView != null){
            packageInstalledView.showSortOptions(mPackageInstalledRepository.getSortOptions());
        }
    }


    /**
     * Метод для отвязки прикрепленной View.
     */
    public void detachView() {
        mMainActivityWeakReference.clear();
    }


}
