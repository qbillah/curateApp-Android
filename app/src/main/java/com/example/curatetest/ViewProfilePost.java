package com.example.curatetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfilePost extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageButton home;
    private ImageView postDisplay;
    private TextView usernameDisplay;
    private LinearLayout tagsDisplay;
    private Post userPost;
    private CircleImageView userPPF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_post);

        mAuth = FirebaseAuth.getInstance();
        home = (ImageButton) findViewById(R.id.returnHome);
        postDisplay = (ImageView) findViewById(R.id.postDisplay);
        usernameDisplay = (TextView) findViewById(R.id.displayUserName);
        tagsDisplay = (LinearLayout) findViewById(R.id.displayTags);
        userPPF = (CircleImageView) findViewById(R.id.userPPFDisplay);

        Intent post = getIntent();
        String key = post.getStringExtra("postID");

        Query toDisplay = FirebaseDatabase.getInstance().getReference("posts").orderByKey().equalTo(key);

        toDisplay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    /*
                    userPost.setURL(snap.child("URL").getValue().toString());
                    userPost.setTimestamp(snap.child("timestamp").getValue().toString());
                    userPost.setTags((ArrayList<String>) snap.child("tags").getValue());
                    userPost.setUploadedBy(snap.child("uploadedBy").getValue().toString());*/

                    ArrayList<String> tags = new ArrayList<>();
                    tags.addAll((ArrayList<String>) snap.child("tags").getValue());

                    Picasso.get().load(snap.child("URL").getValue().toString()).into(postDisplay);
                    usernameDisplay.setText(snap.child("uploadedBy").getValue().toString());

                    for(String s : tags){
                        TextView t = new TextView(getApplicationContext());
                        t.setText("#" + s);
                        t.setTextSize(14);
                        t.setPadding(30 , 10 , 30 , 0);
                        t.setTextColor(getResources().getColor(R.color.borderColor));
                        tagsDisplay.addView(t);
                    }

                    String rawTS = snap.child("timestamp").getValue().toString().substring(0 ,8);
                    rawTS = rawTS.substring(0 , 4) + "-" + rawTS.substring(4 , 6) + "-" + rawTS.substring(6 , 8);

                    try {
                        Date upload = new SimpleDateFormat("yyyy-mm-dd").parse(rawTS);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(upload);

                        String display = new SimpleDateFormat("MMM").format(calendar.getTime()).toString();
                        display = display + " " + new SimpleDateFormat("dd").format(calendar.getTime()).toString();
                        display = display + ", " + new SimpleDateFormat("yyyy").format(calendar.getTime()).toString();

                        TextView stamp = new TextView(getApplicationContext());
                        stamp.setText(display);
                        stamp.setTextSize(14);
                        stamp.setPadding(30 , 15 , 30 , 0);
                        stamp.setTextColor(getResources().getColor(R.color.colorPrimary2));
                        tagsDisplay.addView(stamp);
                        tagsDisplay.invalidate();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /*20201204



                    Timestamp ts = new Timestamp();
                    Date date = new Date(ts.getTime());

                    */

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Query query =  FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String ppf = dataSnapshot.child("PPF").getValue(String.class);

                    userPPF = (CircleImageView) findViewById(R.id.userPPFDisplay);
                    Picasso.get().load(ppf).resize(75, 75).centerCrop().into(userPPF);

                }else{

                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}