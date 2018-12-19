package com.selfie.life.myinstacropper.Filter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selfie.life.myinstacropper.Filter.Utils.BitmapUtils;
import com.selfie.life.myinstacropper.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFilterActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {

    public static final int SELECT_GALLERY_IMAGE = 101;
    private static final String TAG = MainFilterActivity.class.getSimpleName();
    public static String imagePathonly1;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    //@BindView(R.id.image_preview)
    ImageView imagePreview;
    TextView btn_next;
    //@BindView(R.id.tabs)
    TabLayout tabLayout;
    //@BindView(R.id.viewpager)
    ViewPager viewPager;
    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;
    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;
    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;
    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_filter);

        Log.d(TAG, "hellow  >" + imagePathonly1);
        //ButterKnife.bind(this);

        // String getDataIntent= getIntent().getParcelableExtra("BitmapImage");

       /* imagePath1 = MyConfiguration.getPreferences(getApplicationContext(), "imagepath").replace("file://", "");
        Log.d(TAG,  " calledfinal  >"+imagePath1);
        imagePathonly =MyConfiguration.getPreferences(getApplicationContext(), "imagepath").replace("file://", "");
        Log.d(TAG, "imagePath = " + MyConfiguration.getPreferences(getApplicationContext(), "imagepath").replace("file://", ""));

*/
        getSupportActionBar().hide();
        imagePreview = findViewById(R.id.image_preview);
        btn_next = findViewById(R.id.btn_next);
        viewPager = findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        imagePathonly1 = String.valueOf(getIntent().getData()).replace("file://", "");
        loadImage();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToGallery();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */

    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    private void loadImage() {

        // Uri imagepath= Uri.parse(imagePath1);
        // Log.d(TAG,"lastfinakimage >"+imagepath);
        // Log.d(TAG,"result value  >"+imagePathonly);
        // Uri imageUri = getIntent().getData();

        Log.d(TAG, "lastfinakimage111 >" + imagePathonly1);

        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
        originalImage = BitmapFactory.decodeFile(String.valueOf(imagePathonly1), btmapOptions);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);

    }

    // load the default image from assets on app launch

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    Log.d(TAG, "path_get >>  " + selectedImage);

                    // method 1
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                        originalImage.recycle();
                        finalImage.recycle();
                        finalImage.recycle();

                        originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                        imagePreview.setImageBitmap(originalImage);
                        bitmap.recycle();

                        // render selected image thumbnails
                        filtersListFragment.prepareThumbnail(originalImage);
                        // iv.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                break;

        }

    }

    private void openImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);

    }

    /*
     * saves image to camera gallery
     * */
    private void saveImageToGallery() {

//        final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
//        Toast.makeText(this, "Save image from gallary", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "pathgetter  >" + path);

        final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
        Log.d(TAG,"crop path = "+path);
        Intent intent = getIntent();
        intent.putExtra("MESSAGE", path);
        setResult(RESULT_OK,intent);
        finish();

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
