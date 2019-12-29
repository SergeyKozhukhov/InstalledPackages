package ru.sergeykozhukhov.installedpackages.presentation.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import ru.sergeykozhukhov.installedpackages.R;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;
import ru.sergeykozhukhov.installedpackages.data.repository.PackageInstalledRepository;
import ru.sergeykozhukhov.installedpackages.presentation.presenter.PackageInstalledPresenter;
import ru.sergeykozhukhov.installedpackages.presentation.view.adapter.PackageInstalledRecyclerAdapter;
import ru.sergeykozhukhov.installedpackages.presentation.view.adapter.PackageInstalledSpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Главное активити приложения. Умеет показывать список установленных приложений на телефоне.
 */
public class PackageInstalledActivity extends AppCompatActivity implements IPackageInstalledView {

    private RecyclerView mInstalledPackagesRecyclerView; // список информации по приложениям
    private Spinner mSortOptionsSpinner; // список вариантов сорторивки информации по приложениям
    private CheckBox mIsLoadSystemCheckBox; // определение, загружать ли информацию по системным приложениям
    private ImageView mLoadInstalledPackagesImageView; // загрузка данных по приложениям
    private View mProgressLoadFrameLayout; // поле для отображениям процесса загрузки данных
    private ProgressBar mPercentLoadedProgressBar; // индикатор хода загрузки данных
    private TextView mPercentLoadedTextView; // индикатор хода загрузки данных

    // стоит вынести в уровень "model". Сделано для быстроты реализации.
    private boolean mFlagIsFirstLoaded = true; // флаг для определения первый ли раз загружены данные

    private PackageInstalledPresenter mMainPresenter; // презентер данного окна

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews(); // инициализация views
        providePresenter(); // инициализация презентера
        initListeners(); // инициализация обработчиков нажатия на элементы
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        mMainPresenter.loadSortOptions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgress() {
        mProgressLoadFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPercentProgress(Integer percent) {
        mPercentLoadedProgressBar.setProgress(percent);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.percent_progress_text_message_1));
        stringBuilder.append(percent);
        stringBuilder.append(getString(R.string.percent_progress_text_message_2));
        mPercentLoadedTextView.setText(stringBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        mProgressLoadFrameLayout.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showData(@NonNull List<InstalledPackageModel> modelList) {
        PackageInstalledRecyclerAdapter adapter = new PackageInstalledRecyclerAdapter(modelList);
        mInstalledPackagesRecyclerView.setAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSortOptions(@NonNull List<InstalledPackedSortOptionModel> sortOptionsList) {
        PackageInstalledSpinnerAdapter packageInstalledSpinnerAdapter = new PackageInstalledSpinnerAdapter(sortOptionsList);
        mSortOptionsSpinner.setAdapter(packageInstalledSpinnerAdapter);
    }

    /**
     * Инициализация презентера
     */
    private void providePresenter() {
        PackageInstalledRepository packageInstalledRepository = new PackageInstalledRepository(this);
        mMainPresenter = new PackageInstalledPresenter(this, packageInstalledRepository);
    }


    /**
     * Инициализация Views
     */
    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mInstalledPackagesRecyclerView = findViewById(R.id.list_data_app_recycler_view);
        mInstalledPackagesRecyclerView.setLayoutManager(layoutManager);
        mSortOptionsSpinner = findViewById(R.id.sort_options_spinner);
        mIsLoadSystemCheckBox = findViewById(R.id.system_packages_check_box);
        mLoadInstalledPackagesImageView = findViewById(R.id.load_data_image_view);
        mProgressLoadFrameLayout = findViewById(R.id.progress_frame_layout);
        mPercentLoadedProgressBar = findViewById(R.id.percent_loaded_progress_bar);
        mPercentLoadedTextView = findViewById(R.id.comment_progress_text_view);
    }

    /**
     * Инициализация обработчиков нажатия на элементы
     */
    private void initListeners(){

        mLoadInstalledPackagesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSystem = mIsLoadSystemCheckBox.isChecked();
                 mMainPresenter.loadDataAsync(isSystem, mSortOptionsSpinner.getSelectedItem());
                 if(mFlagIsFirstLoaded){
                     mLoadInstalledPackagesImageView.setImageResource(R.drawable.ic_update_black_24dp);
                     mFlagIsFirstLoaded = false;
                 }
            }
        });
    }
}
