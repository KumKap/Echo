package com.internshala.echo.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.internshala.echo.R


/**
 * A simple [Fragment] subclass.
 */
class AboutUsFragment : Fragment() {

    var picture: ImageView? = null
    var _devInfo: TextView? = null
    var _devInfo2: TextView? = null
    var _devInfo3: TextView? = null
    var _version: TextView? = null
    var myActivity: Activity? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_about_us, container, false)
        activity.title = "About us"
        setHasOptionsMenu(true)

        picture = view?.findViewById(R.id.joeyPicture)
        _devInfo = view?.findViewById(R.id.devInfo)
        _devInfo2 = view?.findViewById(R.id.devInfo2)
        _devInfo3 = view?.findViewById(R.id.devInfo3)
        _version = view?.findViewById(R.id.version)
        return view
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }








        override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

}// Required empty public constructor
