package com.lost_n_found.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lost_n_found.R;
import com.lost_n_found.home.home;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String regex = "^(.+)@(.+)$";

    private EditText email;
    private EditText password;
    private TextView forget;
    private Button login;

    private FirebaseAuth mAuth;
// ...


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ConnectivityManager connectivityManager;

    public login_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment login_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static login_fragment newInstance(String param1, String param2) {
        login_fragment fragment = new login_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

        // Initialize Firebase Auth


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_fragment, container, false);

        EditText email = root.findViewById(R.id.email);
        EditText password = root.findViewById(R.id.pass);
        TextView forget = root.findViewById(R.id.forget_pass);
        Button login = root.findViewById(R.id.lg_btn);

        email.setTranslationX(300);
        password.setTranslationX(300);
        forget.setTranslationX(300);
        login.setTranslationX(300);

        email.setAlpha(0);
        password.setAlpha(0);
        forget.setAlpha(0);
        login.setAlpha(0);

        email.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(400).start();
        password.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(600).start();
        forget.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(800).start();
        login.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(800).start();


        //login authentication
        mAuth = FirebaseAuth.getInstance();


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())) {
                    email.setError("Enter Your registered email ");
                } else {
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Password Reset email sent ", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Email not registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_user = email.getText().toString().trim();
                String pass_user = password.getText().toString();


                try {

                    FirebaseUser user = mAuth.getCurrentUser();
                    //checking network connection
                    boolean connected = false;
                    try {

                        user = mAuth.getCurrentUser();
                        connectivityManager = (ConnectivityManager)
                                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        connected = networkInfo != null && networkInfo.isAvailable() &&
                                networkInfo.isConnected();
                        if (!connected) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setIcon(R.drawable.no_net).setTitle("NO INTERNET").setMessage("turn on mobile data or wifi")
                                    .setPositiveButton("turn on", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                            startActivity(i);

                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                            startActivity(i);
                                        }
                                    }).show();
                        }


                    } catch (Exception e) {
                        // System.out.println("CheckConnectivity Exception: " + e.getMessage());
                        Log.v("connectivity", e.toString());
                    }

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(email_user);

                    if (email_user.isEmpty()) {
                        email.setError("This field can't be empty!");
                    }
                    if (!connected) {
                        Toast.makeText(getContext(), "Turn on mobile data", Toast.LENGTH_SHORT).show();
                    } else if (pass_user.isEmpty()) {
                        password.setError("This field can't be empty!");
                    } else if ((!matcher.matches() || !email_user.endsWith(".com")) && !email_user.isEmpty()) {
                        Toast.makeText(getContext(), "Enter valid Email!", Toast.LENGTH_SHORT).show();
                    } else {
                        loginFun(email_user, pass_user);
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Something went wrong!" + e, Toast.LENGTH_SHORT).show();

                }


            }
        });


        //TODO call after succesful login
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent= new Intent(getActivity(), home.class);
//                startActivity(intent);

//
//            }
//        });

        return root;


    }


    private void loginFun(String email_user, String pass_user) {


        mAuth.signInWithEmailAndPassword(email_user, pass_user)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        try {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginSuccessed", "signInWithEmail:success");
                                assert user != null;
                                if (!user.isEmailVerified()) {
                                    Toast.makeText(getContext(), "verify your email", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(getActivity(), home.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    requireActivity().finish();
                                    // getActivity(Splashscreen)


                                    startActivity(intent);
                                    Toast.makeText(getContext(), "Welcome Back!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginDeclined", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Invalid Email or Password!",
                                        Toast.LENGTH_SHORT).show();
                                //  updateUI(null);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}