# ViewPagerIndicator
A viewpager indicator.




Gradle
------------
```groovy
dependencies {
    compile 'com.github.tabassumLatif:ViewPagerIndicator:1.0.+'
}
```

Usage
--------
```xml
	<tabi.vpindicator.ViewPagerIndicator
		android:id="@+id/vpIndicator"
        android:layout_width="match_parent"
        android:layout_height="48dp"/>
```
```java
    ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewpager);
    ViewPagerIndicator viewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.vpIndicator);
    viewpager.setAdapter(mPageAdapter);
    viewPagerIndicator.setViewPager(viewpager);
    // for color
    viewPagerIndicator.setSelectedColor(Color.BLUE);
    viewPagerIndicator.setUnSelectedColor(Color.GREEN);
    
    // for Drawable
    viewPagerIndicator.setSelectedDrawable(getResources().getDrawable(R.drawable.ic_tick_select_light_green));
    viewPagerIndicator.setUnSelectedDrawable(getResources().getDrawable(R.drawable.ic_white_select));
```

##### Properties:

* `app:vpi_width`
* `app:vpi_height`
* `app:vpi_margin`
* `app:vpi_drawable`
* `app:vpi_drawable_unselected`
* `app:vpi_animator`
* `app:vpi_animator_reverse`
* `app:vpi_orientation`
* `app:vpi_gravity`
* `app:vpi_indicator_per_row`
