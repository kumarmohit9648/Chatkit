package com.mohit.chatkit

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohit.chatkit.AppController.Companion.currentUser
import com.mohit.chatkit.helper.toast
import com.pusher.chatkit.messages.Direction
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {

    val TAG: String = "ChatRoomActivity"

    private var messageAdapter = ChatMessageAdapter()
    private var userAdapter = UsersAdapter()
    private var counter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        userRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        userRecyclerView.adapter = userAdapter

        currentUser.fetchMultipartMessages(
            roomId = intent.getStringExtra("room_id")!!,
            initialId = 42,                       // Optional
            direction = Direction.NEWER_FIRST,    // Optional - OLDER_FIRST by default
            limit = 100,
            callback = { result ->
                when (result) {
                    is Result.Success -> {
                        // messageAdapter.addAllMessage(result.value)
                        onAllMessageReceived(result.value)
                    }

                    is Result.Failure -> {
                        Log.d(TAG, result.error.reason)
                    }
                }
            }
        )

        currentUser.usersForRoom(
            roomId = intent.getStringExtra("room_id")!!,
            callback = { result ->
                when (result) {
                    is Result.Success -> {
                        runOnInitThread {
                            toast(result.value.size.toString())
                            userAdapter.addAllUser(result.value)
                            userAdapter.notifyDataSetChanged()
                        }
                    }
                    is Result.Failure -> {
                        runOnInitThread {

                        }
                    }
                }
            }
        )

        currentUser.users { result ->
            when (result) {
                is Result.Success -> {
                    runOnInitThread {
                        toast(result.value.size.toString())
                        userAdapter.addAllUser(result.value)
                        userAdapter.notifyDataSetChanged()
                    }
                }
                is Result.Failure -> {
                    runOnInitThread {

                    }
                }
            }
        }

        currentUser.subscribeToRoomMultipart(
            roomId = intent.getStringExtra("room_id")!!,
            listeners = RoomListeners(
                onMultipartMessage = { message ->
                    runOnInitThread {
                        // toast(counter.toString())
                        counter++
                        onMessageReceived(message)
                    }
                },
                onPresenceChange = { person ->
                    runOnInitThread {
                        /*if (person.id != currentUser.id) {
                            onOtherMemberPresenceChanged(person)
                        }*/
                    }
                },
                onNewReadCursor = { cursor ->
                    runOnInitThread {
                        /*if (cursor.userId != currentUser.id) {
                            onOtherMemberReadCursorChanged(cursor.position)
                        }*/
                    }
                }
            ),
            messageLimit = 0,
            callback = { subscription ->
                //success
                runOnInitThread {
                    /*getCurrentUserReadCursor(room)
                    getOtherMemberInfo(room)*/
                }
            }
        )
    }

    private val handler = Handler()

    private fun runOnInitThread(runnable: () -> Unit) = handler.post(runnable)

    private fun onMessageReceived(message: Message) {
        runOnInitThread {
            messageAdapter.addMessage(message)
            messageAdapter.notifyDataSetChanged()
            messageRecyclerView.layoutManager?.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun onAllMessageReceived(message: List<Message>) {
        runOnInitThread {
            messageAdapter.addAllMessage(message)
            messageAdapter.notifyDataSetChanged()
            messageRecyclerView.layoutManager?.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    fun submit(view: View) {
        if (edit_text.text.isNotEmpty()) {
            sendMessageToRoom(edit_text.text.trim().toString())
        }
    }

    fun sendMessageToRoom(message: String) {
        currentUser.sendSimpleMessage(
            intent.getStringExtra("room_id")!!,
            message,
            callback = { result ->
                runOnInitThread {
                    when (result) {
                        // we handle the success automatically by display the message
                        is Result.Success -> {
                            runOnUiThread {
                                edit_text.text.clear()
                            }
                        }

                        is Result.Failure -> {
                            toast(result.error.reason)
                            toast(result.error.reason)
                        }

                    }
                }
            })
    }
}
