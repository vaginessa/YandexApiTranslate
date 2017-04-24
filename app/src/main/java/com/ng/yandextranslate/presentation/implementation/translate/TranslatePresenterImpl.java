package com.ng.yandextranslate.presentation.implementation.translate;

import android.util.Log;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ng.yandextranslate.controller.data.service.history.HistoryDataService;
import com.ng.yandextranslate.controller.network.YandexTranslateApi;
import com.ng.yandextranslate.controller.network.data.response.LanguageListResponse;
import com.ng.yandextranslate.controller.network.data.response.TranslateResponse;
import com.ng.yandextranslate.model.pojo.LanguagePair;
import com.ng.yandextranslate.presentation.contract.translate.TranslateContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NGusarov on 17/03/17.
 */

public class TranslatePresenterImpl implements TranslateContract.Presenter {

    public static final String TAG = TranslatePresenterImpl.class.getSimpleName();

    private TranslateContract.View mView;
    private YandexTranslateApi mYandexTranslateApi;
    private HistoryDataService mHistoryDataServise;

    //todo unhardcode
    private static String LANG = "ru";

    @Inject
    public TranslatePresenterImpl(YandexTranslateApi api,
                                  HistoryDataService historyDataService,
                                  TranslateContract.View view) {
        this.mView = view;
        this.mYandexTranslateApi = api;
        this.mHistoryDataServise = historyDataService;
        loadSupportLanguages();
    }

    public void loadSupportLanguages() {
        Call<LanguageListResponse> call = mYandexTranslateApi.loadSupportedLangList(LANG);
        call.enqueue(new Callback<LanguageListResponse>() {
            @Override
            public void onResponse(final Call<LanguageListResponse> call, final Response<LanguageListResponse> response) {
                setSupportLangToView(response.body().getMapLangs(), response.body().getListDirs());
            }

            @Override
            public void onFailure(final Call<LanguageListResponse> call, final Throwable t) {
                Log.d(TAG, "error");
                t.printStackTrace();
            }
        });

//        RequestHelper.asyncRequest(mYandexTranslateApi.loadSupportedLangList(LANG),
//                data -> {
//                    compareSupportLanguages(data.getMapLangs(), data.getListDirs());
//                },
//                error -> {
//                    //todo need error processing to view
//                    Log.d(TAG, "ERROR IMPL LOAD SUPP LANG! ");
//                    error.printStackTrace();
//                }
//        );
    }

    private void setSupportLangToView(Map<String, String> supportedLangs, List<String> supportedLangDirs) {
        mView.setLanguages(supportedLangs, supportedLangDirs);
    }

    @Override
    public void getTranslate(String message, LanguagePair langPair) {
        Log.d(TAG, "GET TRANSLATE FOR MESSAGE: " + message);
        Log.d(TAG, "GET LANG: " + langPair.getLangPairStringValue());

        mView.showProgressBar();

        Call<TranslateResponse> call = mYandexTranslateApi.loadTranslateLang(message, langPair.getLangPairStringValue());
        call.enqueue(new Callback<TranslateResponse>() {
            @Override
            public void onResponse(final Call<TranslateResponse> call, final Response<TranslateResponse> response) {
                mView.showTranslateResult(response.body().getResponseText());
                mView.dismissProgressBar();
                mHistoryDataServise.addHistoryData(message, response.body().getResponseText(), langPair);
            }

            @Override
            public void onFailure(final Call<TranslateResponse> call, final Throwable t) {
                mView.showError(t.getMessage());
            }
        });
    }
}
