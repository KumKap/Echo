package com.internshala.echo.Adapters

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.Fragments.AboutUsFragment
import com.internshala.echo.Fragments.FavoriteFragment
import com.internshala.echo.Fragments.MainScreenFragment
import com.internshala.echo.Fragments.SettingsFragment
import com.internshala.echo.R
import com.internshala.echo.R.menu.main
import com.internshala.echo.activities.MainActivity

/**
 * Created by USER on 05-08-2018.
 */
class NavigationDrawerAdapter(_contentlist: ArrayList<String>, _getimages: IntArray, _context: Context): RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList: ArrayList<String>?= null
    var getimages: IntArray?= null
    var mContext: Context?= null
    init{
        this.contentList=_contentlist
        this.getimages= _getimages
        this.mContext= _context
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavViewHolder {
        var itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return contentList?.size as Int
    }

    override fun onBindViewHolder(holder: NavViewHolder?, position: Int) {
        holder?.icon_get?.setBackgroundResource(getimages?.get(position) as Int)
        holder?.text_get?.setText(contentList?.get(position))
        holder?.contentholder?.setOnClickListener({if (position==0){
            val mainScreenFragment= MainScreenFragment()
            (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, mainScreenFragment)
                    .commit()
        } else if(position == 1){val favScreenFragment= FavoriteFragment()
            (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, favScreenFragment)
                    .commit()}
        else if(position == 2){val setScreenFragment= SettingsFragment()
            (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, setScreenFragment)
                    .commit()}
        else {val aboutusFragment= AboutUsFragment()
            (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,aboutusFragment)
                    .commit()}
            MainActivity.Statisfied.drawerLayout?.closeDrawers()
        })

    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var icon_get: ImageView? = null
        var text_get: TextView? = null
        var contentholder: RelativeLayout? = null

        init {
            icon_get = itemView?.findViewById(R.id.Icon_navdrawer)
            text_get = itemView?.findViewById(R.id.text_navdrawer)
            contentholder = itemView?.findViewById(R.id.navdrawer_item_content_holder)


        }
    }
}