package tabi.sample.vpindicator;

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
        viewPagerIndicator.setViewPager(viewPager);

    }
}
