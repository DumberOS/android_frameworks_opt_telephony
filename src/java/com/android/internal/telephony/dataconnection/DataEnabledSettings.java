/*
 * Copyright (C) 2026 The Android Open Source Project
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

package com.android.internal.telephony.dataconnection;

import android.os.Handler;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.DataSettingsManager;

/**
 * Android 13 API adapter retained for binary vendor telephony extensions.
 *
 * <p>Android 14 always uses {@link DataSettingsManager}. This class exposes the small part of the
 * removed DataEnabledSettings ABI referenced by the stock MTK telephony implementation.</p>
 */
public final class DataEnabledSettings {
    private final Phone mPhone;

    public DataEnabledSettings(Phone phone) {
        mPhone = phone;
    }

    public boolean isDataEnabled() {
        DataSettingsManager manager = mPhone.getDataSettingsManager();
        return manager != null && manager.isDataEnabled();
    }

    public boolean isDataEnabled(int apnType) {
        DataSettingsManager manager = mPhone.getDataSettingsManager();
        return manager != null && manager.isDataEnabled(apnType);
    }

    public boolean isDataAllowedInVoiceCall() {
        DataSettingsManager manager = mPhone.getDataSettingsManager();
        return manager != null && manager.isMobileDataPolicyEnabled(
                TelephonyManager.MOBILE_DATA_POLICY_DATA_ON_NON_DEFAULT_DURING_VOICE_CALL);
    }

    public void setInternalDataEnabled(boolean enabled) {
        // Android 14 handles ECM data state through DataSettingsManager's override policies.
    }

    public void unregisterForDataEnabledChanged(Handler handler) {
        // Vendor code reaches this only when isUsingNewDataStack() is false.
    }
}
