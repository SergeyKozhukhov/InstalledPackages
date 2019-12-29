package ru.sergeykozhukhov.installedpackages.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;


/**
 * Модель отображения данных по виду сортирови
 */
public class InstalledPackedSortOptionModel {
    /*
    * mSortOption - вид сортировки
    * mDescription - описание
    * */
    private SortOption mSortOption;
    private String mDescription;

    public InstalledPackedSortOptionModel(@Nullable SortOption mSortOption, @Nullable String mDescription) {
        if (mSortOption == null)
            this.mSortOption = SortOption.NONE;
        this.mSortOption = mSortOption;

        if (mDescription == null)
            this.mDescription = "";
        this.mDescription = mDescription;
    }

    public SortOption getSortOption() {
        return mSortOption;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstalledPackedSortOptionModel that = (InstalledPackedSortOptionModel) o;
        return mSortOption == that.mSortOption &&
                Objects.equals(mDescription, that.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSortOption, mDescription);
    }

    @NonNull
    @Override
    public String toString() {
        return mDescription;
    }
}
