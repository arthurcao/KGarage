/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dinh.kgarage.MyGarage;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SlidingTabLayout extends HorizontalScrollView {

    public interface TabColorizer {

        int getIndicatorColor(int position);

        int getDividerColor(int position);

    }

    
    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private int mTabViewLayoutId;
    private int mTabViewTextViewId;

    private int mWidthLayout = 0;
    private int mWidthIndicator = 0;
    
    public void setWidthScreen(int w){
    	mWidthLayout = w;
    }
//    private ArrayList<Fragment> mListFragment;
    private ArrayList<String> mListTitle;
    private int mCurrentItem = 0;

    private final SlidingTabStrip mTabStrip;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
    
  

    /**
     * Set the custom {@link com.example.dinh.kgarage.MyGarage.SlidingTabLayout.TabColorizer} to be used.
     *
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }

    
//    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
//        mViewPagerPageChangeListener = listener;
//    }

    
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }


    public void setListTabs( ArrayList<String> title){
    	
    	mListTitle = title;
    	mCurrentItem = 0;
    	populateTabStrip();
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
//            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
//        textView.setPadding(padding, padding, padding, padding);
        textView.setPadding(0, padding, 0, padding);

        if(mWidthLayout > 0 && mWidthIndicator > 0){
        	//TODO set width textview
        	LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(mWidthIndicator, LinearLayout.LayoutParams.WRAP_CONTENT);
        	textView.setLayoutParams(p);
        }
        return textView;
    }

    private void populateTabStrip() {    	
    	
    	
//        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();
        int count = mListTitle.size();
        
        if(mWidthLayout > 0){
	        mWidthIndicator = 0;
	        if(count <= 5){
	        	mWidthIndicator = mWidthLayout/count;
	        }else{
	        	int n = 0;
	        	while(true){
	        		if(mWidthIndicator >= 200){
	        			break;
	        		}        		
	        		int w = mWidthLayout/(count - n);
	        		n++;
	        		
	        		if(w == mWidthLayout){
	        			mWidthIndicator = 0;
	        			break;
	        		}
	        		
	        	}
	        }
        
        }
        
        
        
        for (int i = 0; i < count; i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout door_id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

//

            tabTitleView.setText(mListTitle.get(i));
//            tabTitleView.setText("item " + i);
            tabView.setOnClickListener(tabClickListener);

            mTabStrip.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mListTitle != null) {
            scrollToTab(mCurrentItem, 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
                final int tabStripChildCount = mTabStrip.getChildCount();
                if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
                    return;
                }

                View selectedChild = mTabStrip.getChildAt(tabIndex);
                if (selectedChild != null) {
                    int targetScrollX = selectedChild.getLeft() + positionOffset;
                    if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

//    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
//        private int mScrollState;
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            int tabStripChildCount = mTabStrip.getChildCount();
//            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
//                return;
//            }
//
//            mTabStrip.onViewPagerPageChanged(position, positionOffset);
//
//            View selectedTitle = mTabStrip.getChildAt(position);
//            int extraOffset = (selectedTitle != null)
//                    ? (int) (positionOffset * selectedTitle.getWidth())
//                    : 0;
//            scrollToTab(position, extraOffset);
//
//            if (mViewPagerPageChangeListener != null) {
//                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
//                        positionOffsetPixels);
//            }
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//            mScrollState = state;
//
//            if (mViewPagerPageChangeListener != null) {
//                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
//            }
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
//                mTabStrip.onViewPagerPageChanged(position, 0f);
//                scrollToTab(position, 0);
//            }
//
//            if (mViewPagerPageChangeListener != null) {
//                mViewPagerPageChangeListener.onPageSelected(position);
//            }
//        }
//
//    }
    
	public void onPageScrolled(int position) {
			int tabStripChildCount = mTabStrip.getChildCount();
			if ((tabStripChildCount == 0) || (position < 0)
					|| (position >= tabStripChildCount)) {
				return;
			}

			mTabStrip.onViewPagerPageChanged(position, 0);

//			View selectedTitle = mTabStrip.getChildAt(position);
			
			scrollToTab(position, 0);

			
		}

		

	
    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                	mCurrentItem = i;
                	onPageScrolled(mCurrentItem);
//                    mViewPager.setCurrentItem(i);
                	if(mTabLayoutListener != null){
                		mTabLayoutListener.onSelectedItem(mCurrentItem);
                	}
                    return;
                }
            }
        }
    }
    
    TabLayoutListener mTabLayoutListener = null;
    public void setOnListener(TabLayoutListener l){
    	mTabLayoutListener = l;
    }

    public void selectTab(int position){

        if (mListTitle != null) {
            mCurrentItem = position;
//            Log.e("20150521","selectTab mCurrentItem: "+ mCurrentItem);
            onPageScrolled( mCurrentItem);
        }
    }

    public int getCurrentItem(){
        return mCurrentItem;
    }
    
    public interface TabLayoutListener{
    	public void onSelectedItem(int item);
    }

}
