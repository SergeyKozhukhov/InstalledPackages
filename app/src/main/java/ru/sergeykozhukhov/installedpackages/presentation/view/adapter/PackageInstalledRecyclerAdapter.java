package ru.sergeykozhukhov.installedpackages.presentation.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.List;

import ru.sergeykozhukhov.installedpackages.R;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;

/**
 * Адаптер для отображения элементов списка с информацией по приложениям.
 */
public class PackageInstalledRecyclerAdapter extends Adapter<PackageInstalledRecyclerAdapter.PackageInstalledViewHolder> {

    /*
    * mInstalledPackageModelList - список моделей с информацией по приложениям
    * */
    private List<InstalledPackageModel> mInstalledPackageModelList;

    public PackageInstalledRecyclerAdapter(@NonNull List<InstalledPackageModel> installedPackageModelList) {
        mInstalledPackageModelList = installedPackageModelList;
    }

    @NonNull
    @Override
    public PackageInstalledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageInstalledViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.package_installed_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PackageInstalledViewHolder holder, int position) {
        holder.bindView(mInstalledPackageModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mInstalledPackageModelList.size();
    }

    static class PackageInstalledViewHolder extends RecyclerView.ViewHolder {

        private TextView mAppTextView;
        private TextView mPackageNameTextView;
        private ImageView mIconImageView;
        private ImageView mSystemImageView;

        PackageInstalledViewHolder(@NonNull View itemView) {
            super(itemView);

            mAppTextView = itemView.findViewById(R.id.app_name_text_view);
            mPackageNameTextView = itemView.findViewById(R.id.app_package_text_view);
            mIconImageView = itemView.findViewById(R.id.app_icon_image_view);
            mSystemImageView = itemView.findViewById(R.id.app_system_image_view);
        }

        void bindView(@NonNull InstalledPackageModel installedPackageModel) {
            mAppTextView.setText(installedPackageModel.getAppName());
            mPackageNameTextView.setText(installedPackageModel.getAppPackageName());
            mIconImageView.setImageDrawable(installedPackageModel.getAppIcon());
            if (installedPackageModel.getIsAppSystem()){
                mSystemImageView.setImageResource(R.drawable.ic_phonelink_setup_black_24dp);
            }
            else
                mSystemImageView.setImageDrawable(null);

        }
    }
}
