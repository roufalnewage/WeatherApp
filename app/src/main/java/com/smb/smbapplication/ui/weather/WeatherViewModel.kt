package com.smb.smbapplication.ui.weather
/**
 * Created by Shijil Kadambath on 03/08/2018
 * for NewAgeSMB
 * Email : shijil@newagesmb.com
 */
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.smb.smbapplication.common.AbsentLiveData
import com.smb.smbapplication.data.api.ApiResponse
import com.smb.smbapplication.data.api.BaseResponse
import com.smb.smbapplication.data.api.Resource
import com.smb.smbapplication.repo.WeatherRepository
import retrofit2.Response
import javax.inject.Inject

class WeatherViewModel
@Inject constructor( repoRepository: WeatherRepository) : ViewModel() {

    private val _login = MutableLiveData<String>()
    private val _login1 = MutableLiveData<String>()
    private val _alert1 = MutableLiveData<String>()
    private val _forcast1 = MutableLiveData<String>()

    val forcast1: LiveData<String>
        get() = _forcast1
  val alert1: LiveData<String>
        get() = _alert1


    val login1: LiveData<String>
        get() = _login1

    val repositories: LiveData<ApiResponse<JsonObject>> = Transformations

            .switchMap(_login) { login ->
                if (login == null) {
                    AbsentLiveData.create()
                } else {
                    repoRepository.loadCurrentWeather()
                }
            }


    val repositories1: LiveData<ApiResponse<JsonObject>> = Transformations

            .switchMap(_login1) { login ->
                if (login == null) {
                    AbsentLiveData.create()
                } else {
                    repoRepository.loadCurrentWeather()
                }
            }
 val repositoriesforecast: LiveData<ApiResponse<JsonObject>> = Transformations

            .switchMap(_forcast1) { forcast1 ->
                if (forcast1 == null) {
                    AbsentLiveData.create()
                } else {
                    repoRepository.loadWeatherForecast()
                }
            }
 val repositoriesalert: LiveData<ApiResponse<JsonObject>> = Transformations

            .switchMap(_alert1) { alert1 ->
                if (alert1 == null) {
                    AbsentLiveData.create()
                } else {
                    repoRepository.loadWeatherAlert()
                }
            }


    fun retry() {
        _login.value?.let {
            _login.value = it
        }
    }

    fun loadData() {
            _login.value = "test"
    }

    fun loadData1() {
            _login1.value = "test"
    }
    fun loadAlert() {
        _alert1.value = "test"
    }
    fun loadForcast() {
        _forcast1.value = "test"
    }
}