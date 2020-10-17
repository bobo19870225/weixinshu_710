package com.jinshu.weixinbook.widget;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jinshu.weixinbook.R;
import com.jinshu.weixinbook.utils.LogUtil;
import com.jinshu.weixinbook.utils.StringUtils;

import java.util.LinkedList;


/**
 * Created by laidayuan on 16/1/8.
 * 修改lsb
 */
public class ActionBar extends RelativeLayout implements View.OnClickListener {

    private static RelativeLayout rl_next;
    private static TextView action_next;
    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView mLogoView;
    private static ImageView mBackIndicator;
    //private View mHomeView;
    private static TextView mTitleView;
    private LinearLayout mActionsView;
    private RelativeLayout mHomeLayout;
    private ProgressBar mProgress;
    private static RelativeLayout rl_back;

    public ActionBar(Context context, AttributeSet attrs, int defStyleAttr, LinearLayout mActionsView) {
        super(context, attrs, defStyleAttr);
        this.mActionsView = mActionsView;
    }

    public ActionBar(Context context, AttributeSet attrs, LinearLayout mActionsView) {
        super(context, attrs);
        this.mActionsView = mActionsView;
    }


    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.layout_actionbar, null);
        addView(mBarView);
        mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
//        mBackIndicator = (ImageView) mBarView.findViewById(R.id.actionbar_home_is_back);
//        rl_back = (RelativeLayout) mBarView.findViewById(R.id.rl_back);
        rl_next = (RelativeLayout) mBarView.findViewById(R.id.rl_next);
        action_next = (TextView) mBarView.findViewById(R.id.action_next);
        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);

        /*CharSequence title = a.getString(R.styleable.ActionBar_title);
        if (title != null) {
            setTitle(title);
        }*/
        //CharSequence title = a.getString(R.styleable.ActionBar_title);
        // if (title != null) {
        // setTitle(title);
        // }

        a.recycle();
    }

    public static RelativeLayout setBack() {
        mBackIndicator.setVisibility(VISIBLE);
        mBackIndicator.setImageResource(R.mipmap.ic_back);
        return rl_back;
    }

    public static RelativeLayout setNext(String i) {
        rl_next.setVisibility(VISIBLE);
        action_next.setText(i);
        return rl_next;
    }

    public static void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setBackgroundColor(String color) {
        if (StringUtils.isNotEmpty(color)) {
            setBackgroundColor(Color.parseColor(color));
        }

    }

    public void setHomeAction(final Action action) {

        if (action.getText() != null) {
            TextView labelView =
                    (TextView) mHomeLayout.findViewById(R.id.actionbar_home_text);
            labelView.setText(action.getText());
            labelView.setVisibility(View.VISIBLE);
            labelView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    action.performAction(view);
                }
            });
        } else {
            ImageButton labelView =
                    (ImageButton) mHomeLayout.findViewById(R.id.actionbar_home_btn);
            labelView.setImageResource(action.getDrawable());
            labelView.setVisibility(View.VISIBLE);
            labelView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    action.performAction(view);
                }
            });
        }

        //mHomeLayout.setTag(action);
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    private ActionBar actionBar;

    public ActionBar getActionBar() {
        if (actionBar == null) {
            View barView = findViewById(R.id.actionbar);
            if (barView instanceof ActionBar) {
                actionBar = (ActionBar) barView;

            }

        }

        if (actionBar == null) {
            LogUtil.d("actionBar == null --- -----");
        }

        return actionBar;
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the provided logo to the left in the action bar.
     * <p>
     * This is meant to be used instead of the setHomeAction and does not draw
     * a divider to the left of the provided logo.
     *
     * @param resId The drawable resource id
     */
    public void setHomeLogo(int resId) {
        mLogoView.setImageResource(resId);
        mLogoView.setVisibility(View.VISIBLE);
        mHomeLayout.setVisibility(View.GONE);
    }

    /* Emulating Honeycomb, setdisplayHomeAsUpEnabled takes a boolean
     * and toggles whether the "home" view should have a little triangle
     * indicating "up" */
    public void setDisplayHomeAsUpEnabled(boolean show) {
        mBackIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitleColor(int color) {
        mTitleView.setTextColor(color);
    }

    public void setTitleColor(String color) {
        if (StringUtils.isNotEmpty(color)) {
            setBackgroundColor(Color.parseColor(color));
        }
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    /**
     * Set the enabled state of the progress bar.
     */
    public void setProgressBarVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    /**
     * Returns the visibility status for the progress bar.
     */
    public int getProgressBarVisibility() {
        return mProgress.getVisibility();
    }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     *
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action the action to add
     */
    public View addAction(Action action) {
        final int index = mActionsView.getChildCount();
        return addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action the action to add
     * @param index  the position at which to add the action
     */
    public View addAction(Action action, int index) {
        View actionAview = inflateAction(action);
        mActionsView.addView(actionAview, index);

        return actionAview;
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                Object tag = view.getTag();
                View button = view.findViewById(R.id.actionbar_item_btn);
                if (tag == null && button != null) {
                    tag = button.getTag();
                }

                if (tag instanceof Action && tag.equals(action)) {

                    mActionsView.removeView(view);

                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     *
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.layout_actionbar_item, mActionsView, false);

        if (action.getText() != null) {
            TextView labelView =
                    (TextView) view.findViewById(R.id.actionbar_item_text);
            labelView.setText(action.getText());
            labelView.setVisibility(View.VISIBLE);
            labelView.setTextColor(action.getTextColor());
            view.setBackgroundResource(action.getBackgroundResource());
            view.setTag(action);
            view.setOnClickListener(this);
        } else {
            ImageButton labelView =
                    (ImageButton) view.findViewById(R.id.actionbar_item_btn);
            labelView.setImageResource(action.getDrawable());
            labelView.setVisibility(View.VISIBLE);
            view.setBackgroundResource(action.getBackgroundResource());
            labelView.setTag(action);
            labelView.setOnClickListener(this);
        }
        return view;
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        int getDrawable();

        String getText();

        int getTextColor();

        int getBackgroundResource();

        void performAction(View view);
    }


    public static class IntentAction extends AbstractAction {
        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
            mContext = context;
            mIntent = intent;
        }

        @Override
        public int getBackgroundResource() {
            return R.color.theme_color;
        }

        @Override
        public void performAction(View view) {
            try {
                mContext.startActivity(mIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext,
                        mContext.getText(R.string.actionbar_activity_not_found),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
