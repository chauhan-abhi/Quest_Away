package com.abhi.questaway.view.chatui

import android.os.Bundle
import android.util.Log
import com.abhi.questaway.R
import com.abhi.questaway.view.chatui.data.MessagesFixtures
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter

class ChatActivity : DemoMessagesActivity(), MessageInput.InputListener, MessageInput.AttachmentsListener,
    MessageInput.TypingListener {

    private lateinit var messagesList: MessagesList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        this.messagesList = findViewById(R.id.messagesList)
        initAdapter()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setTypingListener(this)
        input.setAttachmentsListener(this)

    }

    private fun initAdapter() {
        super.messagesAdapter = MessagesListAdapter(super.senderId, super.imageLoader)
        super.messagesAdapter.enableSelectionMode(this)
        super.messagesAdapter.setLoadMoreListener(this)
        super.messagesAdapter.registerViewClickListener(
            R.id.messageUserAvatar
        ) { view, message ->
            /*AppUtils.showToast(
                                this@DefaultMessagesActivity,
                                message.getUser().name + " avatar click",
                                false
                            )*/
        }
        this.messagesList.setAdapter(super.messagesAdapter)
    }


    override fun onSelectionChanged(count: Int) {

    }

    override fun onSubmit(input: CharSequence?): Boolean {
        super.messagesAdapter.addToStart(
            MessagesFixtures.getTextMessage(input.toString(), false), true
        )
        return true
    }

    override fun onAddAttachments() {
        super.messagesAdapter.addToStart(
            MessagesFixtures.getImageMessage(), true
        )
    }

    override fun onStartTyping() {
        Log.v("Typing listener", "Start Typing")
    }

    override fun onStopTyping() {
        Log.v("Typing listener", "Stop Typing")
    }


}
