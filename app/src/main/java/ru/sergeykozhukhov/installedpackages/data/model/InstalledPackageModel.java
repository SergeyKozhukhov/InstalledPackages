package ru.sergeykozhukhov.installedpackages.data.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Objects;

/**
 * Модель, для отображения данных о приложении.
 */
public class InstalledPackageModel {

    private String mAppName;

    private String mAppPackageName;

    private Drawable mAppIcon;

    private boolean mIsAppSystem;

    /**
     * Конструктор модели.
     *
     * @param appName        название приложения.
     * @param appPackageName имя пакета.
     * @param appIcon        иконка.
     * @param isAppSystem    идентицикация системеного приложения (true - системное)
     */
    public InstalledPackageModel(@NonNull String appName,
                                 @NonNull String appPackageName,
                                 @NonNull Drawable appIcon,
                                 boolean isAppSystem) {
        mAppName = appName;
        mAppPackageName = appPackageName;
        mAppIcon = appIcon;
        mIsAppSystem = isAppSystem;
    }

    @NonNull
    public String getAppName() {
        return mAppName;
    }

    @NonNull
    public String getAppPackageName() {
        return mAppPackageName;
    }

    @NonNull
    public Drawable getAppIcon() {
        return mAppIcon;
    }

    @NonNull
    public boolean getIsAppSystem() {
        return mIsAppSystem;
    }

    /**
     * Сравнение моделей по имени приложения
     */
    public static Comparator<InstalledPackageModel> BY_APP_NAME = new Comparator<InstalledPackageModel>() {
        @Override
        public int compare(InstalledPackageModel o1, InstalledPackageModel o2) {
            return o1.mAppName.compareTo(o2.mAppName);
        }
    };

    /**
     * Сравнение моделей по имени пакета
     */
    public static Comparator<InstalledPackageModel> BY_APP_PACKAGE_NAME = new Comparator<InstalledPackageModel>() {
        @Override
        public int compare(InstalledPackageModel o1, InstalledPackageModel o2) {
            return o1.mAppPackageName.compareTo(o2.mAppPackageName);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstalledPackageModel that = (InstalledPackageModel) o;
        return mIsAppSystem == that.mIsAppSystem &&
                Objects.equals(mAppName, that.mAppName) &&
                Objects.equals(mAppPackageName, that.mAppPackageName) &&
                Objects.equals(mAppIcon, that.mAppIcon);
    }


    @Override
    public int hashCode() {
        return Objects.hash(mAppName, mAppPackageName, mAppIcon, mIsAppSystem);
    }

    @Override
    public String toString() {
        return "InstalledPackageModel{" +
                "mAppName='" + mAppName + '\'' +
                ", mAppPackageName='" + mAppPackageName + '\'' +
                ", mAppIcon=" + mAppIcon +
                ", mIsAppSystem=" + mIsAppSystem +
                '}';
    }
}