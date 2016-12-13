/*
 * Copyright (C) 2011 The Android Open Source Project
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

package io.connorwyatt.flashcards.adapters

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.os.Parcelable
import android.support.v13.app.FragmentCompat
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

/**
 * Implementation of [android.support.v4.view.PagerAdapter] that
 * uses a [Fragment] to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 *
 *
 * This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * [FragmentPagerAdapter] at the cost of potentially more overhead when
 * switching between pages.
 *
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter.
 *
 *
 *
 * Here is an example implementation of a pager containing fragments of
 * lists:
 *
 *
 * {@sample frameworks/support/samples/Support4Demos/src/com/example/android/supportv4/app
 * * /FragmentStatePagerSupport.java
 * * complete}
 *
 *
 *
 * The `R.layout.fragment_pager` resource of the top-level fragment is:
 *
 *
 * {@sample frameworks/support/samples/Support4Demos/res/layout/fragment_pager.xml
 * * complete}
 *
 *
 *
 * The `R.layout.fragment_pager_list` resource containing each
 * individual fragment's layout is:
 *
 *
 * {@sample frameworks/support/samples/Support4Demos/res/layout/fragment_pager_list.xml
 * * complete}
 */
abstract class FixedFragmentStatePagerAdapter(private val mFragmentManager: FragmentManager) :
    PagerAdapter()
{
    private var mCurTransaction: FragmentTransaction? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private var mSavedFragmentTags = ArrayList<String?>()
    private val mFragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null

    override fun startUpdate(container: ViewGroup)
    {
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any
    {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size > position)
        {
            val f = mFragments[position]
            f.let {
                return it!!
            }
        }

        if (mCurTransaction == null)
        {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val fragment = getItem(position)
        val fragmentTag = getFragmentTag(position)
        if (DEBUG) Log.v(TAG.substring(0, 22), "Adding item #$position: f=$fragment t=$fragmentTag")
        if (mSavedState.size > position)
        {
            val savedTag = mSavedFragmentTags[position]
            if (TextUtils.equals(fragmentTag, savedTag))
            {
                val fss = mSavedState[position]
                fss.let {
                    fragment.setInitialSavedState(it)
                }
            }
        }
        while (mFragments.size <= position)
        {
            mFragments.add(null)
        }
        FragmentCompat.setMenuVisibility(fragment, false)
        FragmentCompat.setUserVisibleHint(fragment, false)
        mFragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment)

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any)
    {
        val fragment = item as Fragment

        if (mCurTransaction == null)
        {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        if (DEBUG)
            Log.v(TAG.substring(0, 22),
                  "Removing item #$position: f=$item v=${item.view} t=${fragment.tag}")
        while (mSavedState.size <= position)
        {
            mSavedState.add(null)
            mSavedFragmentTags.add(null)
        }
        mSavedState[position] = mFragmentManager.saveFragmentInstanceState(fragment)
        mSavedFragmentTags[position] = fragment.tag
        mFragments.set(position, null)

        mCurTransaction!!.remove(fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, item: Any)
    {
        val fragment = item as Fragment
        if (fragment !== mCurrentPrimaryItem)
        {
            mCurrentPrimaryItem.let {
                FragmentCompat.setMenuVisibility(mCurrentPrimaryItem, false)
                FragmentCompat.setUserVisibleHint(mCurrentPrimaryItem, false)
            }
            fragment.let {
                FragmentCompat.setMenuVisibility(fragment, true)
                FragmentCompat.setUserVisibleHint(fragment, true)
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup)
    {
        if (mCurTransaction != null)
        {
            mCurTransaction!!.commitAllowingStateLoss()
            mCurTransaction = null
            mFragmentManager.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, item: Any): Boolean
    {
        return (item as Fragment).view === view
    }

    override fun saveState(): Parcelable
    {
        var state: Bundle? = null
        if (mSavedState.isNotEmpty())
        {
            state = Bundle()
            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
            mSavedState.toArray(fss)
            state.putParcelableArray("states", fss)
            state.putStringArrayList("tags", mSavedFragmentTags)
        }
        mFragments.indices.forEach {
            val f = mFragments[it]
            if (f != null && f.isAdded)
            {
                if (state == null)
                {
                    state = Bundle()
                }
                val key = "f$it"
                mFragmentManager.putFragment(state, key, f)
            }
        }
        return state!!
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?)
    {
        if (state != null)
        {
            val bundle = state as Bundle?
            bundle!!.classLoader = loader
            val fss = bundle.getParcelableArray("states")
            mSavedState.clear()
            mFragments.clear()

            val tags = bundle.getStringArrayList("tags")
            if (tags != null)
            {
                mSavedFragmentTags = tags
            }
            else
            {
                mSavedFragmentTags.clear()
            }
            if (fss != null)
            {
                fss.indices.forEach { mSavedState.add(fss[it] as Fragment.SavedState) }
            }
            val keys = bundle.keySet()
            keys.forEach {
                if (it.startsWith("f"))
                {
                    val index = Integer.parseInt(it.substring(1))
                    val f = mFragmentManager.getFragment(bundle, it)
                    if (f != null)
                    {
                        while (mFragments.size <= index)
                        {
                            mFragments.add(null)
                        }
                        FragmentCompat.setMenuVisibility(f, false)
                        mFragments[index] = f
                    }
                    else
                    {
                        Log.w(TAG.substring(0, 22), "Bad fragment at key $it")
                    }
                }
            }
        }
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    /**
     * Return the unique tag associated with a specified position.
     */
    abstract fun getFragmentTag(position: Int): String

    companion object
    {
        private val TAG = "FixedFragmentStatePagerAdapter"
        private val DEBUG = false
    }
}
