package net.melove.demo.chat.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.conversation.messageitem.MLFileMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLImageMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLRecallMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLTextMessageItem;

import java.util.List;


/**
 * Class ${FILE_NAME}
 * <p>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends RecyclerView.Adapter<MLMessageAdapter.MessageViewHolder> {

    // 刷新类型
    private final int MSG_REFRESH_NORMAL = 0;
    private final int MSG_REFRESH_MORE = 1;

    private Context mContext;


    private String mChatId;
    private EMConversation mConversation;
    private List<EMMessage> mMessages;
    private RecyclerView mRecyclerView;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;
    private MLHandler mHandler;

    /**
     * 构造方法
     *
     * @param context      上下文对象，在解析布局的时候需要用到
     * @param chatId       当前会话者的id，为username/groupId
     * @param recyclerView 当前聊天信息展示列表
     */
    public MLMessageAdapter(Context context, String chatId, RecyclerView recyclerView) {
        mContext = context;
        mChatId = chatId;
        mRecyclerView = recyclerView;

        mHandler = new MLHandler();

        /**
         * 初始化会话对象，这里有三个参数么，
         * mChatid 第一个表示会话的当前聊天的 useranme 或者 groupid
         * null 第二个是会话类型可以为空
         * true 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        mMessages = mConversation.getAllMessages();

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * 重写 Adapter 的获取当前 Item 类型的方法（必须重写，同上）
     *
     * @param position 当前 Item 位置
     * @return 当前 Item 的类型
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = mMessages.get(position);
        int itemType = -1;
        switch (message.getType()) {
        case TXT:
            // 判断是否为撤回类型的消息
            if (message.getBooleanAttribute(MLConstants.ML_ATTR_RECALL, false)) {
                itemType = MLConstants.MSG_TYPE_SYS_RECALL;
            } else {
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TEXT_SEND : MLConstants.MSG_TYPE_TEXT_RECEIVED;
            }
            break;
        case IMAGE:
            itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_IMAGE_SEND : MLConstants.MSG_TYPE_IMAGE_RECEIVED;
            break;
        case FILE:
            itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_FILE_SEND : MLConstants.MSG_TYPE_FILE_RECEIVED;
            break;
        default:
            // 默认返回txt类型
            itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TEXT_SEND : MLConstants.MSG_TYPE_TEXT_RECEIVED;
            break;
        }
        return itemType;
    }

    /**
     * 重写RecyclerView.Adapter 创建ViewHolder方法，这里根据消息类型不同创建不同的 itemView 类
     *
     * @param parent   itemView的父控件
     * @param viewType itemView要显示的消息类型
     * @return 返回 自定义的 MessageViewHolder
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder holder = null;
        switch (viewType) {
        /**
         *  SDK默认类型的消息
         */
        // 文字类消息
        case MLConstants.MSG_TYPE_TEXT_SEND:
        case MLConstants.MSG_TYPE_TEXT_RECEIVED:
            holder = new MessageViewHolder(new MLTextMessageItem(mContext, this, viewType));
            break;
        // 图片类消息
        case MLConstants.MSG_TYPE_IMAGE_SEND:
        case MLConstants.MSG_TYPE_IMAGE_RECEIVED:
            holder = new MessageViewHolder(new MLImageMessageItem(mContext, this, viewType));
            break;
        // 正常的文件类消息
        case MLConstants.MSG_TYPE_FILE_SEND:
        case MLConstants.MSG_TYPE_FILE_RECEIVED:
            holder = new MessageViewHolder(new MLFileMessageItem(mContext, this, viewType));
            break;
        /**
         * 自定义类型的消息
         */
        // 回撤类消息
        case MLConstants.MSG_TYPE_SYS_RECALL:
            holder = new MessageViewHolder(new MLRecallMessageItem(mContext, this, viewType));
            break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 获取当前要显示的 message 对象
        EMMessage message = mMessages.get(position);
        /**
         *  调用自定义{@link MLMessageItem#onSetupView(EMMessage)}来填充数据
         */
        ((MLMessageItem) holder.itemView).onSetupView(message, position);

    }

    /**
     * -------------------------------------------------------------------------------------
     * 为RecyclerView 定义点击和长按的事件回调
     */
    /**
     * 设置回调监听
     *
     * @param listener 自定义回调接口
     */
    public void setOnItemClickListener(MLOnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        /**
         * Item 点击及长按事件的处理
         * 这里Item的点击及长按监听都在自定义的
         * {@link net.melove.demo.chat.conversation.messageitem.MLMessageItem}里实现，
         * 然后通过回调将
         * {@link net.melove.demo.chat.conversation.messageitem.MLMessageItem}的操作以自定义 Action
         * 的方式传递过过来，因为聊天列表的 Item 有多种多样的，每一个 Item 弹出菜单不同，
         *
         * @param position 需要操作的Item的位置
         * @param action   Item 触发的动作，比如 点击、复制、转发、删除、撤回等
         */
        public void onItemAction(int position, int action);
    }


    /**
     * Item 项的点击和长按 Action 事件回调
     *
     * @param position 长按的 Item 位置
     * @param action   长按菜单需要处理的动作，比如 复制、转发、删除、撤回等
     */
    public void onItemAction(int position, int action) {
        mOnItemClickListener.onItemAction(position, action);
    }

    /**
     * ---------------------------------------------------------------------------------
     * 自定义Adapter的刷新操作
     *
     * @param position    数据改变的位置
     * @param count       数据改变的数量
     * @param refreshType 刷新类型，是否滚动到指定位置
     */
    public void refresh(int position, int count, int refreshType) {
        Message msg = mHandler.obtainMessage();
        msg.what = refreshType;
        msg.arg1 = position;
        msg.arg2 = count;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    private class MLHandler extends Handler {

        /**
         * 刷新聊天信息列表，并滚动到底部
         */
        private void refresh(int position, int count) {
            mMessages.clear();
            mMessages.addAll(mConversation.getAllMessages());
            notifyItemInserted(position);
            if (mMessages.size() > 1) {
                // 滚动到最后一条
                mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_REFRESH_NORMAL:
                refresh(msg.arg1, msg.arg2);
                break;
            case MSG_REFRESH_MORE:
                refresh(msg.arg1, msg.arg2);
//                refresh(msg.arg1);
                break;
            }
        }
    }

    /**
     * 非静态内部类会隐式持有外部类的引用，就像大家经常将自定义的adapter在Activity类里，
     * 然后在adapter类里面是可以随意调用外部activity的方法的。当你将内部类定义为static时，
     * 你就调用不了外部类的实例方法了，因为这时候静态内部类是不持有外部类的引用的。
     * 声明ViewHolder静态内部类，可以将ViewHolder和外部类解引用。
     * 大家会说一般ViewHolder都很简单，不定义为static也没事吧。
     * 确实如此，但是如果你将它定义为static的，说明你懂这些含义。
     * 万一有一天你在这个ViewHolder加入一些复杂逻辑，做了一些耗时工作，
     * 那么如果ViewHolder是非静态内部类的话，就很容易出现内存泄露。如果是静态的话，
     * 你就不能直接引用外部类，迫使你关注如何避免相互引用。 所以将 ViewHolder 定义为静态的
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
