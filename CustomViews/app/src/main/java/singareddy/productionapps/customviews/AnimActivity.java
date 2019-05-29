package singareddy.productionapps.customviews;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;

public class AnimActivity extends AppCompatActivity {

    CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_activity);
        cardView = findViewById(R.id.card);
        TransitionManager.beginDelayedTransition(cardView, new Fade());
        cardView.setVisibility(cardView.isShown() ? View.INVISIBLE : View.VISIBLE);
    }
}
