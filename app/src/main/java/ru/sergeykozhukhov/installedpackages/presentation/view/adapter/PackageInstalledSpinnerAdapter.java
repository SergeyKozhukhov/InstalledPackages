package ru.sergeykozhukhov.installedpackages.presentation.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;

/**
 * Адаптер для отображения возможных вариантов сортировки данных по приложениям
 */
public class PackageInstalledSpinnerAdapter extends BaseAdapter {

    /*
    * Список возможных вариантов сортировки данных по приложениям
    * */
    private List<InstalledPackedSortOptionModel> mInstalledPackageSortList;

    public PackageInstalledSpinnerAdapter(List<InstalledPackedSortOptionModel> mSortOptions){
        mInstalledPackageSortList = mSortOptions;
    }

    @Override
    public int getCount() {
        return mInstalledPackageSortList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInstalledPackageSortList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);

            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder)convertView.getTag();
        holder.mSortOptionTextView.setText(getItem(position).toString());

        return  convertView;
    }

    private class ViewHolder{

        private final TextView mSortOptionTextView;

        private ViewHolder(View view){
            mSortOptionTextView = view.findViewById(android.R.id.text1);
        }
    }
}


