# ViewPagerIndicator
A viewpager indicator.




Gradle
------------
```groovy
dependencies {
    compile 'com.github.tabassumLatif:ViewPagerIndicator:1.0.0'
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
