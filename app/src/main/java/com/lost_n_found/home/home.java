package com.lost_n_found.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lost_n_found.R;
import com.lost_n_found.databinding.ActivityHomeBinding;
import com.lost_n_found.home.chatMessages.placeholder.PlaceholderChatContent;
import com.lost_n_found.home.placeholder.PlaceholderContent;
import com.lost_n_found.home.placeholder.PlaceholderFoundContent;
import com.lost_n_found.home.placeholder.PlaceholderLostContent;
import com.lost_n_found.login.CreateUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class home extends AppCompatActivity {
    ActivityHomeBinding binding;
    public static boolean notification =false;
    public  boolean allowNotification =false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FragmentTransaction Profile = getSupportFragmentManager().beginTransaction();
        Profile.replace(R.id.fragment_container_view_tag, new ProfielFragment());
        Profile.commit();
        binding.bottomBar.setItemActiveIndex(3);


        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String uid = firebaseUser.getUid();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DatabaseReference databaseReferenceUser = firebaseDatabase.getReference("user");
            DatabaseReference databaseReferencePosts = firebaseDatabase.getReference("posts/" + uid);
            DatabaseReference databaseReferenceChats = firebaseDatabase.getReference("chats/");
            DatabaseReference databaseReference = firebaseDatabase.getReference("posts");

            databaseReferenceChats.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PlaceholderChatContent.ITEMS.clear();
                    if(allowNotification){
                        notification = true;
                    }
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        CreateUser User = snap.getValue(CreateUser.class);

                        String sent_recieve_room = snap.getKey().toString();

                        if (firebaseUser.getUid().toString().equals(sent_recieve_room.substring(0,uid.length())) ) {
                            String receiverUid = snap.getKey().toString().substring(uid.length(),sent_recieve_room.length());

                            DatabaseReference receiverUserData = firebaseDatabase.getReference("user").child(receiverUid);

                            receiverUserData.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    CreateUser user = snapshot.getValue(CreateUser.class);
                                    PlaceholderChatContent.PlaceholderItem obj = new PlaceholderChatContent.PlaceholderItem(user.getUid(), user.getUsername(),user.getAvatar());
                                    PlaceholderChatContent.ITEMS.add(obj);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }

                    allowNotification = true;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PlaceholderFoundContent.ITEMS.clear();
                    PlaceholderLostContent.ITEMS.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        //Log.v("-11>",snapshot1.getChildrenCount()+"");
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            CreatePost post = snapshot2.getValue(CreatePost.class);
                            String title = post.getTitle().toString();
                            String date = post.getDate().toString();
                            String descp = post.getDescription();
                            String status = post.getStatus().toString();
                            String location = post.getLocation().toString();
                            String userid = post.getUid().toString();
                            String imageUrl = post.getPostImgUrl().toString();
                            String contact = post.getContact().toString();
                            String avatar = post.getAvatar().toString();
                            databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username  = snapshot.child(userid).child("username").getValue()+"";
                                    Date date1 = null;
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                                    try {
                                        date1 = format.parse(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (status.equals("lost")) {

                                        PlaceholderLostContent.PlaceholderItem obj = new PlaceholderLostContent.PlaceholderItem(userid + "", title + "", status + "", descp + "", date1 , location + "", username + "", imageUrl + "", contact, avatar, date+"");
                                        PlaceholderLostContent.ITEMS.add( obj);


                                    } else {
                                        PlaceholderFoundContent.PlaceholderItem obj = new PlaceholderFoundContent.PlaceholderItem(userid + "", title + "", status + "", descp + "", date1, location + "", username + "", imageUrl + "", contact, avatar,date+"");
                                        PlaceholderFoundContent.ITEMS.add(0, obj);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            databaseReferencePosts.orderByValue().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PlaceholderContent.ITEMS.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        CreatePost post = snapshot1.getValue(CreatePost.class);
                        String title = post.getTitle().toString();
                        String date = post.getDate().toString();
                        String descp = post.getDescription();
                        String status = post.getStatus().toString();
                        String location = post.getLocation().toString();
                        String userid = post.getUid().toString();
                        String contact = post.getContact().toString();
                        String imageUrl = post.getPostImgUrl().toString();
                        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.child(firebaseUser.getUid().toString()).child("username").getValue() + "";
                                PlaceholderContent.PlaceholderItem obj = new PlaceholderContent.PlaceholderItem(uid + "", title + "", status + "", descp + "", date + "", location + "", username + "", imageUrl + "", contact);
                                PlaceholderContent.ITEMS.add(obj);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {
            Log.d("home @ Exception", e + "");

        }

        //checking network connection
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            if (!connected) {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).setIcon(R.drawable.no_net).setTitle("NO INTERNET").setMessage("turn on mobile data or wifi")
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


        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        FragmentTransaction home = getSupportFragmentManager().beginTransaction();
                        home.replace(R.id.fragment_container_view_tag, new HomeFragment());
                        home.commit();
                        break;
                    case 1:

                        FragmentTransaction Post = getSupportFragmentManager().beginTransaction();
                        Post.replace(R.id.fragment_container_view_tag, new PostsFragment());
                        Post.commit();
                        break;
                    case 2:
                        FragmentTransaction Map = getSupportFragmentManager().beginTransaction();
                        Map.replace(R.id.fragment_container_view_tag, new MapsFragment0());
                        Map.commit();
                        break;
                    case 3:
                        FragmentTransaction Profile = getSupportFragmentManager().beginTransaction();
                        Profile.replace(R.id.fragment_container_view_tag, new ProfielFragment());
                        Profile.commit();
                        break;
                }
                return true;
            }
        });


        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                fineLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                            }
                            Boolean coarseLocationGranted = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            }
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                //   Toast.makeText(home.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                // Toast.makeText(home.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                            } else {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), "");
                                intent.setData(uri);
                                startActivity(intent);
                                Toast.makeText(home.this, "Permission requird for location", Toast.LENGTH_SHORT).show();

                                // No location access granted.
                            }
                        }


                );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


    }


    public void onConnected(@Nullable Bundle bundle) {

    }


    public void onConnectionSuspended(int i) {

    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}