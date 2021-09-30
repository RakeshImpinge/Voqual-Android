package com.westernsydneyunipx.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public final class NetworkUtils {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_4G = 2;
    public static final int TYPE_3G = 3;
    public static final int TYPE_2G = 4;

    private NetworkUtils() {
        // This utility class is not publicly instantiable
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * To get device consuming network type like wifi, 4g, 3g, 2g
     *
     * @param context
     * @return one of network type {@link NetworkUtils#TYPE_WIFI}, {@link NetworkUtils#TYPE_4G},
     * {@link NetworkUtils#TYPE_3G} or {@link NetworkUtils#TYPE_2G}
     */
    public static int getNetworkType(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return 0;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return TYPE_WIFI;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = mTelephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_LTE:
                    //No specification for the 4g but from wiki
                    //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                    //https://goo.gl/9t7yrR
                    return TYPE_4G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    /**
                     From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                     EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                     Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards
                     for sending voice, data, and signaling data between mobile phones and cell sites.
                     */
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                    //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                    //Some cases are added after  testing(real) in device with 3g enable data
                    //and speed also matters to decide 3g network type
                    //http://goo.gl/bhtVT
                    return TYPE_3G;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return TYPE_2G;
                default:
                    return 0;
            }
        }
        return 0;
    }
}
