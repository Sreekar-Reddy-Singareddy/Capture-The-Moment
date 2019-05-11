package singareddy.productionapps.capturethemoment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ExperimentActivity extends AppCompatActivity {
    private static String TAG = "ExperimentActivity";

    private RoundedImageView photos;
    private int photoNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_item_big_card);
        sampleData();
//        photos = findViewById(R.id.card_front_iv_photo);
//        photos.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (photoNumber == 6) {
//                    photoNumber = 1;
//                }
//                switch (photoNumber) {
//                    case 1:
//                        photos.setImageResource(R.drawable.photo);
//                        break;
//                    case 2:
//                        photos.setImageResource(R.drawable.photo2);
//                        break;
//                    case 3:
//                        photos.setImageResource(R.drawable.photo3);
//                        break;
//                    case 4:
//                        photos.setImageResource(R.drawable.photo4);
//                        break;
//                    case 5:
//                        photos.setImageResource(R.drawable.photo5);
//                        break;
//                }
//                photoNumber++;
//                return false;
//            }
//        });
    }

    private void sampleData() {
        FirebaseDatabase DB = FirebaseDatabase.getInstance();
        DatabaseReference bookRef  = DB.getReference().child("books/-LdIch0uGaw-3ZW0XWC6/cards");
//        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i(TAG, "onDataChange: *");
//                ArrayList<String> cards = (ArrayList<String>) dataSnapshot.getValue();
//                Log.i(TAG, "onDataChange: CARDS: "+cards);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        ArrayList<String> newCards = new ArrayList<>();
        newCards.add("c3"); newCards.add("c4");
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("cards", newCards);
        Log.i(TAG, "sampleData: UPDATE MAP: "+updateMap);
        bookRef.updateChildren(updateMap);
    }
}
