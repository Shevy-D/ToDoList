package com.shevy.todolist.main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.shevy.todolist.R
import com.shevy.todolist.about.AboutActivity
import com.shevy.todolist.analytics.AnalyticsApplication
import com.shevy.todolist.appDefault.AppDefaultFragment
import com.shevy.todolist.settings.SettingsActivity
import com.shevy.todolist.utility.RecyclerViewEmptySupport
import com.shevy.todolist.utility.StoreRetrieveData
import com.shevy.todolist.utility.ToDoItem
import org.json.JSONException
import java.io.IOException
import java.util.*

class MainFragment : AppDefaultFragment() {
    lateinit var mRecyclerView: RecyclerViewEmptySupport
    private var mAddToDoItemFAB: FloatingActionButton? = null
    private var mToDoItemsArrayList: ArrayList<ToDoItem?>? = null
    private var mCoordLayout: CoordinatorLayout? = null
    //private var adapter: BasicListAdapter? = null
    private var mJustDeletedToDoItem: ToDoItem? = null
    private var mIndexOfDeletedToDoItem = 0
    private var storeRetrieveData: StoreRetrieveData? = null
    var itemTouchHelper: ItemTouchHelper? = null
    private lateinit var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener
    private var mTheme = -1
    private var theme: String? = "name_of_the_theme"
    private var app: AnalyticsApplication? = null
    private val testStrings = arrayOf(
        "Clean my room",
        "Water the plants",
        "Get car washed",
        "Get my dry cleaning"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity?.application as AnalyticsApplication
        //        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Aller_Regular.tff").setFontAttrId(R.attr.fontPath).build());

        //We recover the theme we've set and setTheme accordingly
        theme = activity?.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)?.getString(
            THEME_SAVED, LIGHTTHEME
        )
        mTheme = if (theme == LIGHTTHEME) {
            R.style.CustomStyle_LightTheme
        } else {
            R.style.CustomStyle_DarkTheme
        }
        this.activity?.setTheme(mTheme)
        super.onCreate(savedInstanceState)
        val sharedPreferences =
            activity?.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean(CHANGE_OCCURED, false)
        editor?.apply()
        storeRetrieveData = context?.let { StoreRetrieveData(it, FILENAME) }
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
        //adapter = BasicListAdapter(mToDoItemsArrayList!!)
        setAlarms()


        mCoordLayout = view.findViewById<View>(R.id.myCoordinatorLayout) as CoordinatorLayout
        mAddToDoItemFAB = view.findViewById<View>(R.id.addToDoItemFAB) as FloatingActionButton
        mAddToDoItemFAB!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                app!!.send(this, "Action", "FAB pressed")
                /*val newTodo = Intent(context, AddToDoActivity::class.java)
                val item = ToDoItem("", "", false, null)
                val color = ColorGenerator.MATERIAL.randomColor
                item.todoColor = color

                newTodo.putExtra(TODOITEM, item)

                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM)*/
            }
        })


//        mRecyclerView = (RecyclerView)findViewById(R.id.toDoRecyclerView);
        mRecyclerView = view.findViewById<View>(R.id.toDoRecyclerView) as RecyclerViewEmptySupport
        if (theme == LIGHTTHEME) {
            mRecyclerView.setBackgroundColor(resources.getColor(R.color.primary_lightest))
        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView))
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {
            override fun show() {
                mAddToDoItemFAB!!.animate().translationY(0f)
                    .setInterpolator(DecelerateInterpolator(2f)).start()
                //                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            override fun hide() {
                val lp = mAddToDoItemFAB!!.layoutParams as CoordinatorLayout.LayoutParams
                val fabMargin = lp.bottomMargin
                mAddToDoItemFAB!!.animate()
                    .translationY((mAddToDoItemFAB!!.height + fabMargin).toFloat())
                    .setInterpolator(AccelerateInterpolator(2.0f)).start()
            }
        }
/*        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener)
        val callback: ItemTouchHelper.Callback = ItemTouchHelperClass(adapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper!!.attachToRecyclerView(mRecyclerView)
        mRecyclerView.adapter = adapter*/
        //        setUpTransitions();
    }

    override fun onResume() {
        super.onResume()
        app!!.send(this)
        /*val sharedPreferences =
            activity?.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
                val editor = sharedPreferences.edit()
                editor.putBoolean(ReminderFragment.EXIT, false)
                editor.apply()
                activity?.finish()
            }
        }*/
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */if (activity?.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)?.getBoolean(
                RECREATE_ACTIVITY, false
            ) == true
        ) {
            val editor =
                activity?.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)!!.edit()
            editor.putBoolean(RECREATE_ACTIVITY, false)
            editor.apply()
            activity?.recreate()
        }
    }

    override fun onStart() {
        app = activity?.application as AnalyticsApplication
        super.onStart()
        /*val sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {
            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
            adapter = BasicListAdapter(mToDoItemsArrayList!!)
            mRecyclerView.adapter = adapter
            setAlarms()
            val editor = sharedPreferences.edit()
            editor.putBoolean(CHANGE_OCCURED, false)
            //            editor.commit();
            editor.apply()
        }*/
    }

    private fun setAlarms() {
        /*if (mToDoItemsArrayList != null) {
            for (item in mToDoItemsArrayList!!) {
                if (item!!.hasReminder() && item.toDoDate != null) {
                    if (item.toDoDate!!.before(Date())) {
                        item.toDoDate = null
                        continue
                    }
                    val i = Intent(context, TodoNotificationService::class.java)
                    i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                    i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                    createAlarm(i, item.identifier.hashCode(), item.toDoDate!!.getTime())
                }
            }
        }*/
    }

    fun addThemeToSharedPreferences(theme: String?) {
        val sharedPreferences =
            activity?.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(THEME_SAVED, theme)
        editor?.apply()
    }

    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        activity?.menuInflater?.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(context, AboutActivity::class.java)
                startActivity(i)
                true
            }
            R.id.preferences -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (resultCode != Activity.RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            val item = data?.getSerializableExtra(TODOITEM) as ToDoItem?
            if (item?.toDoText?.isEmpty() == true) {
                return
            }
            var existed = false
            if (item!!.hasReminder() && item.toDoDate != null) {
                val i = Intent(context, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                createAlarm(i, item.identifier.hashCode(), item.toDoDate!!.time)
                //                Log.d("OskarSchindler", "Alarm Created: "+item.getToDoText()+" at "+item.getToDoDate());
            }
            for (i in mToDoItemsArrayList!!.indices) {
                if (item.identifier.equals(mToDoItemsArrayList!![i]?.identifier)) {
                    mToDoItemsArrayList!![i] = item
                    existed = true
                    adapter!!.notifyDataSetChanged()
                    break
                }
            }
            if (!existed) {
                addToDataStore(item)
            }
        }*/
    }

    private val alarmManager: AlarmManager
        get() = activity?.getSystemService(ALARM_SERVICE) as AlarmManager

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pi != null
    }

    private fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am = alarmManager
        val pi =
            PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am[AlarmManager.RTC_WAKEUP, timeInMillis] = pi
        //        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            pi.cancel()
            alarmManager.cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }

    private fun addToDataStore(item: ToDoItem?) {
        mToDoItemsArrayList!!.add(item)
        //adapter!!.notifyItemInserted(mToDoItemsArrayList!!.size - 1)
    }

    fun makeUpItems(items: ArrayList<ToDoItem?>, len: Int) {
        for (testString in testStrings) {
            val item = ToDoItem(testString, testString, false, Date())

//            item.setTodoColor(getResources().getString(R.color.red_secondary));
            items.add(item)
        }
    }

/*    inner class BasicListAdapter internal constructor(private val items: ArrayList<ToDoItem?>) :
        RecyclerView.Adapter<BasicListAdapter.ViewHolder>(),
        ItemTouchHelperClass.ItemTouchHelperAdapter {
        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemRemoved(position: Int) {
            //Remove this line if not using Google Analytics
            app!!.send(this, "Action", "Swiped Todo Away")
            mJustDeletedToDoItem = items.removeAt(position)
            mIndexOfDeletedToDoItem = position
            val i = Intent(context, TodoNotificationService::class.java)
            deleteAlarm(i, mJustDeletedToDoItem?.identifier.hashCode())
            notifyItemRemoved(position)

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            val toShow = "Todo"
            Snackbar.make(mCoordLayout!!, "Deleted $toShow", Snackbar.LENGTH_LONG)
                .setAction("UNDO", object : View.OnClickListener {
                    override fun onClick(v: View) {

                        //Comment the line below if not using Google Analytics
                        app!!.send(this, "Action", "UNDO Pressed")
                        items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem)
                        if (mJustDeletedToDoItem?.toDoDate != null && mJustDeletedToDoItem!!.hasReminder()) {
                            val i = Intent(context, TodoNotificationService::class.java)
                            i.putExtra(
                                TodoNotificationService.TODOTEXT,
                                mJustDeletedToDoItem!!.toDoText
                            )
                            i.putExtra(
                                TodoNotificationService.TODOUUID,
                                mJustDeletedToDoItem!!.identifier
                            )
                            mJustDeletedToDoItem!!.toDoDate?.let {
                                createAlarm(
                                    i,
                                    mJustDeletedToDoItem!!.identifier.hashCode(),
                                    it.time
                                )
                            }
                        }
                        notifyItemInserted(mIndexOfDeletedToDoItem)
                    }
                }).show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.list_circle_try, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            //            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }
            val sharedPreferences =
                activity?.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            var bgColor: Int = 0
            //Background color for each to-do item. Necessary for night/day mode
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            var todoTextColor: Int = 0
            if (sharedPreferences != null) {
                if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME) == LIGHTTHEME) {
                    bgColor = Color.WHITE
                    todoTextColor = resources.getColor(R.color.secondary_text)
                } else {
                    bgColor = Color.DKGRAY
                    todoTextColor = Color.WHITE
                }
            }
            holder.linearLayout.setBackgroundColor(bgColor)
            if (item!!.hasReminder() && item.toDoDate != null) {
                holder.mToDoTextview.maxLines = 1
                holder.mTimeTextView.visibility = View.VISIBLE
                //                holder.mToDoTextview.setVisibility(View.GONE);
            } else {
                holder.mTimeTextView.visibility = View.GONE
                holder.mToDoTextview.maxLines = 2
            }
            holder.mToDoTextview.text = item.toDoText
            holder.mToDoTextview.setTextColor(todoTextColor)
            //            holder.mColorTextView.setBackgroundColor(Color.parseColor(item.getTodoColor()));

//            TextDrawable myDrawable = TextDrawable.builder().buildRoundRect(item.getToDoText().substring(0,1),Color.RED, 10);
            //We check if holder.color is set or not
//            if(item.getTodoColor() == null){
//                ColorGenerator generator = ColorGenerator.MATERIAL;
//                int color = generator.getRandomColor();
//                item.setTodoColor(color+"");
//            }
//            Log.d("OskarSchindler", "Color: "+item.getTodoColor());
            val myDrawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(item.toDoText.substring(0, 1), item.todoColor)

//            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            holder.mColorImageView.setImageDrawable(myDrawable)
            if (item.toDoDate != null) {
                val timeToShow: String
                timeToShow = if (is24HourFormat(context)) {
                    AddToDoFragment.formatDate(DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                } else {
                    AddToDoFragment.formatDate(DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                }
                holder.mTimeTextView.text = timeToShow
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(var mView: View) : RecyclerView.ViewHolder(
            mView
        ) {
            var linearLayout: LinearLayout
            var mToDoTextview: TextView

            //            TextView mColorTextView;
            var mColorImageView: ImageView
            var mTimeTextView: TextView

            //            int color = -1;
            init {
                mView.setOnClickListener {
                    val item = items[adapterPosition]
                    val i = Intent(context, AddToDoActivity::class.java)
                    i.putExtra(TODOITEM, item)
                    startActivityForResult(i, REQUEST_ID_TODO_ITEM)
                }
                mToDoTextview = mView.findViewById<View>(R.id.toDoListItemTextview) as TextView
                mTimeTextView = mView.findViewById<View>(R.id.todoListItemTimeTextView) as TextView
                //                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView =
                    mView.findViewById<View>(R.id.toDoListItemColorImageView) as ImageView
                linearLayout = mView.findViewById<View>(R.id.listItemLinearLayout) as LinearLayout
            }
        }
    }
   */
    private fun saveDate() {
        try {
            storeRetrieveData!!.saveToFile(mToDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            storeRetrieveData!!.saveToFile(mToDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecyclerView!!.removeOnScrollListener(customRecyclerScrollViewListener)
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_main
    }

    companion object {
        const val TODOITEM =
            "com.shevy.todolist.MainActivity"
        private const val REQUEST_ID_TODO_ITEM = 100
        const val DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a"
        const val DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm"
        const val FILENAME = "todoitems.json"
        const val SHARED_PREF_DATA_SET_CHANGED = "com.shvey.datasetchanged"
        const val CHANGE_OCCURED = "com.shvey.changeoccured"
        const val THEME_PREFERENCES = "com.shvey.themepref"
        const val RECREATE_ACTIVITY = "com.shvey.recreateactivity"
        const val THEME_SAVED = "com.shvey.savedtheme"
        const val DARKTHEME = "com.shvey.darktheme"
        const val LIGHTTHEME = "com.shvey.lighttheme"
        fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData?): ArrayList<ToDoItem?> {
            var items: ArrayList<ToDoItem?>? = null
            try {
                //CHANGE IT
                //items = storeRetrieveData!!.loadFromFile()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (items == null) {
                items = ArrayList()
            }
            return items
        }

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}