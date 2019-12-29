package ru.sergeykozhukhov.installedpackages.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.sergeykozhukhov.installedpackages.data.model.InstalledPackageModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Тестирование модели класса "InstalledPackageModel" уровня model
 */
public class InstalledPackageModelTest {


    /**
     * Тестирование на ревенство экземпляров
     */
    @Test
    public void equalsInstalledPackageModels(){

        List<InstalledPackageModel> testData1 = createTestData(false);
        List<InstalledPackageModel> testData2 = createTestData(false);
        List<InstalledPackageModel> testData3 = createTestData(true);

        assertEquals(testData1, testData2);
        assertNotEquals(testData1, testData3);
    }

    private List<InstalledPackageModel> createTestData(boolean isSystem) {
        List<InstalledPackageModel> testData = new ArrayList<>();

        testData.add(new InstalledPackageModel("Sberbank",
                "ru.sberbankmobile", null, false));
        testData.add(new InstalledPackageModel("SomeApp",
                "ru.package.some", null, false));
        if(isSystem){
            testData.add(new InstalledPackageModel("Test", "TestPackage",
                    null, true));
        }
        return testData;
    }
}
