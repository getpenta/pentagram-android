/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.pentagram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.pentagram.data.PentagramProvider;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class PentagramStopWordEditorActivity extends BaseFragment{

    private TextView counter;
    private EditTextBoldCursor firstNameField;
    private View headerLabelView;
    private View doneButton;

    private Theme.ResourcesProvider resourcesProvider;

    private final static int done_button = 1;

    private String initKeyword = "";
    private String currentKeyword = "";

    public PentagramStopWordEditorActivity(Theme.ResourcesProvider resourcesProvider, String keyword) {
        this.resourcesProvider = resourcesProvider;
        this.initKeyword = keyword;
        this.currentKeyword = keyword;
    }

    @Override
    public View createView(Context context) {
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue, resourcesProvider), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon, resourcesProvider), false);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("PentagramStopWord", R.string.PentagramStopWord));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){

            @Override
            public void onItemClick(int id) {
                if(id==-1) {
                    finishFragment();
                } else if(id==done_button) {
                    if(firstNameField.getText().length()!=0) {
                        saveWord();
                        finishFragment();
                    }
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_ab_done, AndroidUtilities.dp(56), LocaleController.getString("Done", R.string.Done));

        /*TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
        if (user == null) {
            user = UserConfig.getInstance(currentAccount).getCurrentUser();
        }*/

        FrameLayout layout = new FrameLayout(context);
        fragmentView = layout;
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
        //fragmentView.setOnTouchListener((v, event)->true);

        firstNameField = new EditTextBoldCursor(context){

            @Override
            protected Theme.ResourcesProvider getResourcesProvider() {
                return resourcesProvider;
            }
        };
        firstNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText, resourcesProvider));
        firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        firstNameField.setBackgroundDrawable(null);
        firstNameField.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_text_RedRegular));
        firstNameField.setMaxLines(1);
        firstNameField.setLines(1);
        firstNameField.setSingleLine(true);
        firstNameField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        firstNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        firstNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        firstNameField.setHint(LocaleController.getString("PentagramStopWord", R.string.PentagramStopWord));
        firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        firstNameField.setCursorSize(AndroidUtilities.dp(20));
        firstNameField.setCursorWidth(1.5f);
        firstNameField.setText(initKeyword);
        firstNameField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
        firstNameField.setSelection(initKeyword.length());
        firstNameField.setPadding(0, 0, AndroidUtilities.dp(32), 0);
        layout.addView(firstNameField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36,
                (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)|Gravity.TOP,
                24, 24, 24, 0));

        firstNameField.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentKeyword = s.toString();
                updateDoneButton();
                updateCounter();
            }
        });

        int counterColor = Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), .50f);

        counter = new TextView(context);
        counter.setLines(1);
        counter.setSingleLine(true);
        counter.setGravity(Gravity.CENTER_VERTICAL);
        counter.setEllipsize(TextUtils.TruncateAt.END);
        counter.setTextColor(counterColor);
        counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        counter.setPadding(0, 0, 0, AndroidUtilities.dp(8));
        layout.addView(counter, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 36,
                (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT)|Gravity.TOP,
                24, 24, 24, 0));

        updateDoneButton();
        updateCounter();

        return fragmentView;
    }

    private void saveWord() {
        if(TextUtils.isEmpty(initKeyword)) {
            PentagramProvider.addKeyword(currentKeyword);
        } else {
            PentagramProvider.updateKeyword(initKeyword, currentKeyword);
        }
    }

    private void updateCounter() {
        counter.setText(String.valueOf(120-currentKeyword.length()));
    }

    private void updateDoneButton() {
        if(!TextUtils.isEmpty(currentKeyword) && !TextUtils.equals(currentKeyword, initKeyword)) {
            doneButton.setVisibility(View.VISIBLE);
        } else {
            doneButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if(!animations) {
            firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(firstNameField);
        }
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return resourcesProvider;
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if(isOpen) {
            AndroidUtilities.runOnUIThread(()->{
                if(firstNameField!=null) {
                    firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(firstNameField);
                }
            }, 100);
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER|ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));

        return themeDescriptions;
    }
}
