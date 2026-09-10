/*
 * Copyright 2026 The Android Open Source Project
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

package com.android.internal.telephony;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataProfile;

import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RILUtilsTest {
    private DataProfile buildProfile(int apnTypes, int networkTypes) {
        ApnSetting apn = new ApnSetting.Builder()
                .setEntryName("Test APN")
                .setApnName("ims")
                .setApnTypeBitmask(apnTypes)
                .setNetworkTypeBitmask(networkTypes)
                .setCarrierEnabled(true)
                .build();
        assertNotNull(apn);
        return new DataProfile.Builder().setApnSetting(apn).build();
    }

    @Test
    public void testStockImsUnrestrictedBearerIncludesIwlan() {
        for (int type : new int[] {ApnSetting.TYPE_IMS, ApnSetting.TYPE_EMERGENCY}) {
            DataProfile profile = buildProfile(type, 0);
            // Stock's all-RAT bearer mask, shifted into the HAL RadioAccessFamily format.
            assertEquals(0x1ffffe,
                    RILUtils.convertToHalDataProfileBearerBitmap(profile, true));
            assertEquals(0, profile.getBearerBitmask());
        }
    }

    @Test
    public void testTrebleAppUnrestrictedBearerIsUnchanged() {
        for (int type : new int[] {ApnSetting.TYPE_IMS, ApnSetting.TYPE_EMERGENCY}) {
            assertEquals(0, RILUtils.convertToHalDataProfileBearerBitmap(
                    buildProfile(type, 0), false));
        }
    }

    @Test
    public void testNonImsUnrestrictedBearerIsUnchanged() {
        assertEquals(0, RILUtils.convertToHalDataProfileBearerBitmap(
                buildProfile(ApnSetting.TYPE_DEFAULT, 0), true));
    }

    @Test
    public void testExplicitCarrierBearerRestrictionIsUnchanged() {
        int networkTypes = (int) TelephonyManager.NETWORK_TYPE_BITMASK_LTE;
        DataProfile profile = buildProfile(ApnSetting.TYPE_IMS, networkTypes);
        int expected = ServiceState.convertNetworkTypeBitmaskToBearerBitmask(networkTypes) << 1;
        assertEquals(expected, RILUtils.convertToHalDataProfileBearerBitmap(profile, true));
        assertEquals(expected, RILUtils.convertToHalDataProfileBearerBitmap(profile, false));
        assertEquals(networkTypes, profile.getBearerBitmask());
    }
}
