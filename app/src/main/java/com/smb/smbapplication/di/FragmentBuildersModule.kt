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

package com.smb.smbapplication.di


import com.smb.smbapplication.ui.login.LoginFragment
import com.smb.smbapplication.ui.login.RegistrationStepOneFragment
import com.smb.smbapplication.ui.login.RegistrationStepTwoFragment
import com.smb.smbapplication.ui.weather.WeatherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
/**
 * Created by Shijil Kadambath on 03/08/2018
 * for NewAgeSMB
 * Email : shijil@newagesmb.com
 */

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeWeatherFragment(): WeatherFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationStepOneFragment(): RegistrationStepOneFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationStepTwoFragment(): RegistrationStepTwoFragment
}
