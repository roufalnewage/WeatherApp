/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smb.smbapplication.repo

import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.smb.smbapplication.AppExecutors
import com.smb.smbapplication.data.api.ApiResponse
import com.smb.smbapplication.data.api.WebService
import com.smb.smbapplication.data.db.AppDb
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Shijil Kadambath on 03/08/2018
 * for NewAgeSMB
 * Email : shijil@newagesmb.com
 */

/**
 * Repository that handles User instances.
 *
 */
@Singleton
class WeatherRepository @Inject constructor(
        private val webService: WebService
) {
    //ApiResponse<BaseResponse<List<User>>>

    fun loadCurrentWeather(): LiveData<ApiResponse<JsonObject>> {
        return webService.loadCurrentWeather()
    }


    fun loadWeatherForecast(): LiveData<ApiResponse<JsonObject>> {
        return webService.loadWeatherForecast()
    }


    fun loadWeatherAlert(): LiveData<ApiResponse<JsonObject>> {
        return webService.loadWeatherAlert()
    }




}
