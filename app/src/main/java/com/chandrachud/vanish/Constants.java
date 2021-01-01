package com.chandrachud.vanish;

public class Constants {

    public static final String admobAppId = "ca-app-pub-1286755872665192~1742695696";
    public static final String testRewardedAdId = "ca-app-pub-3940256099942544/5224354917";

    public static final int[] profilePics = {

            R.drawable.random_1_large,
            R.drawable.male_1_large,
            R.drawable.male_2_large,
            R.drawable.female_1_large,
            R.drawable.female_2,
            R.drawable.male_3_large,
            R.drawable.male_4_large,
            R.drawable.female_3_large,
            R.drawable.female_4_large,
            R.drawable.male_5_large,
            R.drawable.male_6_large,
            R.drawable.male_7_large,
            R.drawable.female_5_large,
            R.drawable.random_2_large,
            R.drawable.male_9_large,
            R.drawable.male_8_large

    } ;

    public static final int[] profilePics_medium = {

            R.drawable.random_medium_1,
            R.drawable.male_1_medium,
            R.drawable.male_2_medium,
            R.drawable.female_1_medium,
            R.drawable.female_2_medium,
            R.drawable.male_3_medium,
            R.drawable.male_4_medium,
            R.drawable.female_3_medium,
            R.drawable.female_4_medium,
            R.drawable.male_5_medium,
            R.drawable.male_6_medium,
            R.drawable.male_7_medium,
            R.drawable.female_5_medium,
            R.drawable.random_medium_2,
            R.drawable.male_9_medium,
            R.drawable.male_8_medium

    } ;



    public static final String tag = "tag";
    public static final String webClientId = "51952074150-qdc9qp1ba0m8h7ij75kcfsadjv9dv3ac.apps.googleusercontent.com";


    public static final String users_fire = "Users";

    public static final String signup_Activity = "SignupActivity";
    public static final String login_Activity = "LoginActivity";

    public static final String phone_Intent = "Phone Number";
    public static final String ccd_Intent = "Country Code";
    public static final String name_Intent = "Full Name";
    public static final String profile_num_Intent = "Profile Pic Num";
    public static final String email_Intent = "Email";
    public static final String activity_Intent = "ActivityName";

    public static final String nameSignup = "NameProfileSignup";

    public static final String auth_intent = "AuthenticationIntent";

    public static final String onlyNum_intent = "OnlyNum";
    public static final String onlyCcc_intent = "OnlyCCC";

    public static final String premium_intent = "PremiumIntent";

    //FirebaseFirestoreConstants

    public static final String name_fire = "Full Name";
    public static final String premium_fire = "Premium";
    public static final String phoneNum_fire = "Phone Number";
    public static final String uid_fire = "Unique Id";
    public static final String email_fire = "Email Address";
    public static final String onlyPhoneNum_fire = "Mobile";
    public static final String onlyCCode_fire = "Country Code";
    public static final String deletedNumList_fire = "Numbers you deleted - List";
    public static final String deletedNumCountWeek_fire = "Numbers you Deleted - Week";
    public static final String deletedNumCountTotal_fire  = "Numbers you Deleted - Total";
    public static final String blockedDeletionsTotal_fire = "Blocked Deletions - Total";
    public static final String blockedDeletionsWeek_fire = "Blocked Deletions - Week";
    public static final String deleteAttemptsTotal_fire = "Others Delete Attempts - Total";
    public static final String deleteAttemptsWeek_fire = "Others Delete Attempts - Week";
    public static final String othersDeletedCountWeek_fire = "Numbers Others Deleted - Week";
    public static final String othersDeletedCountTotal_fire = "Numbers Others Deleted - Total";

    public static final String notifyCollection_fire = "Notifications";
    public static final String pendingDeletes_fire = "Pending Deletes from User";
    public static final String isPending_fire = "Pending deletions Check";
    public static final String isNotify_fire = "Notifications Check";
    public static final String isReadd_fire = "Re-add Numbers Check";

    public static final String readdNumbersList_fire = "Re-add Numbers List";
    public static final String numReadded_fire = "Re-added Numbers Count";

    public static final String profilePicNum_fire = "Profile Picture Number";

    public static final String backWord_rFire = "Background Word";

    public static final String phone_realtime = "phone";
    public static final String uid_realtime = "uid";
    public static final String notifications_realtime = "Notifications";
    public static final String isNotify_realtime = "notify";
    public static final String isDelete_realtime = "isDelete";
    public static final String isReadd_realtime = "isReadd";
    public static final String numItem_realtime = "mNumberItemFirebases";
    public static final String readdNumbers_realtime = "readdNumbersList";
    public static final String isPremium_realtime = "isPremium";

    public static final String normal_delete_dialog = "NORMAL_DIALOG";
    public static final String premium_user_dialog = "PREMIUM_USER_DIALOG";
    public static final String not_found_delete_dialog = "NOT_FOUND_DIALOG";
    public static final String countryCode_delete_dialog = "COUNTRY_CODE_DIALOG";
    public static final String delete_continue_dialog = "CONTINUE_DELETE_DIALOG";
    public static final String delete_request_finished_dialog = "REQUEST_SENT_DIALOG";

    public static final String deleted_success = "DELETED";
    public static final String blocked_success = "BLOCKED";
    public static final String num_not_found = "NOT_FOUND";
    public static final String deleted_message = "Your Number was successfully deleted";
    public static final String blocked_message = "A deletion on your device was blocked";
    public static final String not_found_message = "Number not found on other Device";

    public static final String timestamp_fire = "timestamp";

    public static final String toDeleteNumList = "Numbers to Delete";
    public static final String number = "number";

    public static final String workManager_ID = "Vanish_WorkManager_ID";

    public static final String workManager_UUID = "Vanish_WorkManger_UUID";


    public static final String shared_prefs = "Vanish_SharedPreferences_PRIVATE";

    public static final String premium_numbers_prefs = "Recent_Premium_Numbers_PREFS";

    public static final String name_prefs = "Name_PREFS";
    public static final String phone_prefs = "Phone_PREFS";
    public static final String ccc_prefs = "Ccc_PREFS";
    public static final String profilePicNum_prefs = "ProfilePicNum_PREFS";
    public static final String premium_prefs = "Premium_PREFS";
    public static final String email_prefs = "Email_PREFS";
    public static final String notify_settings_pref = "NotifySettings_PREFS";
    public static final String recent_deletions_pref = "RecentDeletions_PREFS";



}
