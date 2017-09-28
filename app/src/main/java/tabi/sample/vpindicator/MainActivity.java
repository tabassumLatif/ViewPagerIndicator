package tabi.sample.vpindicator;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tabi.vpindicator.ViewPagerIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.vpIndicator);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        /*viewPagerIndicator.setSelectedColor(Color.BLUE);
        viewPagerIndicator.setUnSelectedColor(Color.GREEN);*/

        viewPagerIndicator.setSelectedDrawable(getResources().getDrawable(R.drawable.ic_tick_select_light_green));
        viewPagerIndicator.setUnSelectedDrawable(getResources().getDrawable(R.drawable.ic_white_select));
        viewPagerIndicator.setViewPager(viewPager);

    }
}
