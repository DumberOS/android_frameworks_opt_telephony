/*
 * Copyright (C) 2023 PixelBuildsROM
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

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import com.android.internal.telephony.subscription.SubscriptionManagerService;

public class SubscriptionController {
    private static final String LOG_TAG = "SubscriptionController";
    protected static SubscriptionController sInstance = null;

    /** Android 13 binary compatibility holder used by vendor PhoneSwitcher subclasses. */
    public static class WatchedInt {
        private int mValue;

        public WatchedInt(int initialValue) {
            mValue = initialValue;
        }

        public int get() {
            return mValue;
        }

        public void set(int newValue) {
            mValue = newValue;
        }
    }

    public static SubscriptionController getInstance() {
        // Lazy init happens once, whenever getInstance() is invoked for the first time
        if (sInstance == null) {
            synchronized (SubscriptionController.class) {
                if (sInstance == null) {
                    Log.v(LOG_TAG, "getInstance() was invoked for the first time, "
                            + "initializing the stub SubscriptionController");
                    sInstance = new SubscriptionController();
                }
            }
        }
        return sInstance;
    }

    /** Android 13 compatibility API used after the active data subscription changes. */
    public static void invalidateActiveDataSubIdCaches() {
        SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
    }

    /**
     * @return The subscription manager service instance.
     */
    public SubscriptionManagerService getSubscriptionManagerService() {
        return SubscriptionManagerService.getInstance();
    }

    public int getSubIdUsingPhoneId(int phoneId) {
        SubscriptionManagerService subscriptionManagerService = getSubscriptionManagerService();
        int subId = subscriptionManagerService.getSubId(phoneId);
        Integer subIdObj = subId;
        if (subIdObj == null) {
            return SubscriptionManager.INVALID_SUBSCRIPTION_ID;
        }
        return subId;
    }

    /** Android 13 compatibility API used by stock MediaTek telephony components. */
    public int getPhoneId(int subId) {
        return getSubscriptionManagerService().getPhoneId(subId);
    }

    /** Android 13 compatibility API used by stock MediaTek telephony components. */
    public int getDefaultDataSubId() {
        return getSubscriptionManagerService().getDefaultDataSubId();
    }

    /** Android 13 compatibility API used by stock MediaTek telephony components. */
    public int getSlotIndex(int subId) {
        return getSubscriptionManagerService().getSlotIndex(subId);
    }

    /** Android 13 compatibility API used by MTK SIM and phonebook initialization. */
    public int getSimStateForSlotIndex(int slotIndex) {
        IccCardConstants.State state = IccCardConstants.State.UNKNOWN;
        if (slotIndex < 0) {
            return state.ordinal();
        }

        Phone phone = null;
        try {
            phone = PhoneFactory.getPhone(slotIndex);
        } catch (IllegalStateException ignored) {
            // PhoneFactory may not be ready while the UICC controller is starting.
        }
        if (phone != null) {
            IccCard iccCard = phone.getIccCard();
            if (iccCard != null) {
                state = iccCard.getState();
            }
        }
        return state.ordinal();
    }

    /** Android 13 compatibility API used when MTK restores UICC application state. */
    public SubscriptionInfo getSubInfoForIccId(String iccId) {
        return getSubscriptionManagerService().getSubscriptionInfoForIccId(iccId);
    }
}
