package xyz.world.currency.rate.converter.data.database.currencies

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseDataModel
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel

data class WriteCurrenciesDatabaseEssentials(var databaseInterface: DatabaseInterface, var updateTimestamp: Long, var jsonObjectCurrencies: HashMap<String, String>, var currencyDataViewModel: CurrencyDataViewModel)

class WriteCurrenciesDatabase(var context: Context, var writeCurrenciesDatabaseEssentials: WriteCurrenciesDatabaseEssentials) {

    /**
     *  Saving List of All Supported Currencies in the Main Table of Database.
     */
    fun handleCurrenciesDatabase() {
        insertAllData()
    }

    private fun insertAllData()
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        val supportedCurrencies: ArrayList<DatabaseDataModel> = ArrayList<DatabaseDataModel>()

        writeCurrenciesDatabaseEssentials.jsonObjectCurrencies.forEach { (currencyCode, countryName )->
            supportedCurrencies.add(DatabaseDataModel(
                currencyCode,
                countryName,
                0.0,
                writeCurrenciesDatabaseEssentials.updateTimestamp.toString()))
        }

        writeCurrenciesDatabaseEssentials.databaseInterface.initDataAccessObject().insertDataBaseTable(supportedCurrencies)

        writeCurrenciesDatabaseEssentials.currencyDataViewModel.supportedCurrencyList.postValue(supportedCurrencies)

        writeCurrenciesDatabaseEssentials.databaseInterface.close()
    }
}