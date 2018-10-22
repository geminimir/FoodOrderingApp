package app.taxipizza.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.models.User;


public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    Settings settings = new Settings();
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("");

        TextView txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Paramètres");
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.home_frame, settings)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        getActivity().getFragmentManager().beginTransaction()
                .remove(settings)
                .commit();
        } catch (Exception e) {}
    }

    public static class Settings extends PreferenceFragment {
        public Settings() {}

        FirebaseDatabase database;
        DatabaseReference Users;

        CheckBoxPreference notifications;
        PreferenceScreen contact, rate;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            notifications = (CheckBoxPreference)findPreference("notifications");
            contact = (PreferenceScreen)findPreference("contact");
            rate = (PreferenceScreen)findPreference("rate");
            database = FirebaseDatabase.getInstance();
            Users = database.getReference("Users");
            final User updateUser = Utils.getCurrentUser(getActivity());

            notifications.setChecked(updateUser.isNotificationsEnabled());
            notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateUser.setNotificationsEnabled((boolean) newValue);
                    Utils.setCurrentUser(getActivity(), updateUser);
                    Users.child(updateUser.getPhone()).setValue(updateUser);
                    notifications.setChecked((boolean) newValue);
                    return false;
                }
            });

            rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String url = "https://play.google.com/store/apps/details?id=app.ReaderApp";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(i, ""));
                    return false;
                }
            });

            contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("mailto:" + "Khalilmerchaoui@gmail.com"));
                    startActivity(sendIntent);

                    return false;
                }
            });
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
