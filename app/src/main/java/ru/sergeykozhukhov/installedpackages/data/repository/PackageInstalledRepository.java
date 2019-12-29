package ru.sergeykozhukhov.installedpackages.data.repository;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.sergeykozhukhov.installedpackages.R;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;
import ru.sergeykozhukhov.installedpackages.data.model.SortOption;

/**
 * Репозиторий - провайдер данных об установленных приложениях.
 */

/*
* PackageInfo имеет доступ ко всему содержимому manifest файла.
* Используется, если есть необходимость считывать каждый параметр manifest файла.
*
* ApplicationInfo имеет доступ к определенному тэгу manifest файла.
* Используеться, когда требуется получить только определенный параметра manifest файла.
*
* ResolveInfo имеет доступ к intent-filters.
*
* ApplicationInfo и ResolveInfo - просто удобные классы, которые мы можем использовать в соотстветствии с нашими требованиями.
* */
public class PackageInstalledRepository {

    // Context нужен для обращения к PackageManager
    private final Context mContext;
    // PackageManager - менеджер пакетов,
    // отвечающий за установку, обновление, удаление приложений, и хранящий информацию о них
    private final PackageManager mPackageManager;

    /**
     * Конструктор провайдера данных для установленных приложений.
     *
     * @param context {@link Context} контекст для получения зависимости {@link PackageManager}.
     */
    public PackageInstalledRepository(@NonNull Context context) {
        mContext = context;
        mPackageManager = context.getPackageManager(); // получение экземпляра PackageManager
    }

    /**
     * Метод для асинхронной загрузки данных об установленных в системе приложениях.
     *
     * @param isSystem                {@code true} если необходимо показывать системные приложения, {@code false} иначе.
     * @param onLoadingFinishListener {@link OnProgressUpdateListener} слушатель выполнения шага загрузки.
     * @param onLoadingFinishListener {@link OnLoadingFinishListener} слушатель окончания загрузки.
     */
    public void loadDataAsync(boolean isSystem, @NonNull OnProgressUpdateListener onProgressUpdateListener, @NonNull OnLoadingFinishListener onLoadingFinishListener) {
        LoadingPackagesAsyncTask loadingPackagesAsyncTask = new LoadingPackagesAsyncTask(onProgressUpdateListener, onLoadingFinishListener);
        loadingPackagesAsyncTask.execute(isSystem);
    }

    /**
     * Метод для синхронной загрузки данных об установленных в системе приложениях.
     *
     * @param isSystem {@code true} если необходимо показывать системные приложения, {@code false} иначе.
     */
    public List<InstalledPackageModel> getData(boolean isSystem) {
        List<InstalledPackageModel> installedPackageModels = new ArrayList<>();

        List<String> installedPackages = getInstalledPackages(isSystem);

        for (String packageName : installedPackages) {
            getAppSize(packageName);

            InstalledPackageModel installedPackageModel = new InstalledPackageModel(
                    getAppName(packageName), packageName, getAppIcon(packageName),
                    isSystem && isSystemPackage(packageName));

            installedPackageModels.add(installedPackageModel);
        }
        return installedPackageModels;
    }

    /**
     * Получение доступных вариантов сортировки данных о приложениях
     *
     * @return список доступных вариантов сортировки данных о приложениях
     */
    public List<InstalledPackedSortOptionModel> getSortOptions(){
        return Arrays.asList(
                new InstalledPackedSortOptionModel(null, mContext.getString(R.string.sort_option_none_description)),
                new InstalledPackedSortOptionModel(SortOption.BY_APP_NAME, mContext.getString(R.string.sort_option_by_app_name_description)),
                new InstalledPackedSortOptionModel(SortOption.BY_APP_PACKAGE_NAME, mContext.getString(R.string.sort_option_by_app_package_name_description))
        );
    }

    /**
     * Получение списка наименований установленных пакетов
     *
     * @param isSystem определяет, включать ли системные (true) пакеты в список
     * @return список наименований установленных пакетов
     */
    private List<String> getInstalledPackages(boolean isSystem) {
        List<String> apkPackageName = new ArrayList<>();

        // ACTION_MAIN - входная точка приложения
        // CATEGORY_LAUNCHER - указывает на то, что значок данной активности следует поместить в средство запуска приложений системы
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // queryIntentActivities возвращает все activities, использующие подобный intent
        // соритируеться в порядке от самого предпочтительного для выполнения данного intent к наимению предпочтительного
        // возможная проблема: возвращение "дубликатов".
        // Допустимо использоваться getInstalledApplication. В таком случае будут возвращены и системные службы,
        // которые могут не иметь activity
        List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (isSystem || !isSystemPackage(resolveInfo)) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                apkPackageName.add(activityInfo.applicationInfo.packageName);
            }
        }

        return apkPackageName;
    }

    /**
     * Получение наименования приложения
     *
     * @param packageName  - наименование пакета
     * @return наименование приложения
     */
    private String getAppName(@NonNull String packageName) {
        String appName = "";
        ApplicationInfo applicationInfo;

        try {
            // получение всей информации о пакете
            // второй аргумент - флаг фильтрации. В данном случае никакой фильтрации не производится
            applicationInfo = mPackageManager.getApplicationInfo(packageName, 0);
            // получение имени приложения по applicationInfo
            appName = (String) mPackageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * Получение изображения пакета
     *
     * @param packageName - наименование пакета
     * @return изображение, ассоциируемое с данным пакетом или стандартное изображения, в случае отсутвия пакетного
     */
    private Drawable getAppIcon(@NonNull String packageName) {
        // Drawable - базовый класс для всех классов работы с графикой.
        // Представляет собой общую абстракцию для рисуемого объекта на Canvas
        Drawable drawable;
        try {
            drawable = mPackageManager.getApplicationIcon(packageName); // извлечение изображения по имени пакета
        }
        catch (PackageManager.NameNotFoundException e) // в случае ошибки производиться загрузка изображения с id "R.mipmap.ic_launcher"
        {
            e.printStackTrace();
            // ContextCompat - класс из support library, обеспечивающий доступ к функциям Context.
            // getDrawable - получение изображение по id ресурса
            drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher);
        }

        return drawable;
    }

    //данный метод не очень просто реализовать. здесь он нужен только для того, чтобы увеличить время загрузки и понаслаждаться работой презентера
    //по переключению видов.
    private int getAppSize(@NonNull String packageName) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Определение, относится ли данный пакет к системным
     *
     * @param resolveInfo - информация о пакете, полученная с помощью intent
     * @return true - если системное, false - в противном случае
     */
    private boolean isSystemPackage(@NonNull ResolveInfo resolveInfo) {
        // FLAG_SYSTEM - флаг, определяющий, что приложение системное
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * Определение, относится ли данный пакет к системным
     *
     * @param packageName - наименование пакета
     * @return true - если системное, false - в противном случае
     */
    private boolean isSystemPackage(@NonNull String packageName){
        try {
            ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(
                        packageName, 0);
            return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
             return false;
        }
    }



    /**
     * Класс для выполнения ассинхронной загрузки данных
     */
    private class LoadingPackagesAsyncTask extends AsyncTask<Boolean, Integer, List<InstalledPackageModel>> {

        private final OnProgressUpdateListener mOnProgressUpdateListener;
        private final OnLoadingFinishListener mOnLoadingFinishListener;

        LoadingPackagesAsyncTask(@NonNull OnProgressUpdateListener onProgressUpdateListener, @NonNull OnLoadingFinishListener onLoadingFinishListener) {
            mOnProgressUpdateListener = onProgressUpdateListener;
            mOnLoadingFinishListener = onLoadingFinishListener;
        }

        @Override
        protected List<InstalledPackageModel> doInBackground(Boolean... booleans) {

            List<InstalledPackageModel> installedPackageModels = new ArrayList<>();

            List<String> installedPackages = getInstalledPackages(booleans[0]);

            int size = installedPackages.size();

            int current = 0;
            for (String packageName : installedPackages) {
                getAppSize(packageName);

                InstalledPackageModel installedPackageModel = new InstalledPackageModel(
                        getAppName(packageName), packageName, getAppIcon(packageName),
                        booleans[0] && isSystemPackage(packageName));

                installedPackageModels.add(installedPackageModel);

                Integer percent = current*100/size; // определение процентра загрузки данных

                publishProgress(percent);
                current++;
            }

            return installedPackageModels;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mOnProgressUpdateListener.onUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(List<InstalledPackageModel> installedPackageModels) {
            super.onPostExecute(installedPackageModels);
            mOnLoadingFinishListener.onFinish(installedPackageModels);
        }
    }

    /**
     * Интерфейс слушателя выполнения шага загрузки данных
     */
    public interface OnProgressUpdateListener{

        /**
         * Метод, вызываемый в момент выполнения шага загрузки данных
         *
         * @param progress текущее состояние прогресса загрузки данных, измеряеться в пределах [0, 100]
         */
        void onUpdate(int progress);
    }

    /**
     * Интерфейс слушателя окончания загрузки данных.
     */
    public interface OnLoadingFinishListener {

        /**
         * Метод, вызываемый после окончания загрузки данных.
         *
         * @param packageModels {@link List} of {@link InstalledPackageModel} список приложений установленных в системе.
         */
        void onFinish(List<InstalledPackageModel> packageModels);
    }
}
