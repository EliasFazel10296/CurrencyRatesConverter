package xyz.world.currency.rate.converter.data.download

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.EndpointInterface
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import java.util.concurrent.TimeUnit


/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateSupportedListData (var systemCheckpoints: SystemCheckpoints) {

    /**
     *  Download JSON Data from a Public API call on device.
     *  & Parse data on device.
     *  Process will trigger each second
     */
    @ExperimentalCoroutinesApi
    @SuppressLint("CheckResult")
    fun triggerRatesUpdate(context: Context, currencyDataViewModel: CurrencyDataViewModel) {

        val flowableItemsDataStructure = endpointInterfaceRetrofit()
            .downloadRatesData(
                if (CurrencyDataViewModel.baseCurrency.value == null) { PreferencesHandler(context).CurrencyPreferences().readSaveCurrency() }
                else { CurrencyDataViewModel.baseCurrency.value!! }
            )
            .doOnSubscribe {}
            .delay(30, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .filter {

                systemCheckpoints.networkConnection()
            }
        flowableItemsDataStructure.subscribe({
            Log.d("Json Result: Currency List", it.success.toString())








        }, {
            Log.d("Retrofit Error", "${it.message}")
        })
    }

    /**
     *  Initializing Retrofit & Json Parser
     */
    private fun endpointInterfaceRetrofit(): EndpointInterface {

        return Retrofit.Builder()
            .baseUrl(EndpointInterface.BASE_Link)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(EndpointInterface::class.java)
    }
}

/*WidgetDataModel widgetDataModel = new WidgetDataModel(
                                    System.currentTimeMillis(),
                                    appWidgetId,
                                    InstalledWidgetsAdapter.pickedWidgetPackageName,
                                    InstalledWidgetsAdapter.pickedWidgetClassNameProvider,
                                    InstalledWidgetsAdapter.pickedWidgetConfigClassName,
                                    functionsClass.appName(InstalledWidgetsAdapter.pickedWidgetPackageName),
                                    InstalledWidgetsAdapter.pickedWidgetLabel,
                                    false
                            );

                            WidgetDataInterface widgetDataInterface = Room.databaseBuilder(getApplicationContext(), WidgetDataInterface.class, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                                    .fallbackToDestructiveMigration()
                                    .addCallback(new RoomDatabase.Callback() {
                                        @Override
                                        public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onCreate(supportSQLiteDatabase);
                                        }

                                        @Override
                                        public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                            super.onOpen(supportSQLiteDatabase);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadConfiguredWidgets loadConfiguredWidgets = new LoadConfiguredWidgets();
                                                    loadConfiguredWidgets.execute();
                                                }
                                            });
                                        }
                                    })
                                    .build();
                            widgetDataInterface.initDataAccessObject().insertNewWidgetData(widgetDataModel);
                            widgetDataInterface.close();*/