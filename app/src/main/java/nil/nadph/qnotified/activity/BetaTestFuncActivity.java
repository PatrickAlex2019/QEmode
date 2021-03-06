/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.clickToProxyActAction;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemConfigSwitchIfValid;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitchConfig;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cc.ioctl.activity.ChatTailActivity;
import cc.ioctl.activity.ManageScriptsActivity;
import cc.ioctl.hook.ChatTailHook;
import cc.ioctl.hook.MutePokePacket;
import cc.ioctl.hook.PttForwardHook;
import cc.ioctl.script.QNScriptManager;
import com.tencent.mobileqq.widget.BounceScrollView;
import me.kyuubiran.dialog.RevokeMsgDialog;
import me.kyuubiran.hook.RemoveDiyCard;
import me.kyuubiran.hook.RemovePokeGrayTips;
import me.kyuubiran.hook.RemoveRedDot;
import me.kyuubiran.hook.testhook.CutMessage;
import me.singleneuron.qn_kernel.data.HostInfo;
import me.singleneuron.qn_kernel.ui.qq_item.ListItemButton;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;
import xyz.nextalone.hook.CollapseTroopMessage;

@SuppressLint("Registered")
public class BetaTestFuncActivity extends IphoneTitleBarActivityCompat {

    ListItemButton __tv_chat_tail_status;
    ListItemButton __js_status;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        if (!LicenseStatus.isBlacklisted()) {
            super.doOnCreate(bundle);
            ListItemButton _t;
            String _hostName = HostInfo.getHostInfo().getHostName();
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            LinearLayout __ll = new LinearLayout(this);
            __ll.setOrientation(LinearLayout.VERTICAL);
            ViewGroup bounceScrollView = new BounceScrollView(this, null);
            bounceScrollView.setLayoutParams(mmlp);
            bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT,
                dip2px(this, 48));
            RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT,
                WRAP_CONTENT);
            int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
            __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
            ll.addView(subtitle(this,
                "Beta???????????? ????????????????????????[???????????????BUG ????????????????????????????????????" + _hostName + "??????????????????" + _hostName
                    + "?????? ???????????????]"));
            ll.addView(newListItemSwitchConfig(this, "????????????", "?????????????????????????????????????????????",
                PttForwardHook.qn_enable_ptt_save, false));
            ll.addView(
                newListItemConfigSwitchIfValid(this, "????????????????????????", "???????????????",
                    CollapseTroopMessage.INSTANCE));
            ll.addView(newListItemHookSwitchInit(this, "???????????????", "????????????", RemoveRedDot.INSTANCE));
            ll.addView(_t = newListItemButton(this, "????????????????????????", "?????????????????????", "N/A",
                clickToProxyActAction(ChatTailActivity.class)));
            __tv_chat_tail_status = _t;
            ll.addView(newListItemHookSwitchInit(this, "???????????????", "OvO", MutePokePacket.INSTANCE));
            ll.addView(
                newListItemHookSwitchInit(this, "???LogCat???????????????????????????", "[Debug]??????????????????????????? ??????????????????",
                    CutMessage.INSTANCE));
            ListItemButton __t;
            ll.addView(__t = newListItemButton(this, "????????????(.java)", "???????????????, ????????????", "N/A",
                clickToProxyActAction(ManageScriptsActivity.class)));
            __js_status = __t;

            View v = subtitle(this, "?????????????????????");
            v.setOnClickListener(v1 -> RevokeMsgDialog.INSTANCE.onShow(this));
            ll.addView(v);
            ll.addView(newListItemHookSwitchInit(this, "[??????]?????????????????????", "??????????????????????????????",
                RemovePokeGrayTips.INSTANCE));
            ll.addView(newListItemHookSwitchInit(this, "[?????????]??????????????????diy??????", "?????????/zip???????????????????????????",
                RemoveDiyCard.INSTANCE));

            __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;

        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("Beta???????????????");}
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = ChatTailHook.INSTANCE.isEnabled() ? ChatTailHook.INSTANCE.getTailCapacity()
            .replace("\n", "") : null;
        if (text != null && text.length() > 3) {
            // ????????????????????????
            text = "..." + text.substring(text.length() - 3);
        }
        if (text == null) {
            text = "[?????????]";
        }
        if (__tv_chat_tail_status != null) {
            __tv_chat_tail_status.setValue(text);
        }
        if (__js_status != null) {
            __js_status
                .setValue(QNScriptManager.getEnableCount() + "/" + QNScriptManager.getAllCount());
        }
    }
}
