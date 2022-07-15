package com.hvantage.b4eemp.utils;

import com.hvantage.b4eemp.Database.DataModel;

import java.util.ArrayList;
import java.util.List;

public class StaticData {
    private static final StaticData ourInstance = new StaticData();

    private StaticData() {
    }

    public static StaticData getInstance() {
        return ourInstance;
    }

    public List<DataModel> getIdProof() {
        List<DataModel> dataModels = new ArrayList<>();
        DataModel dataModel = new DataModel();
        dataModel.setId(1);
        dataModel.setName("ID Card");
        dataModels.add(dataModel);

        dataModel = new DataModel();
        dataModel.setId(2);
        dataModel.setName("Aadhar Card");
        dataModels.add(dataModel);

        dataModel = new DataModel();
        dataModel.setId(3);
        dataModel.setName("Pan Card");
        dataModels.add(dataModel);

        return dataModels;
    }

}
