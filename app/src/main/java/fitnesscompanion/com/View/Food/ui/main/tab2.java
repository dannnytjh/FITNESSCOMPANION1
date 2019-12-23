package fitnesscompanion.com.View.Food.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import fitnesscompanion.com.Database.UserDB;
import fitnesscompanion.com.Model.User;
import fitnesscompanion.com.R;
import fitnesscompanion.com.Util.BarCodeActivity;
import fitnesscompanion.com.View.Food.SearchFoodActivity;
import fitnesscompanion.com.View.Food.UploadFoodActivity;

public class tab2 extends Fragment {
    private Context context;

    @BindView(R.id.menu_food)
    FloatingActionMenu menu_food;
    @BindView(R.id.fabUpload)
    com.github.clans.fab.FloatingActionButton fabUpload;
    @BindView(R.id.cv1) CardView cv1;
    @BindView(R.id.cv2) CardView cv2;
    @BindView(R.id.cv3) CardView cv3;
    @BindView(R.id.cv4) CardView cv4;

    private UserDB userDB;
    private User user;
    int traineeId;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);
        ButterKnife.bind(this, rootView);
        UserDB userDB = new UserDB(getContext());

        User user = userDB.getData();

        traineeId = user.getId();

        fabUpload.setOnClickListener(onMenuControl);

        return rootView;
    }


    //menu control
    private View.OnClickListener onMenuControl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                /*
                case R.id.fabAdd :
                    Toast.makeText(context,"Add",Toast.LENGTH_LONG).show();
                    break;*/
                case R.id.fabUpload:
                    getActivity().finish();
                    startActivity(new Intent(getContext(), UploadFoodActivity.class).putExtra("traineeId", traineeId));
                    break;

                /*case R.id.fabNearBy:
                    getActivity().finish();
                    startActivity(new Intent(getContext(), RestaurantActivity.class));
                    break;*/

            }
            menu_food.close(true);
        }
    };
}
