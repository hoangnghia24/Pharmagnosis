package hcmute.edu.vn.pharmagnosis.views.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Locale;

import hcmute.edu.vn.pharmagnosis.R;
import hcmute.edu.vn.pharmagnosis.viewmodels.ProfileViewModel;

import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import hcmute.edu.vn.pharmagnosis.views.activities.LoginActivity;

public class UserProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.fetchUser();

        profileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                TextView tvName = view.findViewById(R.id.tv_name);
                TextView tvAgeGender = view.findViewById(R.id.tv_age_gender);
                TextView tvBlood = view.findViewById(R.id.tv_blood);
                TextView tvAllergy = view.findViewById(R.id.tv_allergy);
                ImageView imgAvatarDisplay = view.findViewById(R.id.img_avatar_display);

                TextView tvProfileHeight = view.findViewById(R.id.tv_profile_height);
                TextView tvProfileWeight = view.findViewById(R.id.tv_profile_weight);
                TextView tvBmiScore = view.findViewById(R.id.tv_bmi_score);
                TextView tvBmiStatus = view.findViewById(R.id.tv_bmi_status);
                ImageView icPointer = view.findViewById(R.id.ic_pointer);

                if (tvName != null && user.getFullNAme() != null) tvName.setText(user.getFullNAme());
                if (tvBlood != null && user.getBloodType() != null) tvBlood.setText("Nhóm máu: " + user.getBloodType());

                if (tvAgeGender != null) {
                    String genderStr = (user.getGender() != null && user.getGender().name().equals("MALE")) ? "Nam" : "Nữ";
                    String ageStr = "Chưa rõ";

                    if (user.getDob() != null) {
                        Calendar dob = Calendar.getInstance();
                        dob.setTime(user.getDob());
                        Calendar today = Calendar.getInstance();

                        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                            age--;
                        }
                        ageStr = age + " tuổi";
                    }
                    tvAgeGender.setText(genderStr + " • " + ageStr);
                }

                if (tvAllergy != null) {
                    if (user.getAllergies() != null && !user.getAllergies().isEmpty()) {
                        StringBuilder allergyText = new StringBuilder("Dị ứng: ");
                        for (int i = 0; i < user.getAllergies().size(); i++) {
                            allergyText.append(user.getAllergies().get(i).getAllergenName());
                            if (i < user.getAllergies().size() - 1) allergyText.append(", ");
                        }
                        tvAllergy.setText(allergyText.toString());
                    } else {
                        tvAllergy.setText("Dị ứng: Không có");
                    }
                }

                if (imgAvatarDisplay != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(user.getAvatar(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgAvatarDisplay.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (tvProfileHeight != null && user.getHeight() > 0) tvProfileHeight.setText(String.format(Locale.getDefault(), "%.0f", user.getHeight()));
                if (tvProfileWeight != null && user.getWeight() > 0) tvProfileWeight.setText(String.format(Locale.getDefault(), "%.1f", user.getWeight()));

                if (tvBmiScore != null && tvBmiStatus != null && icPointer != null && user.getBmi() > 0) {
                    float bmi = user.getBmi();
                    tvBmiScore.setText(String.format(Locale.getDefault(), "%.1f", bmi));

                    String status; int color; float bias;
                    if (bmi < 18.5) {
                        status = "Gầy"; color = 0xFF38BDF8; bias = 0.12f;
                    } else if (bmi < 25) {
                        status = "Bình thường"; color = 0xFF22C55E; bias = 0.38f;
                    } else if (bmi < 30) {
                        status = "Thừa cân"; color = 0xFFFACC15; bias = 0.63f;
                    } else {
                        status = "Béo phì"; color = 0xFFEF4444; bias = 0.88f;
                    }

                    tvBmiStatus.setText(status);
                    tvBmiStatus.setTextColor(color);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) icPointer.getLayoutParams();
                    params.horizontalBias = bias;
                    icPointer.setLayoutParams(params);
                }
            }

            View btnLogout = view.findViewById(R.id.btn_logout);
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }
        });

        View cvBmi = view.findViewById(R.id.cv_bmi);
        if (cvBmi != null) {
            cvBmi.setOnClickListener(v -> {
                BmiCalculatorFragment bmiFragment = new BmiCalculatorFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, bmiFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        View cvMedicalDetails = view.findViewById(R.id.cv_medical_details);
        if (cvMedicalDetails != null) {
            cvMedicalDetails.setOnClickListener(v -> {
                MedicalProfileFragment medicalFragment = new MedicalProfileFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, medicalFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }
    }
}