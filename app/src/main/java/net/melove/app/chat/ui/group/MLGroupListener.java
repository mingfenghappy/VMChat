package net.melove.app.chat.ui.group;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import net.melove.app.chat.util.MLLog;

/**
 * Created by lzan13 on 2016/10/26.
 * 群组监听详细处理类
 */
public class MLGroupListener implements EMGroupChangeListener {

    /**
     * 收到其他用户邀请加入群组
     *
     * @param groupId 要加入的群的id
     * @param groupName 要加入的群的名称
     * @param inviter 邀请者
     * @param reason 邀请理由
     */
    @Override public void onInvitationReceived(String groupId, String groupName, String inviter,
            String reason) {
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
    }

    /**
     * 用户申请加入群组
     *
     * @param groupId 要加入的群的id
     * @param groupName 要加入的群的名称
     * @param applyer 申请人的username
     * @param reason 申请加入的reason
     */
    @Override public void onApplicationReceived(String groupId, String groupName, String applyer,
            String reason) {
        MLLog.i("onApplicationAccept groupId:%s, groupName:%, reason:%s", groupId, groupName,
                reason);
    }

    /**
     * 加群申请被对方接受
     *
     * @param groupId 申请加入的群组id
     * @param groupName 申请加入的群组名称
     * @param accepter 同意申请的用户名（一般就是群主）
     */
    @Override public void onApplicationAccept(String groupId, String groupName, String accepter) {
        MLLog.i("onApplicationAccept groupId:%s, groupName:%, accepter:%s", groupId, groupName,
                accepter);
    }

    /**
     * 加群申请被拒绝
     *
     * @param groupId 申请加入的群组id
     * @param groupName 申请加入的群组名称
     * @param decliner 拒绝者的用户名（一般就是群主）
     * @param reason 拒绝理由
     */
    @Override public void onApplicationDeclined(String groupId, String groupName, String decliner,
            String reason) {
        MLLog.i("onApplicationDeclined groupId:%s, decliner:%, sreason:%s", groupId, decliner,
                reason);
    }

    /**
     * 对方接受群组邀请
     *
     * @param groupId 邀请对方加入的群组
     * @param invitee 被邀请者
     * @param reason 理由
     */
    @Override public void onInvitationAccepted(String groupId, String invitee, String reason) {
        MLLog.i("onInvitationAccepted groupId:%s, invitee:%, sreason:%s", groupId, invitee, reason);
    }

    /**
     * 对方拒绝群组邀请
     *
     * @param groupId 邀请对方加入的群组
     * @param invitee 被邀请的人（拒绝群组邀请的人）
     * @param reason 拒绝理由
     */
    @Override public void onInvitationDeclined(String groupId, String invitee, String reason) {
        MLLog.i("onInvitationDeclined groupId:%s, invitee:%, sreason:%s", groupId, invitee, reason);
    }

    /**
     * 当前登录用户被管理员移除出群组
     *
     * @param groupId 被移出的群组id
     * @param groupName 被移出的群组名称
     */
    @Override public void onUserRemoved(String groupId, String groupName) {
        MLLog.i("onUserRemoved groupId:%s, groupName:%s", groupId, groupName);
    }

    /**
     * 群组被解散。 sdk 会先删除本地的这个群组，之后通过此回调通知应用，此群组被删除了
     *
     * @param groupId 解散的群组id
     * @param groupName 解散的群组名称
     */
    @Override public void onGroupDestroyed(String groupId, String groupName) {
        MLLog.i("onGroupDestroyed groupId:%s, groupName:%s", groupId, groupName);
    }

    /**
     * 自动同意加入群组 sdk会先加入这个群组，并通过此回调通知应用
     *
     * @param groupId 收到邀请加入的群组id
     * @param inviter 邀请者
     * @param inviteMessage 邀请信息
     */
    @Override public void onAutoAcceptInvitationFromGroup(String groupId, String inviter,
            String inviteMessage) {
        MLLog.i("onAutoAcceptInvitationFromGroup groupId:%s, inviter:%s, inviteMessage:%s", groupId,
                inviter, inviteMessage);
    }
}