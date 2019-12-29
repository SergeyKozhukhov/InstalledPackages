package ru.sergeykozhukhov.installedpackages.presentation.presenter;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;
import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackedSortOptionModel;
import ru.sergeykozhukhov.installedpackages.data.model.SortOption;
import ru.sergeykozhukhov.installedpackages.data.repository.PackageInstalledRepository;
import ru.sergeykozhukhov.installedpackages.presentation.view.IPackageInstalledView;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Тестирование функций класса "PackageInstalledPresenter" уровня "Presenter".
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageInstalledPresenterTest {

    @Mock
    private IPackageInstalledView mPackageInstalledView; // интерфейс, описывающий возможности View.

    @Mock
    private PackageInstalledRepository mPackageInstalledRepository; // провайдер данных об установленных приложениях.

    @Mock
    private PackageInstalledPresenter mMainPresenter; // presenter главного экрана приложения.

    /**
     * Данный метод будет вызван перед каждым тестовым методом.
     */
    @Before
    public void setUp() {
        mMainPresenter = new PackageInstalledPresenter(mPackageInstalledView, mPackageInstalledRepository);
    }

    /**
     * Тестирование синхронного получения данных в презентере.
     */
    @Test
    public void testLoadDataSync() {
        List<InstalledPackageModel> testData = createTestData();
        //Создание мока для получения данных из репозитория (необходимо создавать мок до вызова тестируемого метода)
        when(
                mPackageInstalledRepository.getData(anyBoolean())
        ).
                thenReturn(testData);

        //Вызов тестируемого метода
        mMainPresenter.loadDataSync(true, createTestSortOption());

        //Проверка, что презентер действительно вызывает методы представления
        verify(mPackageInstalledView).showProgress();
        verify(mPackageInstalledView).showData(testData);
        verify(mPackageInstalledView).hideProgress();
    }

    /**
     * Тестирование синхронного метода получения данных в презентере.
     * <p> В данном тесте дополнительно проверяется порядок вызова методов. Если попробуйте поменять очередность или добавить какой-либо вызов
     * метода {@link IPackageInstalledView} в {@link PackageInstalledPresenter}, данный тест не пройдет.
     */
    @Test
    public void testLoadDataSync_withOrder() {

        List<InstalledPackageModel> testData = createTestData();

        //Создание мока для получения данных из репозитория (необходимо создавать мок до вызова тестируемого метода)
        when(
                mPackageInstalledRepository.getData(anyBoolean())
        ).
                thenReturn(testData);

        //Вызов тестируемого метода
        mMainPresenter.loadDataSync(true, createTestSortOption());

        InOrder inOrder = Mockito.inOrder(mPackageInstalledView);

        //Проверка, что презентер действительно вызывает методы представления, причем в порядке вызова этих методов. Можно сравнить с предыдущим тестом.
        inOrder.verify(mPackageInstalledView).showProgress();
        inOrder.verify(mPackageInstalledView).hideProgress();
        inOrder.verify(mPackageInstalledView).showData(testData);

        //Проверка, что никакой метод не будет вызван у mPackageInstalledView.
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Тестирование асинхронного метода получения данных в презентере.
     */
    @Test
    public void testLoadDataAsync() {
        final List<InstalledPackageModel> testData = createTestData();

        //Здесь происходит магия. Нам нужно выдернуть аргумент, переданный в mPackageInstalledRepository в качетсве слушателя и немедленно вернуть
        //какой-то результат. Ведь нам неважно, каким образом отработает mPackageInstalledRepository#loadDataAsync(), важно, что этот метод должен вернуть
        //в колбеке.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                //получаем слушателей из метода loadDataAsync().
                PackageInstalledRepository.OnProgressUpdateListener onProgressUpdateListener =
                        (PackageInstalledRepository.OnProgressUpdateListener) invocation.getArguments()[1];

                PackageInstalledRepository.OnLoadingFinishListener onLoadingFinishListener =
                        (PackageInstalledRepository.OnLoadingFinishListener) invocation.getArguments()[2];

                //кидаем в них ответ
                onProgressUpdateListener.onUpdate(anyInt());
                onLoadingFinishListener.onFinish(testData);

                return null;
            }
        }).when(mPackageInstalledRepository).loadDataAsync(
                anyBoolean(),
                Mockito.any(PackageInstalledRepository.OnProgressUpdateListener.class),
                Mockito.any(PackageInstalledRepository.OnLoadingFinishListener.class)
        );

        mMainPresenter.loadDataAsync(true, createTestSortOption());

        //Далее просто проверяем, что все будет вызвано в нужном порядке.
        InOrder inOrder = Mockito.inOrder(mPackageInstalledView);
        inOrder.verify(mPackageInstalledView).showProgress();
        inOrder.verify(mPackageInstalledView).showPercentProgress(anyInt());
        inOrder.verify(mPackageInstalledView).hideProgress();
        inOrder.verify(mPackageInstalledView).showData(testData);

        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Тестирование {@link PackageInstalledPresenter#sortData(List, SortOption)} на верность сортировки.
     */
    @Test
    public void testSortData(){
        List<InstalledPackageModel> testDataWithoutSort = createTestData(SortOption.NONE);
        List<InstalledPackageModel> testDataSortByAppName = createTestData(SortOption.BY_APP_NAME);
        List<InstalledPackageModel> testDataSortByAppPackageName = createTestData(SortOption.BY_APP_PACKAGE_NAME);

        mMainPresenter.sortData(testDataWithoutSort, SortOption.BY_APP_NAME);
        assertEquals(testDataWithoutSort, testDataSortByAppName);

        mMainPresenter.sortData(testDataWithoutSort, SortOption.BY_APP_PACKAGE_NAME);
        assertEquals(testDataWithoutSort, testDataSortByAppPackageName);
    }

    /**
     * Тестирование {@link PackageInstalledPresenter#loadSortOptions()} на вызов метода загрузки вариантов сортировки.
     */
    @Test
    public void testLoadSortOptions() {
        //Создание мока для получения данных из репозитория (необходимо создавать мок до вызова тестируемого метода)
        when(
                mPackageInstalledRepository.getSortOptions()
        ).
                thenReturn(createTestListSortOptions());

        //Вызов тестируемого метода
        mMainPresenter.loadSortOptions();

        //Проверка, что презентер действительно вызывает методы представления
        verify(mPackageInstalledView).showSortOptions(createTestListSortOptions());
    }

    /**
     * Тестирование {@link PackageInstalledPresenter#detachView()}.
     *
     * <p> после детача, все методы не будут ничего прокидывать в {@link IPackageInstalledView}.
     */
    @Test
    public void testDetachView() {
        mMainPresenter.detachView();

        mMainPresenter.loadDataAsync(true, createTestSortOption());
        mMainPresenter.loadDataSync(true, createTestSortOption());
        mMainPresenter.loadSortOptions();

        verifyNoMoreInteractions(mPackageInstalledView);
    }


    /**
     * Создание тестового списка моделей с информацией по приложениям
     * @return список моделей, описывающих приложения
     */
    private List<InstalledPackageModel> createTestData() {
        List<InstalledPackageModel> testData = Arrays.asList(
                new InstalledPackageModel("Sberbank","ru.sberbankmobile", null, false),
                new InstalledPackageModel("Test", "com.another.package.test", null, true),
                new InstalledPackageModel("Application","ru.package.app", null, false)
        );
        return testData;
    }

    /**
     * Создание тестового списка моделей с информацией по приложениям, отсторированных в зависимости от указанного вида сортировки
     * @param sortOption вид сортировки
     * @return список моделей, описывающих приложения
     */
    private List<InstalledPackageModel> createTestData(@NonNull SortOption sortOption){
        List<InstalledPackageModel> testData;

        switch (sortOption){
            case NONE:
                testData = createTestData();
                break;
            case BY_APP_NAME:
                testData = Arrays.asList(
                        new InstalledPackageModel("Application","ru.package.app", null, false),
                        new InstalledPackageModel("Sberbank","ru.sberbankmobile", null, false),
                        new InstalledPackageModel("Test", "com.another.package.test",null, true)
                );
                break;
            case BY_APP_PACKAGE_NAME:
                testData = Arrays.asList(
                        new InstalledPackageModel("Test", "com.another.package.test", null, true),
                        new InstalledPackageModel("Application","ru.package.app", null, false),
                        new InstalledPackageModel("Sberbank","ru.sberbankmobile", null, false)
                );
                break;
                default:
                    testData = null;
        }
        return testData;
    }

    /**
     * Создание тестовой модели сортировки
     *
     * @return модель, описывающую сортировку данных по придожениям
     */
    private InstalledPackedSortOptionModel createTestSortOption(){
        return new InstalledPackedSortOptionModel(SortOption.BY_APP_NAME, "A-Z appName");
    }

    /**
     * Создание списка тестовых моделей сортировки
     *
     * @return список моделей, описывающих виды сортировки данных по придожениям
     */
    private List<InstalledPackedSortOptionModel> createTestListSortOptions(){
        return Arrays.asList(
                new InstalledPackedSortOptionModel(SortOption.NONE, "Sort"),
                new InstalledPackedSortOptionModel(SortOption.BY_APP_NAME, "A-Z appName"),
                new InstalledPackedSortOptionModel(SortOption.BY_APP_PACKAGE_NAME, "A-Z PackageName")
        );
    }

}