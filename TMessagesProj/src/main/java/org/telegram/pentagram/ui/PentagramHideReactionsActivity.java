/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.pentagram.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.pentagram.data.PentagramProvider;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FiltersSetupActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("NotifyDataSetChanged")
public class PentagramHideReactionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate{

    public static final int ROW_TYPE_HEADER = 0;
    public static final int ROW_TYPE_SWITCH = 1;
    public static final int ROW_TYPE_SHADOW = 3;

    private static final int ID_HIDE_PRIVATE_CHATS = 635;
    private static final int ID_HIDE_GROUPS = 636;
    private static final int ID_HIDE_CHANNELS = 637;
    private static final int ID_HIDE_BOTS = 638;

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private LinearLayoutManager layoutManager;

    private int rowsCount = 0;
    private final int rowHeader = rowsCount++;
    private final int rowHidePrivateChats = rowsCount++;
    private final int rowHideGroups = rowsCount++;
    private final int rowHideChannels = rowsCount++;
    private final int rowHideBots = rowsCount++;
    private final int rowShadow = rowsCount++;

    public PentagramHideReactionsActivity() {
        super(new Bundle());
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
    }

    @Override
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        super.onTransitionAnimationProgress(isOpen, progress);
        if(fragmentView!=null) {
            fragmentView.invalidate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public View createView(Context context) {

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("PentagramHideReactions", R.string.PentagramHideReactions));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){

            @Override
            public void onItemClick(int id) {
                if(id==-1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter = new ListAdapter(context));
        listView.setOnItemClickListener((view, position, x, y)->{

            Object tag = view.getTag();
            if(tag!=null) {
                switch((int) tag) {
                    case ID_HIDE_PRIVATE_CHATS:
                        TextCheckCell checkCell = (TextCheckCell) view;
                        PentagramProvider.setHideReactionsPrivateChats(!checkCell.isChecked());
                        checkCell.setChecked(!checkCell.isChecked());
                        break;
                    case ID_HIDE_GROUPS:
                        checkCell = (TextCheckCell) view;
                        PentagramProvider.setHideReactionsGroups(!checkCell.isChecked());
                        checkCell.setChecked(!checkCell.isChecked());
                        break;
                    case ID_HIDE_CHANNELS:
                        checkCell = (TextCheckCell) view;
                        PentagramProvider.sethideReactionsChannels(!checkCell.isChecked());
                        checkCell.setChecked(!checkCell.isChecked());
                        break;
                    case ID_HIDE_BOTS:
                        checkCell = (TextCheckCell) view;
                        PentagramProvider.setHideReactionsBots(!checkCell.isChecked());
                        checkCell.setChecked(!checkCell.isChecked());
                        break;
                }
            }
        });

        listView.setVerticalScrollBarEnabled(false);
        listView.setFastScrollVisible(false);

        return fragmentView;
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(actionBar!=null) {
            actionBar.closeSearchField();
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter{

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            switch(holder.getItemViewType()) {
                case ROW_TYPE_SWITCH:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public int getItemCount() {
            return rowsCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch(viewType) {
                case ROW_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ROW_TYPE_SWITCH:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case ROW_TYPE_SHADOW:
                default:
                    view = new ShadowSectionCell(mContext);
                    break;
            }

            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            switch(holder.getItemViewType()) {
                case ROW_TYPE_HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if(position==rowHeader) {
                        headerCell.setText(LocaleController.getString("PentagramHideReactions", R.string.PentagramHideReactions));
                    }
                    break;
                }
                case ROW_TYPE_SWITCH: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if(position==rowHidePrivateChats) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("PentagramHideReactionsPrivateChats", R.string.PentagramHideReactionsPrivateChats),
                                PentagramProvider.isHideReactionsPrivateChats(), false);
                        holder.itemView.setTag(ID_HIDE_PRIVATE_CHATS);
                    } else if(position==rowHideGroups) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("PentagramHideReactionsGroups", R.string.PentagramHideReactionsGroups),
                                PentagramProvider.isHideReactionsGroups(), false);
                        holder.itemView.setTag(ID_HIDE_GROUPS);
                    } else if(position==rowHideChannels) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("PentagramHideReactionsChannels", R.string.PentagramHideReactionsChannels),
                                PentagramProvider.isHideReactionsChannels(), false);
                        holder.itemView.setTag(ID_HIDE_CHANNELS);
                    } else if(position==rowHideBots) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("PentagramHideReactionsBots", R.string.PentagramHideReactionsBots),
                                PentagramProvider.isHideReactionsBots(), false);
                        holder.itemView.setTag(ID_HIDE_BOTS);
                    }
                    break;
                }
                case ROW_TYPE_SHADOW: {
                    holder.itemView.setBackground(Theme.getThemedDrawableByKey(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position==rowHeader) {
                return ROW_TYPE_HEADER;
            } else if(position==rowShadow) {
                return ROW_TYPE_SHADOW;
            } else {
                return ROW_TYPE_SWITCH;
            }
        }

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        return themeDescriptions;
    }

    public class KeywordCell extends FrameLayout{

        private final SimpleTextView valueTextView;
        @SuppressWarnings("FieldCanBeLocal")
        private final ImageView optionsImageView;
        private boolean needDivider;

        private String currentFilter;

        public KeywordCell(Context context) {
            super(context);
            setWillNotDraw(false);

            boolean isRTL = LocaleController.isRTL;
            int paddingStart = AndroidUtilities.dp(21);
            int paddingEnd = AndroidUtilities.dp(56);
            int paddingLeft = isRTL ? paddingEnd : paddingStart;
            int paddingRight = isRTL ? paddingStart : paddingEnd;

            LayoutParams params = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    (isRTL ? Gravity.RIGHT : Gravity.LEFT)|Gravity.CENTER_VERTICAL,
                    0, 0, 0, 0);

            valueTextView = new SimpleTextView(context);
            valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            valueTextView.setTextSize(16);
            valueTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)|Gravity.CENTER_VERTICAL);
            valueTextView.setMaxLines(1);
            valueTextView.setPadding(paddingLeft, 0, paddingRight, 0);
            addView(valueTextView, params);
            valueTextView.setVisibility(VISIBLE);

            optionsImageView = new ImageView(context);
            optionsImageView.setFocusable(false);
            optionsImageView.setScaleType(ImageView.ScaleType.CENTER);
            optionsImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            optionsImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            optionsImageView.setImageResource(R.drawable.msg_actions);
            optionsImageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            addView(optionsImageView, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT)|Gravity.CENTER_VERTICAL, 6, 0, 6, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50), MeasureSpec.EXACTLY));
        }

        public void setFilter(String filter, boolean divider) {
            currentFilter = filter;

            valueTextView.setText(filter);
            needDivider = divider;

            invalidate();
        }

        public String getCurrentFilter() {
            return currentFilter;
        }

        public void setOnOptionsClick(OnClickListener listener) {
            optionsImageView.setOnClickListener(listener);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(16), getMeasuredHeight()-1, getMeasuredWidth()-(LocaleController.isRTL ? AndroidUtilities.dp(62) : 0), getMeasuredHeight()-1, Theme.dividerPaint);
            }
        }
    }


    public class DialogItem extends FrameLayout{

        private TextView text;
        private ImageView icon;

        private int textColor;
        private int iconColor;
        private int selectorColor;

        public DialogItem(Context context, int iconRes, String message, boolean isRed, Runnable onClickListener) {
            super(context);

            if(isRed) {
                iconColor = getThemedColor(Theme.key_text_RedRegular);
                textColor = getThemedColor(Theme.key_text_RedRegular);
                selectorColor = Theme.multAlpha(Theme.getColor(Theme.key_text_RedRegular), .12f);
            } else {
                textColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItem);
                iconColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon);
                selectorColor = getThemedColor(Theme.key_dialogButtonSelector);
            }

            updateBackground();

            setPadding(AndroidUtilities.dp(24), 0, AndroidUtilities.dp(24), 0);

            icon = new ImageView(context);
            icon.setImageResource(iconRes);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
            addView(icon, LayoutHelper.createFrame(24, 24, Gravity.CENTER_VERTICAL|(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)));

            text = new TextView(context);
            text.setLines(1);
            text.setSingleLine(true);
            text.setGravity(Gravity.LEFT);
            text.setEllipsize(TextUtils.TruncateAt.END);
            text.setTextColor(textColor);
            text.setText(message);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            text.setPadding(AndroidUtilities.dp(44), 0, AndroidUtilities.dp(44), 0);
            addView(text, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)|Gravity.CENTER_VERTICAL));

            setOnClickListener(v->{
                onClickListener.run();
            });
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
        }

        void updateBackground() {
            setBackground(Theme.createRadSelectorDrawable(selectorColor, 0, 0));
        }
    }

}
