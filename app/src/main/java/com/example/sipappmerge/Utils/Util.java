package com.example.sipappmerge.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    public static String[] url_name = {"E", "S", "L"};
    public static String app_version = "1.1.0";
    public static int url_type = 1;
    public static String app_version_name = "V " + app_version + " " + url_name[url_type];
    public static boolean recording = true;

    /*public static String BASE =  "https://c2c.armsoft-tech.com/CRMPREDICTIVE/Dialer/";//Predictive
    public static String BASE =  "https://c2c.armsoft-tech.com/CRMMOBILEAPI/Dialer/";//VAPT
    public static String LOGINNew =  BASE+"MobileAPPLogin";*/

    /*public static String BASE =  "https://c2c.armsoft-tech.com/CentralisedMobileAPI/Dialer/";*/
    public static String BASE =  "https://c2c.armsoft-tech.com/MOBILEAPI6/";
    public static String LOGINNew =  BASE+"APPlogin";
    public static String MobileAPPLogin =   BASE+"Dialer/MobileAPPLogin";
    public static String GetDashboard =   BASE+"Dialer/GetDashboard";
    public static String GetBreak =   BASE+"Dialer/GetBreak";
    public static String ApplyBreak =   BASE+"Dialer/ApplyBreak";
    public static String Ready =  BASE+"Dialer/Ready";
    public static String LOGOUTUSER =   BASE+"Dialer/LOGOUTUSER";
    public static String GetData =  BASE+"Dialer/GetData";
    public static String GetCustomerData =   BASE+"Dialer/GetCustomerData";
    public static String GetCustomerData_v1 =   BASE+"Dialer/GetCustomerData_v1";
    public static String GetDispo =   BASE+"Dialer/GetDispo";
    public static String callapi =   BASE+"Dialer/callapi";
    public static String Callcloseapi =   BASE+"Dialer/Callcloseapi";
    public static String GetCallclose_v1 =   BASE+"Dialer/Callcloseapi_v1";
    public static String GetAlternateNo =   BASE+"Dialer/GetAlternateNo";
    public static String getcallhistory =   BASE+"Dialer/getcallhistory";
    public static String UpdatePassword =   BASE+"Dialer/UpdatePassword";
    public static String Authenticate =   BASE+"Dialer/Authenticate";
    public static String FetchPROGRESSIVECALLS =   BASE+"Dialer/FetchPROGRESSIVECALLS";
    public static String PROGRESSIVECALLS =   BASE+"Dialer/PROGRESSIVECALLS";
    public static String AppErrorLog =   BASE+"Dialer/AppErrorLog";
    public static String FetchCalls =   BASE+"Dialer/FetchCalls";
    public static String HangupCalls =   BASE+"Dialer/HangupCalls";
    public static String AppNetworkStrength =   BASE+"Dialer/AppNetworkStrength";



   /*public static String GetDashboard =  BASE+"GetDashboard";
    public static String GetBreak =  BASE+"GetBreak";
    public static String ApplyBreak =  BASE+"ApplyBreak";
    public static String Ready =  BASE+"Ready";
    public static String LOGOUTUSER =  BASE+"LOGOUTUSER";
    public static String GetData =  BASE+"GetData";
    public static String GetCustomerData =  BASE+"GetCustomerData";
    public static String GetDispo =  BASE+"GetDispo";
    public static String callapi =  BASE+"callapi";
    public static String Callcloseapi =  BASE+"Callcloseapi";
    public static String GetAlternateNo =  BASE+"GetAlternateNo";
    public static String getcallhistory =  BASE+"getcallhistory";
    public static String UpdatePassword =  BASE+"UpdatePassword";
    public static String Authenticate =  BASE+"Authenticate";
    public static String FetchPROGRESSIVECALLS =  BASE+"FetchPROGRESSIVECALLS";
    public static String PROGRESSIVECALLS =  BASE+"PROGRESSIVECALLS";*/

    private SharedPreferences preferences;

    public static String getstatus;
    public static boolean dodont=true;
    public Util(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public synchronized static boolean isFirstLaunch(Context context) {
        boolean launchFlag = false;
        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("install", false);
        return launchFlag;
    }

    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }

        /**
         * Checks to see if the device is online before carrying out any operations.
         *
         * @return
         */

        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    public static void hideKeypad(Context context, View view) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void saveData(String key, String value, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("KODAK", Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KODAK", Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void clearSession(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("KODAK", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static String getDeviceId(Context ctx) {
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static boolean isOnline(Context con) {

        ConnectivityManager cm = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.i("netInfo", "" + netInfo);
            return true;
        }
        return false;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getdatetime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime.replace("-", "/");
    }
    public static String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("h:mm a");
        String datetime = dateformat.format(c.getTime());
        return datetime.replace("-", "/");
    }


    public static class Logcat {
        private static final String TAG = "AIRCRM";
        private static final boolean DEBUG_MODE = false;

        public static void e(String msg) {
            if (url_type == 1) {
Log.e(TAG,msg);
            }
        }
    }
    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;

                    break;
                }
            }
        }
        return found;
    }
    public static boolean isRooted() {
        return findBinary("su");
    }
    public static boolean executeShellCommand() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) { }
            }
        }
    }


    public static String getSignalStrength(Context context) throws SecurityException {
        try {
            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String strength = null;
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
            if (cellInfos != null) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i).isRegistered()) {
                        if (cellInfos.get(i) instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthWcdma.getDbm()) + " WCDMA";
                        } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                            CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthGsm.getDbm()) + " Gsm";
                        } else if (cellInfos.get(i) instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthLte.getDbm()) + " 4G";
                        } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthCdma.getDbm()) + " CDMA";
                        }
                    }
                }
            }
            return strength;
        }catch (Exception e)
        {
            return "";
        }
    }

    public static int  getDefaultSim(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        /*try {
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return smsDefaultSim;
    }
    public static String getMacAddr() {
        try {
            List <NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

    public static List<ApplicationInfo> getAppList(PackageManager pm)
    {

//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d("TAG", "Installed package :" + packageInfo.packageName);
            Log.d("TAG", "Source dir : " + packageInfo.sourceDir);
            Log.d("TAG", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }
        return packages;
    }


    public static List<String> getAppNameList(PackageManager pm)
    {


        List<String> packagesNames = new ArrayList<String>();
//get a list of installed apps.
        try {
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo packageInfo : packages) {
                packagesNames.add(packageInfo.packageName);
                Log.d("TAG", "Installed package :" + packageInfo.packageName);
                Log.d("TAG", "Source dir : " + packageInfo.sourceDir);
                Log.d("TAG", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            }
        }catch (Exception e){

        }
        return packagesNames;
}

    public static String getAppNameFromPkgName(Context context, String Packagename) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(Packagename, PackageManager.GET_META_DATA);
            String appName = (String) packageManager.getApplicationLabel(info);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
