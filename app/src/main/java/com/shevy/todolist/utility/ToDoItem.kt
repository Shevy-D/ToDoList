package com.shevy.todolist.utility

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class ToDoItem : Serializable {
    var toDoText: String
    private var mHasReminder: Boolean

    //add description
    private var mToDoDescription: String

    //    private Date mLastEdited;
    var todoColor: Int
    var toDoDate: Date? = null
    var identifier: UUID
        private set

    @JvmOverloads
    constructor(
        todoBody: String = "Clean my room",
        tododescription: String = "Sweep and Mop my Room",
        hasReminder: Boolean = true,
        toDoDate: Date? = Date()
    ) {
        toDoText = todoBody
        mHasReminder = hasReminder
        this.toDoDate = toDoDate
        mToDoDescription = tododescription
        todoColor = 1677725
        identifier = UUID.randomUUID()
    }

    constructor(jsonObject: JSONObject) {
        toDoText = jsonObject.getString(TODOTEXT)
        mToDoDescription = jsonObject.getString(TODODESCRIPTION)
        mHasReminder = jsonObject.getBoolean(TODOREMINDER)
        todoColor = jsonObject.getInt(TODOCOLOR)
        identifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER))

//        if(jsonObject.has(TODOLASTEDITED)){
//            mLastEdited = new Date(jsonObject.getLong(TODOLASTEDITED));
//        }
        if (jsonObject.has(TODODATE)) {
            toDoDate = Date(jsonObject.getLong(TODODATE))
        }
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(TODOTEXT, toDoText)
        jsonObject.put(TODOREMINDER, mHasReminder)
        jsonObject.put(TODODESCRIPTION, mToDoDescription)
        //        jsonObject.put(TODOLASTEDITED, mLastEdited.getTime());
        if (toDoDate != null) {
            jsonObject.put(TODODATE, toDoDate!!.time)
        }
        jsonObject.put(TODOCOLOR, todoColor)
        jsonObject.put(TODOIDENTIFIER, identifier.toString())
        return jsonObject
    }

    fun getmToDoDescription(): String {
        return mToDoDescription
    }

    fun setmToDoDescription(mToDoDescription: String) {
        this.mToDoDescription = mToDoDescription
    }

    fun hasReminder(): Boolean {
        return mHasReminder
    }

    fun setHasReminder(mHasReminder: Boolean) {
        this.mHasReminder = mHasReminder
    }

    companion object {
        //add description
        private const val TODODESCRIPTION = "tododescription"
        private const val TODOTEXT = "todotext"
        private const val TODOREMINDER = "todoreminder"

        //    private static final String TODOLASTEDITED = "todolastedited";
        private const val TODOCOLOR = "todocolor"
        private const val TODODATE = "tododate"
        private const val TODOIDENTIFIER = "todoidentifier"
    }
}