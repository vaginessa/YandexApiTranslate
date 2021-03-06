package com.ng.yandextranslate.controller.data.service.history;

import com.ng.yandextranslate.controller.data.db.Repository;
import com.ng.yandextranslate.model.db.table.HistoryShema;
import com.ng.yandextranslate.model.pojo.HistoryData;
import com.ng.yandextranslate.model.pojo.LanguagePair;

import java.util.List;

/**
 * Created by NG on 11.04.17.
 */

public class HistoryDataService {

    public static final String TAG = HistoryDataService.class.getSimpleName();

    private Repository repository;

    public HistoryDataService(Repository repository) {
        this.repository = repository;
    }

    public void addHistoryData(HistoryData data) {
        repository.addHistory(data);
    }

    public void addHistoryData(String originalText, String translateText, LanguagePair languagePair ) {
        addHistoryData(new HistoryData(languagePair, originalText, translateText));
    }

    public List<HistoryData> getHistoryDataList() {
        return repository.getAllHistories();
    }

    public List<HistoryData> getFavoriteHistoryDataList() {
        return repository.getFavoriteHistories();
    }

    public void deleteHistoryData(int id) {
        repository.deleteHistoryData(id);
    }

    public void deleteAllHistoryData() {
        repository.deleteAllHistoryData();
    }

    public HistoryData getHistory(int id) {
        return repository.getHistory(id);
    }

    public void makeFavorite(int id, boolean isFavorite) {
        repository.makeHistoryFavorite(id, isFavorite);
    }

    public void unMakeAllFavorites() {
        List<HistoryData> favorites = getFavoriteHistoryDataList();
        for (HistoryData history: favorites) {
            makeFavorite(history.getKey(), false);
        }
    }

    public long getHistoryCount() {
        return repository.getDataCount(HistoryShema.HistoryTable.NAME);
    }

    public int getLastHistoryId() {
        return repository.getLastHistoryId();
    }
}
