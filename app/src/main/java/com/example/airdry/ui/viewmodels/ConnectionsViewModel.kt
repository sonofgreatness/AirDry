package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.ProductKey
import com.example.airdry.domain.REST_API.data_class.Asset
import com.example.airdry.domain.REST_API.data_class.ReleaseResponse
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import com.example.airdry.domain.repository.interfaces.ProductKeyRepository
import com.example.airdry.domain.use_cases.DownloadUpdateUseCase
import com.example.airdry.util.Constants.Companion.PRODUCT_KEY
import com.example.airdry.util.operational_states.WorkerReleaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ConnectionsViewModel @Inject constructor(
    private val context : Application,
   private val productKeyRepo: ProductKeyRepository,
    private val api: GithubApi,
    private  val downloadUpdateUseCase: DownloadUpdateUseCase
) : ViewModel(){



    private val _releases = MutableStateFlow<List<ReleaseResponse>>(emptyList())
    val releases: StateFlow<List<ReleaseResponse>> = _releases
    private  val _isProductKeyvalid  = MutableLiveData<Boolean>()
    val isProductKeyvalid: LiveData<Boolean> get() = _isProductKeyvalid


    init{
        fetchAllReleases()
      //  checkProductKey()
    }




    fun fetchAllReleases() {
        viewModelScope.launch {
            try {
                val response = api.getAllReleases() // <-- you'll make this endpoint return a list
                if (response.isSuccessful) {
                    response.body()?.string()?.let { json ->
                        val jsonArray = JSONArray(json)
                        val releaseList = mutableListOf<ReleaseResponse>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val assetsJson = obj.getJSONArray("assets")
                            val assets = mutableListOf<Asset>()
                            for (j in 0 until assetsJson.length()) {
                                val assetObj = assetsJson.getJSONObject(j)
                                assets.add(Asset(assetObj.getString("browser_download_url")))
                            }
                            releaseList.add(
                                ReleaseResponse(
                                    tag_name = obj.getString("tag_name"),
                                    published_at = obj.getString("published_at"),
                                    assets = assets
                                )
                            )
                        }

                        // Sort newest first by date
                        _releases.value = releaseList.sortedByDescending {
                            Instant.parse(it.published_at) // needs java.time
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ManageUpdates", "Error fetching releases", e)
            }
        }
    }

    fun downloadUpdate(url: String){
        viewModelScope.launch {
            downloadUpdateUseCase(url).collect{result ->
                when (result)
                {
                    is WorkerReleaseResponse.Error -> {
                        Toast.makeText(context, "Error ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is WorkerReleaseResponse.Loading -> {
                        Toast.makeText(context, "${result.message}", Toast.LENGTH_SHORT).show()

                    }
                    is WorkerReleaseResponse.Success -> {
                        Toast.makeText(context, "Success ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
    fun saveProductKey (key: String){
       viewModelScope.launch {
           productKeyRepo.getProductKeys().let { list->
               if(list.isEmpty()){
                   val pr = ProductKey(keyValue = key)
                   productKeyRepo.createProductKey(pr)
                   _isProductKeyvalid.postValue(true)
                   Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show()

               }
           }
       }

    }

     fun checkProductKey() {
        viewModelScope.launch {
            productKeyRepo.getProductKeys().let{list->
                if(list.isEmpty())
                {
                    Toast.makeText(context,
                        "product key invalid", Toast.LENGTH_SHORT).show()
                    _isProductKeyvalid.postValue(false)
                }else{
                   _isProductKeyvalid.postValue(list[0].keyValue == PRODUCT_KEY)
                }
            }

        }
    }
}