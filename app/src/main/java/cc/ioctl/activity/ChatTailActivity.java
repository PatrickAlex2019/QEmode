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
package cc.ioctl.activity;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitchFriendConfigNext;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.dip2sp;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.logi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import cc.ioctl.dialog.RikkaCustomMsgTimeFormatDialog;
import cc.ioctl.hook.ChatTailHook;
import cc.ioctl.hook.FakeBatteryHook;
import com.tencent.mobileqq.widget.BounceScrollView;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.kyuubiran.util.UtilsKt;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.FriendSelectActivity;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.activity.TroopSelectActivity;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.drawable.HighContrastBorder;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@SuppressLint("Registered")
public class ChatTailActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    public static final String delimiter = "#msg#";
    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_DISABLE = 0x300AFF82;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;
    private static final int R_ID_REGEX_VALUE = 0x300AFF84;
    private static int battery = 0;
    private static String power = "?????????";

    TextView tvStatus;

    private boolean mMsfResponsive = false;
    private TextView __tv_chat_tail_groups, __tv_chat_tail_friends, __tv_chat_tail_time_format;

    public static int getBattery() {
        return battery;
    }

    public static String getPower() {
        if (FakeBatteryHook.INSTANCE.isEnabled()) {
            return FakeBatteryHook.INSTANCE.isFakeBatteryCharging() ? "?????????" : "?????????";
        }
        return power;
    }

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        if (!FakeBatteryHook.INSTANCE.isEnabled()) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            registerReceiver(new BatteryReceiver(), filter);//??????BroadcastReceiver
        }
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(ChatTailActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        int mar = (int) (dip2px(ChatTailActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(ChatTailActivity.this, "?????????????????????????????????????????????"));
        ChatTailHook ct = ChatTailHook.INSTANCE;
        boolean enabled = ct.isEnabled();
        ViewGroup _s;
        LinearLayout _t;
        ll.addView(_t = subtitle(ChatTailActivity.this, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(ChatTailActivity.this, "???????????????????????????????????????\\n"));

        ll.addView(_s = newListItemButton(this, "??????????????????", "????????????????????????????????????", "N/A",
            v -> TroopSelectActivity.startToSelectTroopsAndSaveToExfMgr(ChatTailActivity.this,
                ConfigItems.qn_chat_tail_troops, "????????????????????????")));
        ll.addView(_s = newListItemButton(this, "?????????????????????", "???????????????????????????????????????", "N/A",
            v -> FriendSelectActivity.startToSelectFriendsAndSaveToExfMgr(ChatTailActivity.this,
                ConfigItems.qn_chat_tail_friends, "???????????????????????????")));
        ll.addView(_s = newListItemButton(this, "??????????????????", "??????QN?????????Q???\"??????????????????????????????\"?????????",
            RikkaCustomMsgTimeFormatDialog.getTimeFormat(),
            view -> Toasts.info(ChatTailActivity.this, "??????QN?????????Q???\"??????????????????????????????\"?????????")));
        ll.addView(subtitle(ChatTailActivity.this, "???????????????"));
        ll.addView(subtitle(ChatTailActivity.this, "????????????(??????????????????): "));
        LinearLayout _a, _b, _c, _d, _e, _f, _g, _h;
        ll.addView(_a = subtitle(ChatTailActivity.this, delimiter + "         : ????????????"));
        ll.addView(_b = subtitle(ChatTailActivity.this, "#model#   : ????????????"));
        ll.addView(_c = subtitle(ChatTailActivity.this, "#brand#   : ????????????"));
        ll.addView(_d = subtitle(ChatTailActivity.this, "#battery# : ????????????"));
        ll.addView(_e = subtitle(ChatTailActivity.this, "#power#   : ??????????????????"));
        ll.addView(_f = subtitle(ChatTailActivity.this, "#time#    : ????????????"));
        ll.addView(_g = subtitle(ChatTailActivity.this, "#Spacemsg#    : ????????????"));
        ll.addView(_h = subtitle(ChatTailActivity.this, "\\n       : ??????"));
        int _5dp = dip2px(ChatTailActivity.this, 5);
        EditText pct = createEditText(R_ID_PERCENT_VALUE, _5dp,
            ct.getTailCapacity().replace("\n", "\\n"),
            ChatTailActivity.delimiter + " ????????????????????????");
        _a.setOnClickListener(v -> pct.setText(pct.getText() + delimiter));
        _b.setOnClickListener(v -> pct.setText(pct.getText() + "#model#"));
        _c.setOnClickListener(v -> pct.setText(pct.getText() + "#brand#"));
        _d.setOnClickListener(v -> pct.setText(pct.getText() + "#battery#"));
        _e.setOnClickListener(v -> pct.setText(pct.getText() + "#power#"));
        _f.setOnClickListener(v -> pct.setText(pct.getText() + "#time#"));
        _g.setOnClickListener(v -> pct.setText(pct.getText() + "#Spacemsg#"));
        _h.setOnClickListener(v -> pct.setText(pct.getText() + "\\n"));
        ll.addView(pct,
            newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        ll.addView(newListItemSwitchFriendConfigNext(this, "????????????",
            "???????????????????????????????????????????????????(????????????" + HostInfo.getHostInfo().getHostName() + ")",
            ConfigItems.qn_chat_tail_regex, false));
        ll.addView(createEditText(R_ID_REGEX_VALUE, _5dp, ChatTailHook.getTailRegex(),
            "????????????????????????????????????(????????????)"),
            newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT,
                2 * _5dp, _5dp, 2 * _5dp, _5dp));
        ll.addView(newListItemSwitchFriendConfigNext(this, "????????????",
            "???????????????????????????(????????????" + HostInfo.getHostInfo().getHostName() + ")",
            ConfigItems.qn_chat_tail_global, false));
        Button apply = new Button(ChatTailActivity.this);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply,
            newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button dis = new Button(ChatTailActivity.this);
        dis.setId(R_ID_DISABLE);
        dis.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(dis);
        dis.setText("??????");
        ll.addView(dis,
            newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        showStatus();
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("???????????????");
        return true;
    }

    private EditText createEditText(int id, int _5dp, String text, String hint) {
        EditText pct = new EditText(ChatTailActivity.this);
        pct.setId(id);
        pct.setInputType(TYPE_CLASS_TEXT);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(ChatTailActivity.this, 18));
        ViewCompat.setBackground(pct, null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        ViewCompat.setBackground(pct, new HighContrastBorder());
        pct.setHint(hint);
        pct.setText(text);
        pct.setSelection(pct.getText().length());
        //??????????????????????????????????????????????????????????????????????????????Android?????????????????????????????????
        return pct;
    }

    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        String str = cfg.getString(ConfigItems.qn_chat_tail_troops);
        int n = 0;
        if (str != null && str.length() > 4) {
            n = str.split(",").length;
        }
        __tv_chat_tail_groups.setText(n + "??????");
        str = cfg.getString(ConfigItems.qn_chat_tail_friends);
        n = 0;
        if (str != null && str.length() > 4) {
            n = str.split(",").length;
        }
        __tv_chat_tail_friends.setText(n + "?????????");
    }

    private void showStatus() {
        ChatTailHook ct = ChatTailHook.INSTANCE;
        boolean enabled = ct.isEnabled();
        String desc = "????????????: ";
        if (enabled) {
            if (!ct.isRegex() || !ct.isPassRegex("????????????")) {
                desc += "?????????: \n" + ct.getTailCapacity()
                    .replace(ChatTailActivity.delimiter, "????????????")
                    .replace("#model#", Build.MODEL)
                    .replace("#brand#", Build.BRAND)
                    .replace("#battery#", battery + "")
                    .replace("#power#", ChatTailActivity.getPower())
                    .replace("#time#",
                        new SimpleDateFormat(RikkaCustomMsgTimeFormatDialog.getTimeFormat())
                            .format(new Date()));
                if (desc.contains("#Spacemsg#")) {
                    desc = desc.replace("#Spacemsg#", "");
                    desc = UtilsKt.makeSpaceMsg(desc);
                }
            } else {
                desc += "?????????: \n????????????";
            }
        } else {
            desc += "??????";
        }
        tvStatus.setText(desc);
        Button apply, disable;
        apply = ChatTailActivity.this.findViewById(R_ID_APPLY);
        disable = ChatTailActivity.this.findViewById(R_ID_DISABLE);
        if (!enabled) {
            apply.setText("???????????????");
        } else {
            apply.setText("??????");
        }
        if (!enabled) {
            disable.setVisibility(View.GONE);
        } else {
            disable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        switch (v.getId()) {
            case R_ID_APPLY:
                doUpdateTailCfg();
                logi("isRegex:" + String.valueOf(ChatTailHook.isRegex()));
                logi("isPassRegex:" + String.valueOf(ChatTailHook.isPassRegex("????????????")));
                logi("getTailRegex:" + ChatTailHook.getTailRegex());
                break;
            case R_ID_DISABLE:
                cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, false);
                try {
                    cfg.save();
                } catch (Exception e) {
                    Toasts.error(ChatTailActivity.this, "??????:" + e.toString(), Toast.LENGTH_LONG);
                    log(e);
                }
                showStatus();
        }
    }

    private void doUpdateTailCfg() {
        ChatTailHook ct = ChatTailHook.INSTANCE;
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        EditText pct;
        pct = ChatTailActivity.this.findViewById(R_ID_PERCENT_VALUE);
        String val = pct.getText().toString();
        if (Utils.isNullOrEmpty(val)) {
            Toasts.error(ChatTailActivity.this, "??????????????????");
            return;
        }
        if (!val.contains(ChatTailActivity.delimiter)) {
            Toasts.error(ChatTailActivity.this, "????????????????????????" + ChatTailActivity.delimiter + "");
            return;
        }
        ct.setTail(val);
        val = ((EditText) ChatTailActivity.this.findViewById(R_ID_REGEX_VALUE)).getText()
            .toString();
        if (!Utils.isNullOrEmpty(val)) {
            ct.setTailRegex(val);
        }
        if (!ct.isEnabled()) {
            cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, true);
            try {
                cfg.save();
            } catch (Exception e) {
                Toasts.error(ChatTailActivity.this, "??????:" + e.toString(), Toast.LENGTH_LONG);
                log(e);
            }
        }
        showStatus();
    }

    private static class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_BATTERY_CHANGED: {
                    int current = intent.getExtras().getInt("level");//??????????????????
                    int total = intent.getExtras().getInt("scale");//???????????????
                    int percent = current * 100 / total;
                    ChatTailActivity.battery = percent;
                }
                case Intent.ACTION_POWER_DISCONNECTED: {
                    ChatTailActivity.power = "?????????";
                }
                case Intent.ACTION_POWER_CONNECTED: {
                    ChatTailActivity.power = "?????????";
                }
            }

        }
    }

}
