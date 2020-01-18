package com.mohit.chatkit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohit.chatkit.AppController.Companion.currentUser
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatListeners
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import com.pusher.chatkit.rooms.Room
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_rooms.*

class RoomsActivity : AppCompatActivity() {

    val adapter = RoomsAdapter()
    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        userId = intent.getStringExtra("userId")

        initRecyclerView()
        initChatManager()
    }

    private fun initRecyclerView() {
        messageRecyclerView.layoutManager = LinearLayoutManager(this@RoomsActivity)
    }

    private fun initChatManager() {
        val chatManager = ChatManager(
            instanceLocator = "v1:us1:96a58ec5-d3be-4bcc-99da-03077301e7f9",
            userId = userId!!,
            dependencies = AndroidChatkitDependencies(
                tokenProvider = ChatkitTokenProvider(
                    endpoint = "https://breadcrumbstheapp.com/api/chatkit-server-php-master/examples/generate_token.php?user_id=" + userId,
                    userId = userId!!
                )
            )
        )

        // TODO: What do we need to do with these that were here before?
//                .context(this@RoomsListActivity)
//        intent.getStringExtra("extra")

        chatManager.connect(listeners = ChatListeners(
            onErrorOccurred = { },
            onAddedToRoom = { },
            onRemovedFromRoom = { },
            onCurrentUserReceived = { },
            onNewReadCursor = { },
            onRoomDeleted = { },
            onRoomUpdated = { },
            onPresenceChanged = { u, n, p -> },
            onUserJoinedRoom = { u, r -> },
            onUserLeftRoom = { u, r -> },
            onUserStartedTyping = { u, r -> },
            onUserStoppedTyping = { u, r -> }
        )) { result ->
            when (result) {
                is Result.Success -> {
                    // We have connected!
                    runOnInitThread {
                        val currentUser = result.value
                        AppController.currentUser = currentUser
                        executeMethod()
                        val userJoinedRooms = ArrayList<Room>(currentUser.rooms)
                        for (i in 0 until userJoinedRooms.size) {
                            adapter.addRoom(userJoinedRooms[i])
                        }
                        messageRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }

                is Result.Failure -> {
                    // Failure
                    Log.d("TAG", result.error.toString())
                }
            }
        }
    }

    private fun executeMethod() {
        /*currentUser.getJoinableRooms { result ->
            when (result) {
                is Result.Success -> {
                    // Do something with List<Room>
                    val rooms = result.value
                    runOnUiThread {
                        for (i in 0 until rooms.size) {
                            adapter.addRoom(rooms[i])
                        }
                    }
                }
            }
        }*/

        adapter.setInterface(object : RoomsAdapter.RoomClickedInterface {
            override fun roomSelected(room: Room) {
                if (room.memberUserIds.contains(currentUser.id)) {
                    // user already belongs to this room
                    roomJoined(room)
                } else {
                    currentUser.joinRoom(
                        roomId = room.id,
                        callback = { result ->
                            when (result) {
                                is Result.Success -> {
                                    // Joined the room!
                                    roomJoined(result.value)
                                }
                                is Result.Failure -> {
                                    Log.d("TAG", result.error.toString())
                                }
                            }
                        }
                    )
                }
            }
        })
    }

    private fun roomJoined(room: Room) {
        val intent = Intent(this@RoomsActivity, ChatRoomActivity::class.java)
        intent.putExtra("room_id", room.id)
        intent.putExtra("room_name", room.name)
        startActivity(intent)
    }

    private val handler = Handler()

    private fun runOnInitThread(runnable: () -> Unit) = handler.post(runnable)
}
